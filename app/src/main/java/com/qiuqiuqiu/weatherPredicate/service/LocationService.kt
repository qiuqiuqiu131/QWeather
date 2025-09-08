package com.qiuqiuqiu.weatherPredicate.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface ILocationService {
    suspend fun getLastLocation(): Location?
    fun isLocationEnabled(): Boolean
    fun hasLocationPermissions(): Boolean
}

fun hasLocationPermissions(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

class LocationService @Inject constructor(@ApplicationContext private val context: Context) :
    ILocationService {

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // 保存最后一次获取到的位置
    private var lastKnownLocation: Location? = null

    // 位置监听器
    private val locationListener =
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                lastKnownLocation = location
                locationManager.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

    init {
        // 检查权限并尝试获取最后一次已知位置
        if (hasLocationPermissions()) {
            getCachedLastKnownLocation()
        }
    }

    override fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCachedLastKnownLocation() {
        // 尝试从不同的定位提供商获取最后一次已知位置
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)

        providers.forEach { provider ->
            if (locationManager.isProviderEnabled(provider)) {
                try {
                    val location = locationManager.getLastKnownLocation(provider)
                    if (location != null && isLocationValid(location)) {
                        lastKnownLocation = location
                        return
                    }
                } catch (e: SecurityException) {
                    // 权限异常，忽略
                }
            }
        }
    }

    private fun isLocationValid(location: Location): Boolean {
        // 检查位置是否太旧（超过5分钟）
        val isRecent = System.currentTimeMillis() - location.time < 5 * 60 * 1000
        // 检查位置是否有有效的经纬度
        val hasValidCoordinates = location.latitude != 0.0 || location.longitude != 0.0
        return isRecent && hasValidCoordinates
    }

    override fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override suspend fun getLastLocation(): Location? = suspendCancellableCoroutine { cont ->
        if (!hasLocationPermissions()) {
            cont.resumeWithException(SecurityException("Location permissions not granted"))
            return@suspendCancellableCoroutine
        }

        if (!isLocationEnabled()) {
            cont.resumeWithException(Exception("Location services are disabled"))
            return@suspendCancellableCoroutine
        }

        // 首先检查是否有有效的缓存位置
        lastKnownLocation?.let { location ->
            if (isLocationValid(location)) {
                cont.resume(location)
                return@suspendCancellableCoroutine
            }
        }

        // 如果没有有效的缓存位置，请求位置更新
        try {
            // 优先使用GPS，其次使用网络定位
            val provider =
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    LocationManager.GPS_PROVIDER
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                ) {
                    LocationManager.NETWORK_PROVIDER
                } else {
                    cont.resumeWithException(Exception("No location provider available"))
                    return@suspendCancellableCoroutine
                }

            // 请求单次位置更新
            locationManager.requestSingleUpdate(
                provider,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        lastKnownLocation = location
                        cont.resume(location)
                        // 移除监听器
                        locationManager.removeUpdates(this)
                    }

                    override fun onStatusChanged(
                        provider: String?,
                        status: Int,
                        extras: Bundle?
                    ) {
                    }

                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {
                        cont.resumeWithException(Exception("Location provider disabled"))
                        locationManager.removeUpdates(this)
                    }
                },
                Looper.getMainLooper()
            )

            // 设置超时（10秒）
            cont.invokeOnCancellation { locationManager.removeUpdates(locationListener) }
        } catch (e: SecurityException) {
            cont.resumeWithException(SecurityException("Location permission denied"))
        } catch (e: Exception) {
            cont.resumeWithException(Exception("Failed to get location: ${e.message}"))
        }
    }
}

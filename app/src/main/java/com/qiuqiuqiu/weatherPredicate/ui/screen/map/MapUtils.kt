package com.qiuqiuqiu.weatherPredicate.ui.screen.map


import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BitmapDescriptor
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.geocode.GeoCodeOption
import com.baidu.mapapi.search.geocode.GeoCodeResult
import com.baidu.mapapi.search.geocode.GeoCoder
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult
import com.bumptech.glide.Glide
import com.qiuqiuqiu.weatherPredicate.model.CityWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "MapUtils"

object MapUtils {

    /**
     * 搜索并显示地理编码结果
     */
    fun geocodeAndShow(
        context: Context,
        mapView: MapView,
        baiduMap: BaiduMap?,
        city: String,
        address: String,
        fallbackAddressOnly: Boolean = false,
        onResult: ((LatLng?) -> Unit)? = null
    ) {
        if (address.isBlank() && city.isBlank()) {
            Toast.makeText(context, "请输入有效的城市或地址", Toast.LENGTH_SHORT).show()
            onResult?.invoke(null)
            return
        }

        val coder = GeoCoder.newInstance()
        coder.setOnGetGeoCodeResultListener(object : OnGetGeoCoderResultListener {
            override fun onGetGeoCodeResult(result: GeoCodeResult?) {
                (context as? Activity)?.runOnUiThread {
                    try {
                        if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
                            val pt: LatLng = result.location

                            // ✅ 检查是否在中国境内
                            if (!isInChina(pt.latitude, pt.longitude)) {
                                Toast.makeText(context, "仅支持中国境内地址查询", Toast.LENGTH_SHORT).show()
                                onResult?.invoke(null)
                                return@runOnUiThread
                            }

                            // ✅ 在地图上显示标记
                            baiduMap?.clear()
                            val bd = BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation)
                            val markerOpts = MarkerOptions().position(pt).icon(bd)
                            baiduMap?.addOverlay(markerOpts)
                            baiduMap?.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(pt, 15f))

                            onResult?.invoke(pt)
                        } else {
                            if (fallbackAddressOnly) {
                                coder.geocode(GeoCodeOption().address(address))
                                return@runOnUiThread
                            } else {
                                Toast.makeText(context, "未找到该地址，请检查输入", Toast.LENGTH_SHORT).show()
                                onResult?.invoke(null)
                            }
                        }
                    } finally {
                        mapView.postDelayed({
                            try {
                                coder.destroy()
                            } catch (_: Throwable) {}
                        }, 300L)
                    }
                }
            }

            override fun onGetReverseGeoCodeResult(result: ReverseGeoCodeResult?) {}
        })

        try {
            val opt = GeoCodeOption()
            if (city.isNotBlank()) opt.city(city)
            if (address.isNotBlank()) opt.address(address)
            coder.geocode(opt)
        } catch (e: Exception) {
            Log.e(TAG, "geocode call failed: ${e.message}", e)
            Toast.makeText(context, "查询失败: ${e.message}", Toast.LENGTH_SHORT).show()
            onResult?.invoke(null)
            try {
                coder.destroy()
            } catch (_: Throwable) {}
        }
    }

    /**
     * ✅ 判断是否在中国大陆范围内
     */
    private fun isInChina(lat: Double, lon: Double): Boolean {
        return lat in 3.0..54.0 && lon in 73.0..136.0
    }


    /**
     * 启动定位，并将地图移动到当前位置
     */
    fun startLocation(context: Context, baiduMap: BaiduMap?) {
        val client = LocationClient(context.applicationContext)
        val option = LocationClientOption().apply {
            isOpenGps = true
            setCoorType("bd09ll") // 百度经纬度坐标
            setScanSpan(0)       // 仅定位一次
        }
        client.locOption = option

        client.registerLocationListener(object : BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation?) {
                Log.d("LocationTest", "onReceiveLocation: $location")
                if (location == null) {
                    Toast.makeText(context, "定位失败: location is null", Toast.LENGTH_SHORT).show()
                    return
                }
                location ?: return
                val latLng = LatLng(location.latitude, location.longitude)

                baiduMap?.apply {
                    isMyLocationEnabled = true
                    val locData = MyLocationData.Builder()
                        .latitude(location.latitude)
                        .longitude(location.longitude)
                        .accuracy(location.radius)
                        .build()
                    setMyLocationData(locData)

                    setMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, 18f))
                }

                Toast.makeText(
                    context,
                    "定位成功: ${location.latitude}, ${location.longitude}",
                    Toast.LENGTH_SHORT
                ).show()

                client.stop() // 定位一次后停止
            }
        })

        client.start()
    }
}

/**
 * 管理 MapView 生命周期，避免内存泄漏
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> mapView.onResume()
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                androidx.lifecycle.Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    return mapView
}

/**
 * 动态加载天气图标，按 size 缩放
 */
suspend fun getWeatherIcon(context: Context, iconCode: String, size: Int): BitmapDescriptor? {
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://a.hecdn.net/img/common/icon/202106d/${iconCode}.png"
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(url)
                .submit(size, size) // 👈 根据缩放级别动态指定大小
                .get()
            BitmapDescriptorFactory.fromBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("MapUtils", "加载天气图标失败: ${e.message}")
            null
        }
    }
}

/**
 * 根据地图缩放级别，显示城市天气图标
 */
suspend fun showCityWeatherMarkers(
    context: Context,
    baiduMap: BaiduMap?,
    cities: List<CityWeather>
) {
    if (baiduMap == null) return

    // 获取当前缩放级别
    val zoom = baiduMap.mapStatus.zoom
    val minSize = 32   // 最小图标 px
    val maxSize = 80   // 最大图标 px
    val size = (zoom * 5).toInt().coerceIn(minSize, maxSize)

    withContext(Dispatchers.Main) {
        baiduMap.clear()
        for (city in cities) {
            val pt = LatLng(city.lat, city.lon)
            val icon = getWeatherIcon(context, city.icon, size)
            val markerOpts = if (icon != null) {
                MarkerOptions().position(pt).icon(icon).title("${city.name} ${city.text}")
            } else {
                MarkerOptions().position(pt).title("${city.name} ${city.text}")
            }
            baiduMap.addOverlay(markerOpts)
        }
    }
}


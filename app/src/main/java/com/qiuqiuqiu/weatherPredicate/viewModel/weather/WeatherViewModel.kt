package com.qiuqiuqiu.weatherPredicate.viewModel.weather

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.model.weather.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.weather.CityType
import com.qiuqiuqiu.weatherPredicate.model.weather.LocationWeatherModel
import com.qiuqiuqiu.weatherPredicate.service.ILocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

val defaultLocation = CityLocationModel(CityType.Normal, Pair(116.4074, 39.9042))

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherManager: ILocationWeatherManager,
    private val locationService: ILocationService,
    private val localDataManager: ILocalDataManager
) : ViewModel() {
    var locationWeather = mutableStateOf(LocationWeatherModel())

    var isInit: MutableState<Boolean> = mutableStateOf(true)
        private set

    var isRefreshing: MutableState<Boolean> = mutableStateOf(false)
        private set

    private lateinit var currentLocation: CityLocationModel
    private var pointLocation: Location? = null

    /**
     * 使用前必须调用
     */
    @SuppressLint("NewApi")
    fun initLocation(
        location: CityLocationModel,
        refresh: Boolean = true
    ) {
        currentLocation = location
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("Weather", "获取天气失败: ${e.stackTrace}")
        } + Dispatchers.IO) {
            val startTime = LocalDateTime.now()
            currentLocation.let {
                // 先取缓存数据
                val result = weatherManager.getCacheLocationWeather(it)

                locationWeather.value = result.first
                isInit.value = false

                Log.i(
                    "Weather",
                    "首次加载页面耗时: ${
                        Duration.between(startTime, LocalDateTime.now()).toMillis()
                    } ms"
                )

                viewModelScope.launch(CoroutineExceptionHandler { _, e ->
                    Log.e("Weather", "获取天气失败: ${e.stackTrace}")
                } + Dispatchers.IO) {
                    delay(200)
                    // 定位城市，且开启定位，则更新坐标
                    if (currentLocation.type == CityType.Position && locationService.isLocationEnabled()) {
                        val location = locationService.getLastLocation()
                        if (location != null && location != pointLocation) {
                            currentLocation = CityLocationModel(
                                CityType.Position,
                                Pair(location.longitude, location.latitude)
                            )
                            localDataManager.addPositionCity(currentLocation)

                            val weatherLoc =
                                weatherManager.getCacheLocationWeather(it)
                            locationWeather.value = weatherLoc.first
                        }
                        pointLocation = location
                    }
                    // 如果命中缓存，再请求更新
                    else if (refresh && !result.second
                        && Duration.between(result.first.lastUpdateTime, LocalDateTime.now())
                            .toMinutes() > 10
                    ) {
                        isRefreshing.value = true
                        val weatherLocation =
                            weatherManager.getNewLocationWeather(it)
                        locationWeather.value = weatherLocation
                        isRefreshing.value = false
                    }
                    Log.i(
                        "Weather",
                        "更新数据耗时: ${
                            Duration.between(startTime, LocalDateTime.now()).toMillis()
                        } ms"
                    )
                }
            }
        }
    }

    fun refreshing() {
        if (isInit.value || isRefreshing.value) return
        isRefreshing.value = true
        updateWeatherModel { isRefreshing.value = false }
    }

    fun updateWeatherModel(callback: () -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("Weather", "获取天气失败: ${e.message}")
        } + Dispatchers.IO) {
            currentLocation.let {
                val locWeather =
                    weatherManager.getNewLocationWeather(it)
                locationWeather.value = locWeather
            }
            callback.invoke()
        }
    }
}
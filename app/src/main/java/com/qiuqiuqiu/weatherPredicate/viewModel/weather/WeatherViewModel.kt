package com.qiuqiuqiu.weatherPredicate.viewModel.weather

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.model.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.CityType
import com.qiuqiuqiu.weatherPredicate.model.LocationWeatherModel
import com.qiuqiuqiu.weatherPredicate.service.ILocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val _locationWeather = MutableStateFlow(LocationWeatherModel())
    val locationWeather: StateFlow<LocationWeatherModel> = _locationWeather.asStateFlow()

    var isInit: MutableState<Boolean> = mutableStateOf(true)
        private set

    var isRefreshing: MutableState<Boolean> = mutableStateOf(false)
        private set

    private var currentLocation: CityLocationModel? = null

    /**
     * 使用前必须调用
     */
    @SuppressLint("NewApi")
    fun initLocation(location: CityLocationModel?, refresh: Boolean = true) {
        currentLocation = location ?: currentLocation ?: defaultLocation

        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("Weather", "获取天气失败: ${e.stackTrace}")
        } + Dispatchers.IO) {
            // 判断当前位置是否在城市列表中，如果不存在，相当于删掉了位置，使用第一个城市
            val list = localDataManager.getCityList()
            if (currentLocation != null) {
                if (list.firstOrNull {
                        currentLocation!!.location.first == it.location.first && currentLocation!!.location.second == it.location.second
                    } == null
                ) {
                    currentLocation = list.firstOrNull() ?: defaultLocation
                }


                // 判断当前位置是否为定位城市，且没有权限
                if (currentLocation!!.type == CityType.Position && !locationService.hasLocationPermissions()) {
                    // 未获得权限，删除城市列表中的定位Location
                    localDataManager.removePositionCity()
                    currentLocation =
                        list.firstOrNull({ it.type == CityType.Host }) ?: list.firstOrNull()
                                ?: defaultLocation
                }

                val result =
                    weatherManager.getCacheLocationWeather(currentLocation!!)
                _locationWeather.update { result.first }
                isInit.value = false

                // 定位城市，且开启定位，则更新坐标
                if (currentLocation!!.type == CityType.Position && locationService.isLocationEnabled()) {
                    val location = locationService.getLastLocation()
                    if (location != null) {
                        currentLocation = CityLocationModel(
                            CityType.Position,
                            Pair(location.longitude, location.latitude)
                        )
                        localDataManager.addPositionCity(currentLocation!!)

                        val weatherLoc =
                            weatherManager.getCacheLocationWeather(currentLocation!!)
                        _locationWeather.update { weatherLoc.first }
                    }
                }
                // 如果命中缓存，再请求更新
                else if (refresh && !result.second
                    && Duration.between(locationWeather.value.lastUpdateTime, LocalDateTime.now())
                        .toMinutes() > 10
                ) {
                    isRefreshing.value = true
                    val weatherLocation =
                        weatherManager.getNewLocationWeather(currentLocation!!)
                    _locationWeather.update { weatherLocation }
                    isRefreshing.value = false
                }
            }
        }
    }

    fun refreshing() {
        if (isInit.value || isRefreshing.value) return
        isRefreshing.value = true
        updateWeatherModel { isRefreshing.value = false }
    }

    private fun updateWeatherModel(callback: () -> Unit) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("Weather", "获取天气失败: ${e.message}")
        } + Dispatchers.IO) {
            currentLocation?.let {
                val locationWeather =
                    weatherManager.getNewLocationWeather(it)
                _locationWeather.update { locationWeather }
            }
            callback.invoke()
        }
    }
}
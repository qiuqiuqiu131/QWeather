package com.qiuqiuqiu.weatherPredicate.viewModel

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

    private lateinit var currentLocation: CityLocationModel

    /**
     * 使用前必须调用
     */
    @SuppressLint("NewApi")
    fun initLocation(location: CityLocationModel?, refresh: Boolean = true) {
        if (location != null)
            currentLocation = location
        else
            currentLocation = defaultLocation

        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("Weather", "获取天气失败: ${e.stackTrace}")
        } + Dispatchers.IO) {
            val list = localDataManager.getCityList()
            if (list.firstOrNull {
                    it.location.first == currentLocation.location.first &&
                            it.location.second == currentLocation.location.second
                } == null) {
                currentLocation = list.firstOrNull() ?: defaultLocation
            }

            if (currentLocation.type == CityType.Position && !locationService.hasLocationPermissions()) {
                // 未获得权限，删除城市列表中的定位Location
                localDataManager.removePositionCity()
                val list = localDataManager.getCityList().toMutableList()
                currentLocation =
                    list.firstOrNull({ it.type == CityType.Host }) ?: list.firstOrNull()
                            ?: defaultLocation
            }

            currentLocation.let {
                val result =
                    weatherManager.getCacheLocationWeather(it)
                _locationWeather.update { result.first }
                isInit.value = false

                // 定位城市，且开启定位，则更新坐标
                if (currentLocation.type == CityType.Position && locationService.isLocationEnabled()) {
                    val location = locationService.getLastLocation()
                    if (location != null) {
                        currentLocation = CityLocationModel(
                            CityType.Position,
                            Pair(location.longitude, location.latitude)
                        )
                        localDataManager.addPositionCity(currentLocation)

                        val weatherLoc =
                            weatherManager.getCacheLocationWeather(it)
                        _locationWeather.update { weatherLoc.first }
                    }
                }
                // 如果命中缓存，再请求更新
                else if (refresh && !result.second
                    && Duration.between(result.first.lastUpdateTime, LocalDateTime.now())
                        .toMinutes() > 10
                ) {
                    isRefreshing.value = true
                    val weatherLocation =
                        weatherManager.getNewLocationWeather(it)
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
            currentLocation.let {
                val locationWeather =
                    weatherManager.getNewLocationWeather(it)
                _locationWeather.update { locationWeather }
            }
            callback.invoke()
        }
    }
}
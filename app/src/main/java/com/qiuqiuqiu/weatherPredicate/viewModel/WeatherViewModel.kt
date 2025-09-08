package com.qiuqiuqiu.weatherPredicate.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
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

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherManager: ILocationWeatherManager,
    private val locationService: ILocationService
) : ViewModel() {
    private val _locationWeather = MutableStateFlow(LocationWeatherModel())
    val locationWeather: StateFlow<LocationWeatherModel> = _locationWeather.asStateFlow()

    var isInit: MutableState<Boolean> = mutableStateOf(true)
        private set

    var isRefreshing: MutableState<Boolean> = mutableStateOf(false)
        private set

    private lateinit var currentLocation: Pair<Double, Double>

    /**
     * 使用前必须调用
     */
    @SuppressLint("NewApi")
    fun initLocation(location: Pair<Double, Double>?, refresh: Boolean = true) {
        if (location != null)
            currentLocation = location

        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("Weather", "获取天气失败: ${e.message}")
        } + Dispatchers.IO) {
            if (location == null) {
                val location = locationService.getLastLocation()
                if (location != null)
                    currentLocation = Pair(location.longitude, location.latitude)
                else
                // TODO:上一个显示的城市
                    currentLocation = Pair(116.4074, 39.9042)
            }

            currentLocation.let {
                val result = weatherManager.getCacheLocationWeather(it.first, it.second)
                _locationWeather.update { result.first }

                viewModelScope.launch(Dispatchers.Main) {
                    isInit.value = false
                }

                // 如果命中缓存，再请求更新
                if (refresh && !result.second
                    && Duration.between(result.first.lastUpdateTime, LocalDateTime.now())
                        .toMinutes() > 10
                ) {
                    isRefreshing.value = true
                    val weatherLocation =
                        weatherManager.getNewLocationWeather(it.first, it.second)
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
                    weatherManager.getNewLocationWeather(it.first, it.second)
                _locationWeather.update { locationWeather }
            }
            callback.invoke()
        }
    }
}
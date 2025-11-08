package com.qiuqiuqiu.weatherPredicate.viewModel.weather

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.model.weather.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.service.ILocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherMainViewModel @Inject constructor(
    private val locationWeatherManager: ILocationWeatherManager,
    private val locationService: ILocationService,
    private val localDataManager: ILocalDataManager
) : ViewModel() {
    var cities: MutableState<List<CityLocationModel>> = mutableStateOf(emptyList())
        private set

    var isRefreshing: MutableState<Boolean> = mutableStateOf(false)
        private set

    var isInit: MutableState<Boolean> = mutableStateOf(true)
        private set

    var loadingFailed: MutableState<Boolean> = mutableStateOf(false)
        private set

    var pageIndex: MutableIntState = mutableIntStateOf(0)

    private var currentCityWeather: MutableStateFlow<WeatherViewModel?> = MutableStateFlow(null)
    val cityWeather: StateFlow<WeatherViewModel?> = currentCityWeather

    init {
        initCities()
    }

    // 加载城市列表及天气
    fun initCities() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            viewModelScope.launch(
                Dispatchers.Main
            ) {
                isInit.value = false
                loadingFailed.value = true
            }
        }) {
            // 判断当前位置是否在城市列表中，如果不存在
            if (!locationService.hasLocationPermissions()) {
                // 未获得权限，删除城市列表中的定位Location
                localDataManager.removePositionCity()
            }

            var citiesList = localDataManager.getCityList()
            if (citiesList.isEmpty())
                citiesList = listOf(defaultLocation)
            citiesList.forEach { locationWeatherManager.getCacheLocationWeather(it) }
            cities.value = citiesList

            isInit.value = false
        }
    }

    fun setCurrentCity(weather: WeatherViewModel?) {
        currentCityWeather.update { weather }
    }
}
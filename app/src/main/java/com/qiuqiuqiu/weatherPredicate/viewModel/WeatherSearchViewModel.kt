package com.qiuqiuqiu.weatherPredicate.viewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.manager.ISearchCityManager
import com.qiuqiuqiu.weatherPredicate.manager.LocalDataManager
import com.qiuqiuqiu.weatherPredicate.model.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.CityType
import com.qiuqiuqiu.weatherPredicate.model.SearchCityModel
import com.qiuqiuqiu.weatherPredicate.service.IQWeatherService
import com.qiuqiuqiu.weatherPredicate.service.LocationService
import com.qweather.sdk.response.geo.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class WeatherSearchViewModel @Inject constructor(
    val weatherService: IQWeatherService,
    val searchCityManager: ISearchCityManager,
    val locationService: LocationService,
    val locationWeatherManager: ILocationWeatherManager,
    val localDataManager: LocalDataManager
) : ViewModel() {
    var searchCityModel: MutableStateFlow<SearchCityModel> = MutableStateFlow(SearchCityModel())
        private set

    var isInit: MutableState<Boolean> = mutableStateOf(false)
        private set

    init {
        isInit.value = true
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("Search", "获取天气失败: ${e.message}")
        } + Dispatchers.IO) {
            val model = searchCityManager.getTopCities()
            searchCityModel.update { model }
            viewModelScope.launch(Dispatchers.Main) {
                isInit.value = false
            }
        }
    }

    var searchCities: MutableState<List<Location>?> = mutableStateOf(null)
        private set

    private val searchInputFlow = MutableStateFlow<String?>(null)

    init {
        searchInputFlow.debounce(400) // 400ms内只响应最后一次输入
            .distinctUntilChanged()
            .onEach { input ->
                if (input.isNullOrBlank()) {
                    searchCities.value = null
                } else {
                    viewModelScope.launch(CoroutineExceptionHandler { _, e ->
                        Log.e("Search", "获取天气失败: ${e.message}")
                    } + Dispatchers.IO) {
                        weatherService.getCurrentCity(input, number = 20).let {
                            searchCities.value = it
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchInputChange(input: String) {
        searchInputFlow.value = input
    }

    fun initCityWeather(location: Location, callBack: () -> Unit) {
        isInit.value = true
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("Weather", "获取天气失败: ${e.message}")
        } + Dispatchers.IO) {
            locationWeatherManager.getNewLocationWeather(
                CityLocationModel(
                    CityType.Normal, Pair(location.lon.toDouble(), location.lat.toDouble())
                )
            )

            viewModelScope.launch(Dispatchers.Main) {
                callBack()
                delay(1000)
                isInit.value = false
            }
        }
    }

    fun requirePosition(): Boolean =
        runBlocking {
            localDataManager.getCityList().firstOrNull { it.type == CityType.Position } == null
        }
}
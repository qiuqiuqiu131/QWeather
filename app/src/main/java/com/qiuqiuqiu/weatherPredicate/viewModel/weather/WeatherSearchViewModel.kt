package com.qiuqiuqiu.weatherPredicate.viewModel.weather

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.manager.ISearchCityManager
import com.qiuqiuqiu.weatherPredicate.model.weather.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.weather.CityType
import com.qiuqiuqiu.weatherPredicate.model.weather.SearchCityModel
import com.qiuqiuqiu.weatherPredicate.model.weather.SearchHistory
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
    val localDataManager: ILocalDataManager
) : ViewModel() {
    var searchCityModel: MutableStateFlow<SearchCityModel> = MutableStateFlow(SearchCityModel())
        private set

    var searchHistories: MutableStateFlow<List<SearchHistory>> = MutableStateFlow(listOf())
        private set

    var rangePois: MutableStateFlow<List<Location>?> = MutableStateFlow(null)
        private set

    var isInit: MutableState<Boolean> = mutableStateOf(true)
        private set

    fun initSearchData(currentCity: CityLocationModel?) {
        searchInputFlow.value = null
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("Search", "获取天气失败: ${e.message}")
        } + Dispatchers.IO) {
            val model = searchCityManager.getTopCities()
            searchCityModel.update { model }
            searchHistories.update { localDataManager.getSearchHistories() }
            if (currentCity != null)
                try {
                    rangePois.update {
                        searchCityManager.getPoiCache(
                            currentCity.location.first.toString(),
                            currentCity.location.second.toString()
                        )
                    }
                } catch (e: Exception) {
                    Log.e("Search", "获取附近失败: ${e.message}")
                }

            delay(300)
            viewModelScope.launch(Dispatchers.Main) {
                isInit.value = false
            }
        }
    }

    var searchCities: MutableState<List<Location>?> = mutableStateOf(null)
        private set

    var searchInputFlow = MutableStateFlow<String?>(null)
        private set

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
            isInit.value = false
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

    var isLoadingLocation: MutableState<Boolean> = mutableStateOf(false)
        private set

    fun isLocationEnabled(): Boolean = locationService.isLocationEnabled()

    fun addPositionCity(callBack: (CityLocationModel) -> Unit) {
        isLoadingLocation.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val location = locationService.getLastLocation()
            if (location == null) {
                // 获取定位失败
            } else {
                val city = CityLocationModel(
                    CityType.Position,
                    Pair(location.longitude, location.latitude)
                )
                localDataManager.addPositionCity(city)
                locationWeatherManager.getNewLocationWeather(city)
                delay(500)
                viewModelScope.launch(Dispatchers.Main) {
                    callBack(city)
                    isLoadingLocation.value = false
                }
            }
        }
    }

    fun clearSearchHistories() {
        viewModelScope.launch(Dispatchers.IO) {
            localDataManager.clearSearchHistories()
            viewModelScope.launch(Dispatchers.Main) {
                searchHistories.value = listOf()
            }
        }
    }

    fun searchHistoryClick(history: SearchHistory) {
        searchInputFlow.value = history.name
    }

    fun addSearchHistory(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            val history =
                SearchHistory(location.name, Pair(location.lon.toDouble(), location.lat.toDouble()))
            localDataManager.addSearchHistory(history)
            val list = localDataManager.getSearchHistories()
            viewModelScope.launch(Dispatchers.Main) {
                searchHistories.value = list
            }
        }
    }
}
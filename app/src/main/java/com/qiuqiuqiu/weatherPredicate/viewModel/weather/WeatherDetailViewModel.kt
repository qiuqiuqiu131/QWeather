package com.qiuqiuqiu.weatherPredicate.viewModel.weather

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.model.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.LocationWeatherModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    val locationWeatherManager: ILocationWeatherManager,
    val localDataManager: ILocalDataManager
) : ViewModel() {
    var isInit = mutableStateOf(true)
        private set

    private val _locationWeather = MutableStateFlow(LocationWeatherModel())
    val locationWeather: StateFlow<LocationWeatherModel> = _locationWeather.asStateFlow()

    private lateinit var cityList: List<CityLocationModel>

    fun initWeatherData(city: CityLocationModel) {
        viewModelScope.launch(Dispatchers.IO) {
            cityList = localDataManager.getCityList()

            val result = locationWeatherManager.getCacheLocationWeather(city)
            _locationWeather.update { result.first }
            isInit.value = false
        }
    }
}
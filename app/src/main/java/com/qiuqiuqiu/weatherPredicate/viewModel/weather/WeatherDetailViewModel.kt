package com.qiuqiuqiu.weatherPredicate.viewModel.weather

import android.util.Log
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.model.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.LocationWeatherModel
import com.qiuqiuqiu.weatherPredicate.service.IQWeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val defaultPageNames = listOf("实况天气", "每日天气", "多日天气", "空气质量")

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    val locationWeatherManager: ILocationWeatherManager,
    val localDataManager: ILocalDataManager,
    val weatherService: IQWeatherService
) : ViewModel() {
    var isInit = mutableStateOf(true)
        private set

    private val _locationWeather = MutableStateFlow(LocationWeatherModel())
    val locationWeather: StateFlow<LocationWeatherModel> = _locationWeather.asStateFlow()

    var pageItems: MutableState<List<String>> = mutableStateOf(emptyList())
        private set

    var pageIndex: MutableIntState = mutableIntStateOf(0)
        private set

    private lateinit var cityList: List<CityLocationModel>

    fun initWeatherData(city: CityLocationModel, pageName: String? = null) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("Weather", "获取天气失败: ${e.stackTrace}")
        } + Dispatchers.IO) {
            cityList = localDataManager.getCityList()

            val result = locationWeatherManager.getCacheLocationWeather(city)

            // 获取拓展天气数据
            val weatherDailiesTask =
                async { weatherService.getWeatherMoreDay(result.first.location!!.id) }
            val weatherHourliesTask =
                async { weatherService.getWeather168Hour(result.first.location!!.id) }
            val indicesDailiesTask = async {
                weatherService.getWeatherIndices3Day(result.first.location!!.id).groupBy { it.name }
                    .map { Pair(it.key, it.value.sortedBy { va -> va.date }) }
            }
            result.first.weatherDailiesMore = weatherDailiesTask.await()
            result.first.weatherHourliesMore = weatherHourliesTask.await()
            result.first.indicesDailiesMore = indicesDailiesTask.await()

            _locationWeather.update { result.first }

            pageItems.value =
                defaultPageNames + (result.first.indicesDailies?.map { it.name.replace("指数", "") }
                    ?: emptyList())
            if (pageName != null && isInit.value)
                pageIndex.intValue = pageItems.value.indexOf(pageName).let { if (it < 0) 0 else it }
            isInit.value = false
        }
    }
}
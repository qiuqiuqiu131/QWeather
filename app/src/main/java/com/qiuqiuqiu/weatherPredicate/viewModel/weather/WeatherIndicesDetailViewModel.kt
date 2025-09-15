package com.qiuqiuqiu.weatherPredicate.viewModel.weather

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.model.TimelyChartModel
import com.qweather.sdk.response.indices.IndicesDaily
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherIndicesDetailViewModel @Inject constructor(val context: Context) : ViewModel() {
    private val _chartModel: MutableStateFlow<TimelyChartModel?> = MutableStateFlow(null)
    val chartModel: StateFlow<TimelyChartModel?> = _chartModel.asStateFlow()

    val selectedIndex = mutableIntStateOf(0)

    @SuppressLint("NewApi")
    fun initChartModel(weatherIndices: IndicesDaily, detailModel: WeatherDetailViewModel) {
        _chartModel.update { null }
        viewModelScope.launch(Dispatchers.Default) {
            val model = when (weatherIndices.type.toInt()) {
                1 -> detailModel.getTimelyChartModel(HourlyDetailType.Pop)
                2 -> detailModel.getTimelyChartModel(HourlyDetailType.Hum)
                5 -> detailModel.getTimelyChartModel(HourlyDetailType.Cloud)
                4 -> detailModel.getTimelyChartModel(HourlyDetailType.Pressure)
                7 -> detailModel.getTimelyChartModel(HourlyDetailType.Wind)
                13 -> detailModel.getTimelyChartModel(HourlyDetailType.Hum)
                14 -> detailModel.getTimelyChartModel(HourlyDetailType.Cloud)
                16 -> detailModel.getTimelyChartModel(HourlyDetailType.Cloud)
                else -> detailModel.getTimelyChartModel(HourlyDetailType.Temp)
            }
            viewModelScope.launch(Dispatchers.Main) {
                _chartModel.update { model }
            }
        }
    }
}
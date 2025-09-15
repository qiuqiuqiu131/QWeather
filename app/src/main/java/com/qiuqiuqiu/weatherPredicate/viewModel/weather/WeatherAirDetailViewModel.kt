package com.qiuqiuqiu.weatherPredicate.viewModel.weather

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.model.TimelyChartModel
import com.qiuqiuqiu.weatherPredicate.ui.normal.ChartPoint
import com.qweather.sdk.response.air.v1.AirHourly
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

@HiltViewModel
class WeatherAirDetailViewModel @Inject constructor() : ViewModel() {
    private var _model: MutableStateFlow<TimelyChartModel?> = MutableStateFlow(null)
    val model = _model.asStateFlow()

    @SuppressLint("NewApi")
    fun initModel(airHourly: List<AirHourly>) {
        viewModelScope.launch(Dispatchers.Default) {
            var model = TimelyChartModel(
                airHourly.map { it ->
                    ChartPoint(
                        time = OffsetDateTime.parse(it.forecastTime),
                        value = it.indexes.firstOrNull()?.aqi?.toFloat() ?: 0f
                    )
                },
                "AQI",
                type = HourlyDetailType.Air
            )
            _model.emit(model)
        }
    }
}
package com.qiuqiuqiu.weatherPredicate.model

import com.qiuqiuqiu.weatherPredicate.ui.normal.ChartPoint
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.HourlyDetailType

data class TimelyChartModel(
    val data: List<ChartPoint>,
    val type: HourlyDetailType
)
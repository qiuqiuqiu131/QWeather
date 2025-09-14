package com.qiuqiuqiu.weatherPredicate.model

import com.qiuqiuqiu.weatherPredicate.ui.normal.ChartPoint
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.HourlyDetailType

data class TimelyChartModel(
    val data1: List<ChartPoint>,
    val dataName1: String,
    val data2: List<ChartPoint>? = null,
    val dataName2: String? = null,
    val type: HourlyDetailType
)
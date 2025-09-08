package com.qiuqiuqiu.weatherPredicate.model

enum class CityType {
    Normal,
    Position,
    Host
}

data class CityLocationModel(
    val type: CityType,
    val location: Pair<Double, Double>
)
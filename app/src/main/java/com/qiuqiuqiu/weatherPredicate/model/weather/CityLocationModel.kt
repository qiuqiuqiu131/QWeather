package com.qiuqiuqiu.weatherPredicate.model.weather

enum class CityType {
    Normal,
    Position,
    Host
}

data class CityLocationModel(
    val type: CityType,
    val location: Pair<Double, Double>
)
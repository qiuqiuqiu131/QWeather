package com.qiuqiuqiu.weatherPredicate.model.weather

import com.qweather.sdk.response.geo.Location
import com.qweather.sdk.response.weather.WeatherDaily
import java.time.LocalDateTime

data class SearchCityModel(
    val updateTime: LocalDateTime? = null,
    val topCities: List<Pair<Location, WeatherDaily>>? = null
)
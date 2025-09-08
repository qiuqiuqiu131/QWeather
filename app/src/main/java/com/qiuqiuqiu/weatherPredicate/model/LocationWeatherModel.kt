package com.qiuqiuqiu.weatherPredicate.model

import com.qweather.sdk.response.air.v1.AirV1CurrentResponse
import com.qweather.sdk.response.geo.Location
import com.qweather.sdk.response.indices.IndicesDaily
import com.qweather.sdk.response.warning.Warning
import com.qweather.sdk.response.weather.WeatherDaily
import com.qweather.sdk.response.weather.WeatherHourly
import com.qweather.sdk.response.weather.WeatherNow
import java.time.LocalDateTime

data class LocationWeatherModel(
    var type: CityType = CityType.Normal,
    val weatherNow: WeatherNow? = null,
    val location: Location? = null,
    val weatherHourlies: List<WeatherHourly>? = null,
    val weatherDailies: List<WeatherDaily>? = null,
    val indicesDailies: List<IndicesDaily>? = null,
    val warnings: List<Warning>? = null,
    val airCurrent: AirV1CurrentResponse? = null,
    val lastUpdateTime: LocalDateTime? = null
)
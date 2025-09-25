package com.qiuqiuqiu.weatherPredicate.manager

import android.annotation.SuppressLint
import com.qiuqiuqiu.weatherPredicate.model.weather.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.weather.LocationWeatherModel
import com.qiuqiuqiu.weatherPredicate.repository.TianRepository
import com.qiuqiuqiu.weatherPredicate.service.IQWeatherService
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime

interface ILocationWeatherManager {
    /** 获取对应位置的天气数据，请求更新 */
    suspend fun getNewLocationWeather(
        location: CityLocationModel
    ): LocationWeatherModel

    /** 获取对应位置的天气数据，优先获取缓存 */
    suspend fun getCacheLocationWeather(
        location: CityLocationModel
    ): Pair<LocationWeatherModel, Boolean>
}

class LocationWeatherManager @Inject constructor(
    val weatherService: IQWeatherService,
    val repository: TianRepository
) :
    ILocationWeatherManager {
    private val weatherCache: MutableMap<String, LocationWeatherModel> = mutableMapOf()

    override suspend fun getNewLocationWeather(
        location: CityLocationModel
    ): LocationWeatherModel {
        val locationWeather = getLocationWeather(location)
        weatherCache["${location.location.first},${location.location.second}"] = locationWeather
        return locationWeather
    }

    override suspend fun getCacheLocationWeather(
        location: CityLocationModel
    ): Pair<LocationWeatherModel, Boolean> {
        val locationWeather = weatherCache["${location.location.first},${location.location.second}"]
        if (locationWeather != null) {
            locationWeather.type = location.type
            return Pair(locationWeather, false)
        }
        return Pair(getNewLocationWeather(location), true)
    }

    @SuppressLint("NewApi")
    private suspend fun getLocationWeather(
        location: CityLocationModel
    ): LocationWeatherModel =
        coroutineScope {
            val locationId = "${location.location.first},${location.location.second}"
            val resCity = async { weatherService.getCurrentCity(locationId).firstOrNull() }
            val resWeatherNow = async { weatherService.getWeatherNow(locationId) }
            val resWeatherHourly = async { weatherService.getWeather24Hour(locationId) }
            val resWeatherDaily = async { weatherService.getWeather7Day(locationId) }
            val resIndicesDaily = async { weatherService.getWeatherIndices(locationId) }
            val resWarnings = async { weatherService.getWarningNow(locationId) }
            val resAirCurrent = async {
                weatherService.getAirCurrent(
                    location.location.first,
                    location.location.second
                )
            }

            val model = LocationWeatherModel(
                type = location.type,
                location = resCity.await(),
                weatherNow = resWeatherNow.await(),
                weatherDailies = resWeatherDaily.await(),
                weatherHourlies = resWeatherHourly.await(),
                indicesDailies = resIndicesDaily.await(),
                warnings = resWarnings.await(),
                airCurrent = resAirCurrent.await(),
                lastUpdateTime = LocalDateTime.now()
            )

            model.shiJu =
                repository.getShiJu(matchWeatherType(model.weatherNow?.text ?: "晴")).result

            model
        }
}

fun matchWeatherType(desc: String): Int = when {
    desc.contains("风") -> 1
    desc.contains("云") -> 2
    desc.contains("雨") -> 3
    desc.contains("雪") -> 4
    desc.contains("霜") -> 5
    desc.contains("露") -> 6
    desc.contains("雾") -> 7
    desc.contains("雷") -> 8
    desc.contains("晴") -> 9
    desc.contains("阴") -> 10
    else -> 9 // 未匹配到返回0
}

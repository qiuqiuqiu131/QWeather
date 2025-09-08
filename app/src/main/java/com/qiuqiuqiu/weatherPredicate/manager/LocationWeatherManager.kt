package com.qiuqiuqiu.weatherPredicate.manager

import android.annotation.SuppressLint
import com.qiuqiuqiu.weatherPredicate.model.LocationWeatherModel
import com.qiuqiuqiu.weatherPredicate.service.IQWeatherService
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime

interface ILocationWeatherManager {
    /** 获取对应位置的天气数据，请求更新 */
    suspend fun getNewLocationWeather(
        longitude: Double,
        latitude: Double
    ): LocationWeatherModel

    /** 获取对应位置的天气数据，优先获取缓存 */
    suspend fun getCacheLocationWeather(
        longitude: Double,
        latitude: Double
    ): Pair<LocationWeatherModel, Boolean>
}

class LocationWeatherManager @Inject constructor(val weatherService: IQWeatherService) :
    ILocationWeatherManager {
    private val weatherCache: MutableMap<String, LocationWeatherModel> = mutableMapOf()

    override suspend fun getNewLocationWeather(
        longitude: Double,
        latitude: Double

    ): LocationWeatherModel {
        val locationWeather = getLocationWeather(longitude, latitude)
        weatherCache["${longitude},${latitude}"] = locationWeather
        return locationWeather
    }

    override suspend fun getCacheLocationWeather(
        longitude: Double,
        latitude: Double
    ): Pair<LocationWeatherModel, Boolean> {
        val locationWeather = weatherCache["${longitude},${latitude}"]
        if (locationWeather != null) return Pair(locationWeather, false)
        return Pair(getNewLocationWeather(longitude, latitude), true)
    }

    @SuppressLint("NewApi")
    private suspend fun getLocationWeather(
        longitude: Double,
        latitude: Double
    ): LocationWeatherModel =
        coroutineScope {
            val locationId = "${longitude},${latitude}"
            val resCity = async { weatherService.getCurrentCity(locationId).firstOrNull() }
            val resWeatherNow = async { weatherService.getWeatherNow(locationId) }
            val resWeatherHourly = async { weatherService.getWeather24Hour(locationId) }
            val resWeatherDaily = async { weatherService.getWeather7Day(locationId) }
            val resIndicesDaily = async { weatherService.getWeatherIndices(locationId) }
            val resWarnings = async { weatherService.getWarningNow(locationId) }
            val resAirCurrent = async { weatherService.getAirCurrent(latitude, longitude) }

            LocationWeatherModel(
                location = resCity.await(),
                weatherNow = resWeatherNow.await(),
                weatherDailies = resWeatherDaily.await(),
                weatherHourlies = resWeatherHourly.await(),
                indicesDailies = resIndicesDaily.await(),
                warnings = resWarnings.await(),
                airCurrent = resAirCurrent.await(),
                lastUpdateTime = LocalDateTime.now()
            )
        }
}

package com.qiuqiuqiu.weatherPredicate.manager

import android.annotation.SuppressLint
import com.qiuqiuqiu.weatherPredicate.model.SearchCityModel
import com.qiuqiuqiu.weatherPredicate.service.IQWeatherService
import com.qweather.sdk.basic.Range
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.Duration
import java.time.LocalDateTime

interface ISearchCityManager {
    suspend fun getTopCities(): SearchCityModel
}

class SearchCityManager @Inject constructor(
    val weatherService: IQWeatherService
) : ISearchCityManager {
    private var topCitiesCache: SearchCityModel? = null

    @SuppressLint("NewApi")
    override suspend fun getTopCities(): SearchCityModel {
        val cache = topCitiesCache
        // 如果有缓存且未超过10分钟，直接返回缓存
        if (cache != null && Duration.between(cache.updateTime, LocalDateTime.now())
                .toMinutes() < 10
        ) {
            return cache
        }

        // 否则请求新数据并更新缓存
        val result = getTopCitiesFromService()
        topCitiesCache = result
        return result
    }

    @SuppressLint("NewApi")
    private suspend fun getTopCitiesFromService(): SearchCityModel =
        coroutineScope {
            val resCities = weatherService.getCityTop(15, Range.CN)
            val cityWeathersTask =
                resCities.map { async { weatherService.getWeather3Day(it.id).first() } }

            SearchCityModel(LocalDateTime.now(), resCities.zip(cityWeathersTask.awaitAll()))
        }
}
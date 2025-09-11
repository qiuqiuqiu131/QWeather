package com.qiuqiuqiu.weatherPredicate.manager

import com.qiuqiuqiu.weatherPredicate.model.CityWeather
import com.qiuqiuqiu.weatherPredicate.model.provinceCapitals
import com.qiuqiuqiu.weatherPredicate.service.IQWeatherService
import jakarta.inject.Inject
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

interface IMapWeatherManager {
    /** 获取省会城市的天气 */
    suspend fun getManualCitiesWeather(): List<CityWeather>
}

class MapWeatherManager @Inject constructor(val weatherService: IQWeatherService) :
        IMapWeatherManager {

    // 缓存结构，key为城市id，value为Pair<CityWeather, 缓存时间戳>
    private val cache = ConcurrentHashMap<String, Pair<CityWeather, Long>>()
    private val cacheDuration = 10 * 60 * 1000 // 10分钟，单位毫秒

    override suspend fun getManualCitiesWeather(): List<CityWeather> {
        val now = System.currentTimeMillis()
        return coroutineScope {
            val tasks =
                    provinceCapitals.map { city ->
                        async {
                            val cached = cache[city.id]
                            if (cached != null && now - cached.second < cacheDuration) {
                                cached.first
                            } else {
                                val weather = weatherService.getWeatherNow(city.id)
                                val cityWeather =
                                        CityWeather(
                                                id = city.id,
                                                name = city.name,
                                                lat = city.lat,
                                                lon = city.lon,
                                                icon = weather.icon,
                                                text = weather.text
                                        )
                                cache[city.id] = cityWeather to now
                                cityWeather
                            }
                        }
                    }
            tasks.map { it.await() }
        }
    }
}

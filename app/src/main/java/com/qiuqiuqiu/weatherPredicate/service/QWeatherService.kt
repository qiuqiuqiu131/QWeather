package com.qiuqiuqiu.weatherPredicate.service

import android.content.Context
import android.util.Log
import com.qweather.sdk.Callback
import com.qweather.sdk.JWTGenerator
import com.qweather.sdk.QWeather
import com.qweather.sdk.basic.Indices
import com.qweather.sdk.basic.Lang
import com.qweather.sdk.basic.Range
import com.qweather.sdk.basic.Unit
import com.qweather.sdk.parameter.air.AirV1Parameter
import com.qweather.sdk.parameter.geo.GeoCityLookupParameter
import com.qweather.sdk.parameter.geo.GeoCityTopParameter
import com.qweather.sdk.parameter.indices.IndicesParameter
import com.qweather.sdk.parameter.warning.WarningNowParameter
import com.qweather.sdk.parameter.weather.WeatherParameter
import com.qweather.sdk.response.air.v1.AirV1CurrentResponse
import com.qweather.sdk.response.error.ErrorResponse
import com.qweather.sdk.response.geo.GeoCityLookupResponse
import com.qweather.sdk.response.geo.GeoCityTopResponse
import com.qweather.sdk.response.geo.Location
import com.qweather.sdk.response.indices.IndicesDaily
import com.qweather.sdk.response.indices.IndicesDailyResponse
import com.qweather.sdk.response.warning.Warning
import com.qweather.sdk.response.warning.WarningResponse
import com.qweather.sdk.response.weather.WeatherDaily
import com.qweather.sdk.response.weather.WeatherDailyResponse
import com.qweather.sdk.response.weather.WeatherHourly
import com.qweather.sdk.response.weather.WeatherHourlyResponse
import com.qweather.sdk.response.weather.WeatherNow
import com.qweather.sdk.response.weather.WeatherNowResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException


data class CityWeather(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val icon: String,  // 和风天气 icon code
    val text: String   // 天气描述

)

data class CityInfo(
    val name: String,
    val id: String,  // QWeather 的 locationId
    val lat: Double,
    val lon: Double
)


interface IQWeatherService {
    /**
     * 获取实时天气
     * @param locationId 位置ID，经纬度坐标/城市ID
     */
    suspend fun getWeatherNow(locationId: String): WeatherNow

    /**
     * 获取近24小时天气
     * @param locationId 位置ID，经纬度坐标/城市ID
     */
    suspend fun getWeather24Hour(locationId: String): List<WeatherHourly>

    /**
     * 获取近三天的天气
     * @param locationId 位置ID，经纬度坐标/城市ID
     */
    suspend fun getWeather3Day(locationId: String): List<WeatherDaily>

    /**
     * 获取近七天的天气
     * @param locationId 位置ID，经纬度坐标/城市ID
     */
    suspend fun getWeather7Day(locationId: String): List<WeatherDaily>

    /**
     * 获取当天的生活指数
     * @param locationId 位置ID，经纬度坐标/城市ID
     */
    suspend fun getWeatherIndices(locationId: String): List<IndicesDaily>

    /**
     * 获取实时预警
     * @param locationId 位置ID，经纬度坐标/城市ID
     */
    suspend fun getWarningNow(locationId: String): List<Warning>

    /**
     * 获取实时空气质量
     * @param locationId 经纬度坐标
     */
    suspend fun getAirCurrent(longitude: Double, latitude: Double): AirV1CurrentResponse

    /**
     * 获取城市信息
     * @param locationId 位置ID，经纬度坐标/模糊描述
     * @param adm 上级行政区划，可选，如 beijing
     * @param range 范围，可选，如 Range.CN 限定为中国范围内
     */
    suspend fun getCurrentCity(
        locationId: String,
        adm: String? = null,
        range: Range? = null,
        number: Int? = null
    ): List<Location>

    /**
     * 获取热门城市
     * @param number 数量，最大支持20
     * @param range 范围，可选，如 Range.CN 限定为中国范围内
     */
    suspend fun getCityTop(
        number: Int,
        range: Range? = null
    ): List<Location>
}

val provinceCapitals = listOf(
    CityInfo("北京", "101010100", 39.9042, 116.4074),
    CityInfo("天津", "101030100", 39.3434, 117.3616),
    CityInfo("上海", "101020100", 31.2304, 121.4737),
    CityInfo("重庆", "101040100", 29.4316, 106.9123),
    CityInfo("石家庄", "101090101", 38.0428, 114.5149),
    CityInfo("太原", "101100101", 37.8706, 112.5489),
    CityInfo("呼和浩特", "101080101", 40.817, 111.7652),
    CityInfo("沈阳", "101070101", 41.8057, 123.4315),
    CityInfo("长春", "101060101", 43.816, 125.3235),
    CityInfo("哈尔滨", "101050101", 45.8038, 126.5349),
    CityInfo("南京", "101190101", 32.0617, 118.7778),
    CityInfo("杭州", "101210101", 30.245, 120.2039),
    CityInfo("合肥", "101220101", 31.8612, 117.2858),
    CityInfo("福州", "101230101", 26.0614, 119.3062),
    CityInfo("南昌", "101240101", 28.6829, 115.8582),
    CityInfo("济南", "101120101", 36.6502, 117.1201),
    CityInfo("郑州", "101180101", 34.7473, 113.6248),
    CityInfo("武汉", "101200101", 30.5931, 114.3054),
    CityInfo("长沙", "101250101", 28.2282, 112.9388),
    CityInfo("广州", "101280101", 23.1252, 113.2806),
    CityInfo("南宁", "101300101", 22.817, 108.3669),
    CityInfo("海口", "101310101", 20.044, 110.1999),
    CityInfo("成都", "101270101", 30.6594, 104.0657),
    CityInfo("贵阳", "101260101", 26.5783, 106.7135),
    CityInfo("昆明", "101290101", 24.8797, 102.8332),
    CityInfo("拉萨", "101140101", 29.652, 91.1721),
    CityInfo("西安", "101110101", 34.3416, 108.9398),
    CityInfo("兰州", "101160101", 36.0611, 103.8343),
    CityInfo("西宁", "101150101", 36.6171, 101.7779),
    CityInfo("银川", "101170101", 38.4872, 106.2309),
    CityInfo("乌鲁木齐", "101130101", 43.8256, 87.6169),
    CityInfo("台北", "101340101", 25.033, 121.5654),
    CityInfo("香港", "101320101", 22.3964, 114.1095),
    CityInfo("澳门", "101330101", 22.203, 113.545)
)
data class LatLngWeather(
    val temp: String,
    val text: String,
    val aqi: String,
    val category: String
)

@OptIn(ExperimentalCoroutinesApi::class)
class QWeatherService @Inject constructor(@ApplicationContext private val context: Context) :
    IQWeatherService {
    private val instance: QWeather
    private val TAG: String = "QWeatherExampleMainActivity"

    init {
        try {
            val jwt = JWTGenerator(
                "MC4CAQAwBQYDK2VwBCIEIHfPWEdN9zfEzH7TWR38/NvBtVcRhy76a8GoGdv5WNHr",
                "2EKT9P93UX",
                "CC5BTDE5JF"
            )
            instance = QWeather.getInstance(context, "nq6r6xdaqp.re.qweatherapi.com")
                .setTokenGenerator(jwt)
                .setLogEnable(true)
        } catch (e: Exception) {
            throw Exception("QWeather init failed")
        }
    }

    override suspend fun getWeatherNow(locationId: String): WeatherNow {
        val parameter = WeatherParameter(locationId)
            .lang(Lang.ZH_HANS).unit(Unit.METRIC)
        return suspendCancellableCoroutine { cont ->
            instance.weatherNow(parameter, object : Callback<WeatherNowResponse> {
                override fun onSuccess(response: WeatherNowResponse) {
                    cont.resume(response.now, null)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    Log.e(TAG, "getWeatherNow onFailure: $errorResponse")
                    cont.resumeWithException(Exception(errorResponse.toString()))
                }

                override fun onException(e: Throwable) {
                    Log.e(TAG, "getWeatherNow onException: $e")
                    cont.resumeWithException(e)
                }
            })
        }
    }

    override suspend fun getWeather24Hour(locationId: String): List<WeatherHourly> {
        val parameter = WeatherParameter(locationId)
            .lang(Lang.ZH_HANS).unit(Unit.METRIC)
        return suspendCancellableCoroutine { cont ->
            instance.weather24h(parameter, object : Callback<WeatherHourlyResponse> {
                override fun onSuccess(response: WeatherHourlyResponse) {
                    cont.resume(response.hourly, null)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    Log.e(TAG, "getWeatherNow onFailure: $errorResponse")
                    cont.resumeWithException(Exception(errorResponse.toString()))
                }

                override fun onException(e: Throwable) {
                    Log.e(TAG, "getWeatherNow onException: $e")
                    cont.resumeWithException(e)
                }
            })
        }
    }

    override suspend fun getWeather3Day(locationId: String): List<WeatherDaily> {
        val parameter = WeatherParameter(locationId)
            .lang(Lang.ZH_HANS).unit(Unit.METRIC)
        return suspendCancellableCoroutine { cont ->
            instance.weather3d(parameter, object : Callback<WeatherDailyResponse> {
                override fun onSuccess(response: WeatherDailyResponse) {
                    cont.resume(response.daily, null)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    Log.e(TAG, "getWeatherNow onFailure: $errorResponse")
                    cont.resumeWithException(Exception(errorResponse.toString()))
                }

                override fun onException(e: Throwable) {
                    Log.e(TAG, "getWeatherNow onException: $e")
                    cont.resumeWithException(e)
                }
            })
        }
    }

    override suspend fun getWeather7Day(locationId: String): List<WeatherDaily> {
        val parameter = WeatherParameter(locationId)
            .lang(Lang.ZH_HANS).unit(Unit.METRIC)
        return suspendCancellableCoroutine { cont ->
            instance.weather7d(parameter, object : Callback<WeatherDailyResponse> {
                override fun onSuccess(response: WeatherDailyResponse) {
                    cont.resume(response.daily, null)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    Log.e(TAG, "getWeatherNow onFailure: $errorResponse")
                    cont.resumeWithException(Exception(errorResponse.toString()))
                }

                override fun onException(e: Throwable) {
                    Log.e(TAG, "getWeatherNow onException: $e")
                    cont.resumeWithException(e)
                }
            })
        }
    }

    override suspend fun getWeatherIndices(locationId: String): List<IndicesDaily> {
        val parameter = IndicesParameter(locationId, Indices.ALL)
            .lang(Lang.ZH_HANS)
        return suspendCancellableCoroutine { cont ->
            instance.indices1d(parameter, object : Callback<IndicesDailyResponse> {
                override fun onSuccess(response: IndicesDailyResponse) {
                    cont.resume(response.daily, null)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    Log.e(TAG, "getWeatherNow onFailure: $errorResponse")
                    cont.resumeWithException(Exception(errorResponse.toString()))
                }

                override fun onException(e: Throwable) {
                    Log.e(TAG, "getWeatherNow onException: $e")
                    cont.resumeWithException(e)
                }
            })
        }
    }

    override suspend fun getWarningNow(locationId: String): List<Warning> {
        val parameter = WarningNowParameter(locationId)
            .lang(Lang.ZH_HANS)
        return suspendCancellableCoroutine { cont ->
            instance.warningNow(parameter, object : Callback<WarningResponse> {
                override fun onSuccess(response: WarningResponse) {
                    cont.resume(response.warning, null)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    Log.e(TAG, "getWeatherNow onFailure: $errorResponse")
                    cont.resumeWithException(Exception(errorResponse.toString()))
                }

                override fun onException(e: Throwable) {
                    Log.e(TAG, "getWeatherNow onException: $e")
                    cont.resumeWithException(e)
                }
            })
        }
    }

    override suspend fun getAirCurrent(longitude: Double, latitude: Double): AirV1CurrentResponse {
        val parameter = AirV1Parameter(latitude, longitude).setLang(Lang.ZH_HANS)
        return suspendCancellableCoroutine { cont ->
            instance.airCurrent(parameter, object : Callback<AirV1CurrentResponse> {
                override fun onSuccess(response: AirV1CurrentResponse) {
                    cont.resume(response, null)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    Log.e(TAG, "getWeatherNow onFailure: $errorResponse")
                    cont.resumeWithException(Exception(errorResponse.toString()))
                }

                override fun onException(e: Throwable) {
                    Log.e(TAG, "getWeatherNow onException: $e")
                    cont.resumeWithException(e)
                }
            })
        }
    }

    override suspend fun getCurrentCity(
        locationId: String,
        adm: String?,
        range: Range?,
        number: Int?
    ): List<Location> {
        var parameter = GeoCityLookupParameter(locationId)
            .lang(Lang.ZH_HANS)
        if (range != null)
            parameter = parameter.range(range)
        if (adm != null)
            parameter = parameter.adm(adm)
        if (number != null)
            parameter = parameter.number(number)


        return suspendCancellableCoroutine { cont ->
            instance.geoCityLookup(parameter, object : Callback<GeoCityLookupResponse> {
                override fun onSuccess(response: GeoCityLookupResponse) {
                    cont.resume(response.location, null)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    Log.e(TAG, "getWeatherNow onFailure: $errorResponse")
                    cont.resumeWithException(Exception(errorResponse.toString()))
                }

                override fun onException(e: Throwable) {
                    Log.e(TAG, "getWeatherNow onException: $e")
                    cont.resumeWithException(e)
                }
            })
        }
    }

    override suspend fun getCityTop(number: Int, range: Range?): List<Location> {
        var parameter = GeoCityTopParameter().number(number)
            .lang(Lang.ZH_HANS)
        if (range != null)
            parameter = parameter.range(range)

        return suspendCancellableCoroutine { cont ->
            instance.geoCityTop(parameter, object : Callback<GeoCityTopResponse> {
                override fun onSuccess(response: GeoCityTopResponse) {
                    cont.resume(response.topCityList, null)
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    Log.e(TAG, "getWeatherNow onFailure: $errorResponse")
                    cont.resumeWithException(Exception(errorResponse.toString()))
                }

                override fun onException(e: Throwable) {
                    Log.e(TAG, "getWeatherNow onException: $e")
                    cont.resumeWithException(e)
                }
            })
        }
    }

    //获取省会城市天气
    suspend fun getManualCitiesWeather(): List<com.qiuqiuqiu.weatherPredicate.service.CityWeather> {
        val result = mutableListOf<com.qiuqiuqiu.weatherPredicate.service.CityWeather>()
        for (city in provinceCapitals) {
            try {
                val weather = getWeatherNow(city.id)
                result.add(
                    CityWeather(
                        id = city.id,
                        name = city.name,
                        lat = city.lat,
                        lon = city.lon,
                        icon = weather.icon,
                        text = weather.text
                    )
                )
            } catch (e: Exception) {
                Log.e("QWeatherService", "获取 ${city.name} 天气失败: ${e.message}")
            }
        }
        return result
    }


}



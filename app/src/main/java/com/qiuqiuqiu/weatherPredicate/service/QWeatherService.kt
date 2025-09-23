package com.qiuqiuqiu.weatherPredicate.service

import android.content.Context
import android.util.Log
import com.qweather.sdk.Callback
import com.qweather.sdk.JWTGenerator
import com.qweather.sdk.QWeather
import com.qweather.sdk.basic.Indices
import com.qweather.sdk.basic.Lang
import com.qweather.sdk.basic.Poi
import com.qweather.sdk.basic.Range
import com.qweather.sdk.basic.Unit
import com.qweather.sdk.parameter.air.AirV1Parameter
import com.qweather.sdk.parameter.geo.GeoCityLookupParameter
import com.qweather.sdk.parameter.geo.GeoCityTopParameter
import com.qweather.sdk.parameter.geo.GeoPoiRangeParameter
import com.qweather.sdk.parameter.grid.GridWeatherParameter
import com.qweather.sdk.parameter.indices.IndicesParameter
import com.qweather.sdk.parameter.warning.WarningNowParameter
import com.qweather.sdk.parameter.weather.WeatherParameter
import com.qweather.sdk.response.air.v1.AirDaily
import com.qweather.sdk.response.air.v1.AirHourly
import com.qweather.sdk.response.air.v1.AirV1CurrentResponse
import com.qweather.sdk.response.air.v1.AirV1DailyResponse
import com.qweather.sdk.response.air.v1.AirV1HourlyResponse
import com.qweather.sdk.response.error.ErrorResponse
import com.qweather.sdk.response.geo.GeoCityLookupResponse
import com.qweather.sdk.response.geo.GeoCityTopResponse
import com.qweather.sdk.response.geo.GeoPoiResponse
import com.qweather.sdk.response.geo.Location
import com.qweather.sdk.response.grid.GridNow
import com.qweather.sdk.response.grid.GridNowResponse
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
     * 获取168小时天气
     */
    suspend fun getWeather168Hour(locationId: String): List<WeatherHourly>

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
     * 获取多日的天气
     */
    suspend fun getWeatherMoreDay(locationId: String): List<WeatherDaily>

    /**
     * 获取当天的生活指数
     * @param locationId 位置ID，经纬度坐标/城市ID
     */
    suspend fun getWeatherIndices(locationId: String): List<IndicesDaily>

    /**
     * 获取三天的生活指数
     * @param locationId 位置ID，经纬度坐标/城市ID
     */
    suspend fun getWeatherIndices3Day(locationId: String): List<IndicesDaily>

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
     * 获取逐小时空气质量
     * @param locationId 经纬度坐标
     */
    suspend fun getAirHourly(longitude: Double, latitude: Double): List<AirHourly>

    /**
     * 获取逐日空气质量
     * @param locationId 经纬度坐标
     */
    suspend fun getAirDaily(longitude: Double, latitude: Double): List<AirDaily>

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

    /**
     * 获取周边POI
     * @param longitude 经度
     * @param latitude 纬度
     * @param range 范围，单位km，默认1km，最大支持20km
     * @param number 数量，默认10个，最大支持20个
     */
    suspend fun getPoi(
        longitude: String,
        latitude: String,
        range: Int = 10,
        number: Int = 10
    ): List<Location>

    suspend fun getGridCurrentWeather(
        longitude: Double,
        latitude: Double
    ): GridNow
}


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
                .setLogEnable(false)
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

    override suspend fun getWeather168Hour(locationId: String): List<WeatherHourly> {
        val parameter = WeatherParameter(locationId)
            .lang(Lang.ZH_HANS).unit(Unit.METRIC)
        return suspendCancellableCoroutine { cont ->
            instance.weather168h(parameter, object : Callback<WeatherHourlyResponse> {
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

    override suspend fun getWeatherMoreDay(locationId: String): List<WeatherDaily> {
        val parameter = WeatherParameter(locationId)
            .lang(Lang.ZH_HANS).unit(Unit.METRIC)
        return suspendCancellableCoroutine { cont ->
            instance.weather30d(parameter, object : Callback<WeatherDailyResponse> {
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

    override suspend fun getWeatherIndices3Day(locationId: String): List<IndicesDaily> {
        val parameter = IndicesParameter(locationId, Indices.ALL)
            .lang(Lang.ZH_HANS)
        return suspendCancellableCoroutine { cont ->
            instance.indices3d(parameter, object : Callback<IndicesDailyResponse> {
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

    override suspend fun getAirHourly(
        longitude: Double,
        latitude: Double
    ): List<AirHourly> {
        val parameter = AirV1Parameter(latitude, longitude).setLang(Lang.ZH_HANS)
        return suspendCancellableCoroutine { cont ->
            instance.airHourly(parameter, object : Callback<AirV1HourlyResponse> {
                override fun onSuccess(response: AirV1HourlyResponse) {
                    cont.resume(response.hours, null)
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

    override suspend fun getAirDaily(
        longitude: Double,
        latitude: Double
    ): List<AirDaily> {
        val parameter = AirV1Parameter(latitude, longitude).setLang(Lang.ZH_HANS)
        return suspendCancellableCoroutine { cont ->
            instance.airDaily(parameter, object : Callback<AirV1DailyResponse> {
                override fun onSuccess(response: AirV1DailyResponse) {
                    cont.resume(response.days, null)
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

    override suspend fun getPoi(
        longitude: String,
        latitude: String,
        range: Int,
        number: Int
    ): List<Location> {
        var parameter = GeoPoiRangeParameter("$longitude,$latitude", Poi.SCENIC)
            .number(number).radius(range)
            .lang(Lang.ZH_HANS)

        return suspendCancellableCoroutine { cont ->
            instance.geoPoiRange(parameter, object : Callback<GeoPoiResponse> {
                override fun onSuccess(response: GeoPoiResponse) {
                    cont.resume(response.poi, null)
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

    override suspend fun getGridCurrentWeather(longitude: Double, latitude: Double): GridNow {
        var parameter = GridWeatherParameter(longitude, latitude)
            .lang(Lang.ZH_HANS)

        return suspendCancellableCoroutine { cont ->
            instance.gridNow(parameter, object : Callback<GridNowResponse> {
                override fun onSuccess(response: GridNowResponse) {
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


}
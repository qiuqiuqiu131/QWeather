package com.qiuqiuqiu.weatherPredicate.data

data class WeatherNow(
    val obsTime: String, //
    val feelsLike: String,
    val temp: String,
    val icon: String,
    val text: String,
    val wind360: String,
    val windDir: String,
    val windScale: String,
    val windSpeed: String,
    val humidity: String,
    val precip: String,
    val pressure: String,
    val vis: String,
    val cloud: String,
    val dew: String
)

fun WeatherNow.toQWeather(): com.qweather.sdk.response.weather.WeatherNow {
    val weatherNow = com.qweather.sdk.response.weather.WeatherNow()
    weatherNow.obsTime = this.obsTime
    weatherNow.feelsLike = this.feelsLike
    weatherNow.temp = this.temp
    weatherNow.icon = this.icon
    weatherNow.text = this.text
    weatherNow.wind360 = this.wind360
    weatherNow.windDir = this.windDir
    weatherNow.windScale = this.windScale
    weatherNow.windSpeed = this.windSpeed
    weatherNow.humidity = this.humidity
    weatherNow.precip = this.precip
    weatherNow.pressure = this.pressure
    weatherNow.vis = this.vis
    weatherNow.cloud = this.cloud
    weatherNow.dew = this.dew
    return weatherNow
}

fun com.qweather.sdk.response.weather.WeatherNow.toData(): WeatherNow {
    return WeatherNow(
        obsTime = this.obsTime ?: "",
        feelsLike = this.feelsLike ?: "",
        temp = this.temp ?: "",
        icon = this.icon ?: "",
        text = this.text ?: "",
        wind360 = this.wind360 ?: "",
        windDir = this.windDir ?: "",
        windScale = this.windScale ?: "",
        windSpeed = this.windSpeed ?: "",
        humidity = this.humidity ?: "",
        precip = this.precip ?: "",
        pressure = this.pressure ?: "",
        vis = this.vis ?: "",
        cloud = this.cloud ?: "",
        dew = this.dew ?: ""
    )
}
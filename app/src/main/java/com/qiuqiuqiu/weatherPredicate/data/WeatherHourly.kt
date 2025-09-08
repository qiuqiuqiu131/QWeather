package com.qiuqiuqiu.weatherPredicate.data

data class WeatherHourly(
    val fxTime: String, //
    val temp: String,
    val icon: String,
    val text: String,
    val wind360: String,
    val windDir: String,
    val windScale: String,
    val windSpeed: String,
    val humidity: String,
    val pop: String,
    val precip: String,
    val pressure: String,
    val cloud: String,
    val dew: String
)

fun WeatherHourly.toQWeather(): com.qweather.sdk.response.weather.WeatherHourly {
    val weatherHourly = com.qweather.sdk.response.weather.WeatherHourly()
    weatherHourly.fxTime = this.fxTime
    weatherHourly.temp = this.temp
    weatherHourly.icon = this.icon
    weatherHourly.text = this.text
    weatherHourly.wind360 = this.wind360
    weatherHourly.windDir = this.windDir
    weatherHourly.windScale = this.windScale
    weatherHourly.windSpeed = this.windSpeed
    weatherHourly.humidity = this.humidity
    weatherHourly.pop = this.pop
    weatherHourly.precip = this.precip
    weatherHourly.pressure = this.pressure
    weatherHourly.cloud = this.cloud
    weatherHourly.dew = this.dew
    return weatherHourly
}

fun com.qweather.sdk.response.weather.WeatherHourly.toData(): WeatherHourly {
    return WeatherHourly(
        fxTime = this.fxTime ?: "",
        temp = this.temp ?: "",
        icon = this.icon ?: "",
        text = this.text ?: "",
        wind360 = this.wind360 ?: "",
        windDir = this.windDir ?: "",
        windScale = this.windScale ?: "",
        windSpeed = this.windSpeed ?: "",
        humidity = this.humidity ?: "",
        pop = this.pop ?: "",
        precip = this.precip ?: "",
        pressure = this.pressure ?: "",
        cloud = this.cloud ?: "",
        dew = this.dew ?: ""
    )
}

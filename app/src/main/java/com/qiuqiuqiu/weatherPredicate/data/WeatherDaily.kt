package com.qiuqiuqiu.weatherPredicate.data

data class WeatherDaily(
    val fxDate: String, //
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val moonPhase: String,
    val moonPhaseIcon: String,
    val tempMax: String,
    val tempMin: String,
    val iconDay: String,
    val textDay: String,
    val iconNight: String,
    val textNight: String,
    val wind360Day: String,
    val windDirDay: String,
    val windScaleDay: String,
    val windSpeedDay: String,
    val wind360Night: String,
    val windDirNight: String,
    val windScaleNight: String,
    val windSpeedNight: String,
    val humidity: String,
    val precip: String,
    val pressure: String,
    val vis: String,
    val cloud: String,
    val uvIndex: String
)

fun WeatherDaily.toQWeather(): com.qweather.sdk.response.weather.WeatherDaily {
    val weatherDaily = com.qweather.sdk.response.weather.WeatherDaily()
    weatherDaily.fxDate = this.fxDate
    weatherDaily.sunrise = this.sunrise
    weatherDaily.sunset = this.sunset
    weatherDaily.moonrise = this.moonrise
    weatherDaily.moonset = this.moonset
    weatherDaily.moonPhase = this.moonPhase
    weatherDaily.moonPhaseIcon = this.moonPhaseIcon
    weatherDaily.tempMax = this.tempMax
    weatherDaily.tempMin = this.tempMin
    weatherDaily.iconDay = this.iconDay
    weatherDaily.textDay = this.textDay
    weatherDaily.iconNight = this.iconNight
    weatherDaily.textNight = this.textNight
    weatherDaily.wind360Day = this.wind360Day
    weatherDaily.windDirDay = this.windDirDay
    weatherDaily.windScaleDay = this.windScaleDay
    weatherDaily.windSpeedDay = this.windSpeedDay
    weatherDaily.wind360Night = this.wind360Night
    weatherDaily.windDirNight = this.windDirNight
    weatherDaily.windScaleNight = this.windScaleNight
    weatherDaily.windSpeedNight = this.windSpeedNight
    weatherDaily.humidity = this.humidity
    weatherDaily.precip = this.precip
    weatherDaily.pressure = this.pressure
    weatherDaily.vis = this.vis
    weatherDaily.cloud = this.cloud
    weatherDaily.uvIndex = this.uvIndex
    return weatherDaily
}

fun com.qweather.sdk.response.weather.WeatherDaily.toData(): WeatherDaily {
    return WeatherDaily(
        fxDate = this.fxDate ?: "",
        sunrise = this.sunrise ?: "",
        sunset = this.sunset ?: "",
        moonrise = this.moonrise ?: "",
        moonset = this.moonset ?: "",
        moonPhase = this.moonPhase ?: "",
        moonPhaseIcon = this.moonPhaseIcon ?: "",
        tempMax = this.tempMax ?: "",
        tempMin = this.tempMin ?: "",
        iconDay = this.iconDay ?: "",
        textDay = this.textDay ?: "",
        iconNight = this.iconNight ?: "",
        textNight = this.textNight ?: "",
        wind360Day = this.wind360Day ?: "",
        windDirDay = this.windDirDay ?: "",
        windScaleDay = this.windScaleDay ?: "",
        windSpeedDay = this.windSpeedDay ?: "",
        wind360Night = this.wind360Night ?: "",
        windDirNight = this.windDirNight ?: "",
        windScaleNight = this.windScaleNight ?: "",
        windSpeedNight = this.windSpeedNight ?: "",
        humidity = this.humidity ?: "",
        precip = this.precip ?: "",
        pressure = this.pressure ?: "",
        vis = this.vis ?: "",
        cloud = this.cloud ?: "",
        uvIndex = this.uvIndex ?: ""
    )
}
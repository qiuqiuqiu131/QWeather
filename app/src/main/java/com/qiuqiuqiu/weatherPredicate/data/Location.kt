package com.qiuqiuqiu.weatherPredicate.data

data class Location(
    val name: String,
    val id: String, //
    val lat: String,
    val lon: String,
    val adm2: String,
    val adm1: String,
    val country: String,
    val tz: String,
    val utcOffset: String,
    val isDst: String,
    val type: String,
    val rank: String,
    val fxLink: String
)

fun Location.toQWeather(): com.qweather.sdk.response.geo.Location {
    val location = com.qweather.sdk.response.geo.Location()
    location.name = this.name
    location.id = this.id
    location.lat = this.lat
    location.lon = this.lon
    location.adm2 = this.adm2
    location.adm1 = this.adm1
    location.country = this.country
    location.tz = this.tz
    location.utcOffset = this.utcOffset
    location.isDst = this.isDst
    location.type = this.type
    location.rank = this.rank
    location.fxLink = this.fxLink
    return location
}

fun com.qweather.sdk.response.geo.Location.toData(): Location {
    return Location(
        name = this.name ?: "",
        id = this.id ?: "",
        lat = this.lat ?: "",
        lon = this.lon ?: "",
        adm2 = this.adm2 ?: "",
        adm1 = this.adm1 ?: "",
        country = this.country ?: "",
        tz = this.tz ?: "",
        utcOffset = this.utcOffset ?: "",
        isDst = this.isDst ?: "",
        type = this.type ?: "",
        rank = this.rank ?: "",
        fxLink = this.fxLink ?: ""
    )
}
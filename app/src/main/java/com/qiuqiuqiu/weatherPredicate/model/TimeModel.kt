package com.qiuqiuqiu.weatherPredicate.model

data class TimeResult(
    val country: String,
    val encountry: String?,
    val countrycode: String?,
    val countryezm: String?,
    val city: String,
    val encity: String?,
    val timeZone: String,
    val nowmonth: String?,
    val ennnowmonth: String?,
    val week: String,
    val enweek: String?,
    val weeknum: String?,
    val noon: String?,
    val ennoon: String?,
    val summertime: Int?,
    val cursummertime: Int?,
    val timestamp: Long?,
    val strtime: String
)

data class TimeResponse(
    val code: Int,
    val msg: String,
    val result: TimeResult?
)

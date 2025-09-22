package com.qiuqiuqiu.weatherPredicate.model

data class JieJiaRiResponse(
    val code: Int,
    val msg: String,
    val result: JieJiaRiResult?
)

data class JieJiaRiResult(
    val list: List<HolidayInfo>
)

data class HolidayInfo(
    val date: String,
    val daycode: Int,
    val weekday: Int,
    val cnweekday: String,
    val lunaryear: String,
    val lunarmonth: String,
    val lunarday: String,
    val info: String,
    val start: Int,
    val now: Int,
    val end: Int,
    val holiday: String,
    val name: String,
    val enname: String,
    val isnotwork: Int,
    val vacation: List<String>,
    val remark: List<String>,
    val wage: Int,
    val tip: String,
    val rest: String
)

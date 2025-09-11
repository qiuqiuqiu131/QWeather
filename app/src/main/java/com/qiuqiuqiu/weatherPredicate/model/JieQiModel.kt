package com.qiuqiuqiu.weatherPredicate.model

data class JieQiResponse(
    val code: Int,
    val msg: String,
    val result: JieQiResult
)

data class JieQiResult(
    val name: String,
    val nameimg: String,
    val day: String,
    val date: JieQiDate,
    val yuanyin: String,
    val shiju: String,
    val jieshao: String,
    val xishu: String,
    val meishi: String,
    val yiji: String
)

data class JieQiDate(
    val gregdate: String,
    val lunardate: String,
    val cnyear: String,
    val cnmonth: String,
    val cnday: String,
    val cnzodiac: String
)
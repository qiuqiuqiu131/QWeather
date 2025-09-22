package com.qiuqiuqiu.weatherPredicate.model

data class LunarResponse(
    val code: Int,
    val msg: String,
    val result: LunarResult
)

data class LunarResult(
    val gregoriandate: String,
    val lunardate: String,
    val lunar_festival: String?,
    val festival: String?,
    val fitness: String?,
    val taboo: String?,
    val shenwei: String?,
    val taishen: String?,
    val chongsha: String?,
    val suisha: String?,
    val wuxingjiazi: String?,
    val wuxingnayear: String?,
    val wuxingnamonth: String?,
    val xingsu: String?,
    val pengzu: String?,
    val jianshen: String?,
    val tiangandizhiyear: String?,
    val tiangandizhimonth: String?,
    val tiangandizhiday: String?,
    val lmonthname: String?,
    val shengxiao: String?,
    val lubarmonth: String?,
    val lunarday: String?,
    val jieqi: String?
)

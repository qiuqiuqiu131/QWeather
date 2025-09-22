package com.qiuqiuqiu.weatherPredicate.model

data class DailyFortuneResponse(
    val code: Int,
    val msg: String,
    val result: DailyFortuneResult,
)

data class StarModel(
    val name: String,
    val content: DailyFortuneResult,
)

data class DailyFortuneResult(
    val list: List<DailyFortuneItem>
)

data class DailyFortuneItem(
    val type: String,
    val content: String
)


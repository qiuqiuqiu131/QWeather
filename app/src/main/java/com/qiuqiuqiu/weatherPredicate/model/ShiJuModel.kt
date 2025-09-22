package com.qiuqiuqiu.weatherPredicate.model

data class ShiJuResponse(
    val code: Int,
    val msg: String,
    val result: ShiJuResult
)

data class ShiJuResult(
    val tqtype: Int,
    val content: String,
    val author: String,
    val source: String

)


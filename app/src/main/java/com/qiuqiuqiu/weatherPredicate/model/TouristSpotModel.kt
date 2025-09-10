package com.qiuqiuqiu.weatherPredicate.model

data class TouristSpotResponse(
    val code: Int,
    val msg: String,
    val result: TouristSpotResult
)

data class TouristSpotResult(
    val list: List<TouristSpotItem>
)

data class TouristSpotItem(
    val name: String,
    val content: String,
    val province: String,
    val city: String
)

package com.qiuqiuqiu.weatherPredicate.model


data class AllNewsResponse(
    val code: Int,
    val msg: String,
    val result: AllNewsResult
)

data class AllNewsResult(
    val curpage: Int,
    val allnum: Int,
    val list: List<AllNewsItem>
)

data class AllNewsItem(
    val id: String,
    val ctime: String,
    val title: String,
    val description: String,
    val source: String,
    val picUrl: String,
    val url: String
)

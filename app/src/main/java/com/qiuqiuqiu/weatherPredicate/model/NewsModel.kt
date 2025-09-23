package com.qiuqiuqiu.weatherPredicate.model

data class NewsResponse(
    val code: Int,
    val msg: String,
    val result: NewsResult?
)

data class NewsResult(
    val curpage: Int,
    val allnum: Int,
    val list: List<NewsItem>
)

data class NewsItem(
    val id: String,
    val ctime: String,
    val title: String,
    val description: String,
    val source: String,
    val picUrl: String?,
    val url: String
)
package com.qiuqiuqiu.weatherPredicate.model

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    val code: Int,
    val msg: String,
    val result: NewsResult?
)

data class NewsResult(
    val curpage: Int,
    val allnum: Int,
    @SerializedName("newslist")
    val newslist: List<NewsItem>? = null
)



data class NewsItem(
    val id: String,
    val ctime: String,
    val title: String,
    val description: String,
    val source: String,
    @SerializedName("picUrl") val picUrl: String? = null,
    val url: String
)

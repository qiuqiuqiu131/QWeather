package com.qiuqiuqiu.weatherPredicate.data

data class IndicesDaily(
    val date: String, //
    val type: String, //
    val name: String,
    val level: String,
    val category: String,
    val text: String
)

fun IndicesDaily.toQWeather(): com.qweather.sdk.response.indices.IndicesDaily {
    val indicesDaily = com.qweather.sdk.response.indices.IndicesDaily()
    indicesDaily.date = this.date
    indicesDaily.type = this.type
    indicesDaily.name = this.name
    indicesDaily.level = this.level
    indicesDaily.category = this.category
    indicesDaily.text = this.text
    return indicesDaily
}

fun com.qweather.sdk.response.indices.IndicesDaily.toData(): IndicesDaily {
    return IndicesDaily(
        date = this.date ?: "",
        type = this.type ?: "",
        name = this.name ?: "",
        level = this.level ?: "",
        category = this.category ?: "",
        text = this.text ?: ""
    )
}
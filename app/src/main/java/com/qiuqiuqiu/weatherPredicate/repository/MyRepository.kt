package com.qiuqiuqiu.weatherPredicate.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.qiuqiuqiu.weatherPredicate.model.JieQiResponse
import com.qiuqiuqiu.weatherPredicate.model.TimeResponse
import com.qiuqiuqiu.weatherPredicate.model.TouristSpotResponse
import com.qiuqiuqiu.weatherPredicate.network.TianApiCities
import com.qiuqiuqiu.weatherPredicate.service.ApiKeyProvider
import com.qiuqiuqiu.weatherPredicate.viewModel.TourSearchType
import javax.inject.Inject
import javax.inject.Singleton

// --------- Repository ---------
@Singleton
class TimeRepository @Inject constructor(
    private val api: TianApiCities,
    private val apiKeyProvider: ApiKeyProvider
) {
    suspend fun getWorldTime(city: String): TimeResponse {
        return api.getWorldTime(apiKeyProvider.key, city)
    }
}

@Singleton
class JieQiRepository @Inject constructor(
    private val api: TianApiCities,
    private val apiKeyProvider: ApiKeyProvider
) {
    suspend fun getJieQi(word: String,year:String): JieQiResponse? {
        val response = api.getJieQi(apiKeyProvider.key,word,year)
        return response
    }
}

@Singleton
class TourRepository @Inject constructor(
    private val api: TianApiCities,
    private val apiKeyProvider: ApiKeyProvider
) {

    /**
     * 三种搜索模式只允许选其一，未选择任何条件则返回 null
     * @param word 景点名称
     * @param province 省份
     * @param city 城市
     * @param num 返回数量
     * @param page 页码
     */
    suspend fun getTourSpot(
        word: String? = null,
        province: String? = null,
        city: String? = null,
        num: Int = 10,
        page: String? = "1"
    ): TouristSpotResponse? {
        // 三种条件只能选其一
     //   Log.d(TAG, "getTourSpot params -> word='${word.orEmpty()}', province='${province.orEmpty()}', city='${city.orEmpty()}', num=$num, page='$page'")
        val nonEmptyFields = listOf(word, province, city).count { !it.isNullOrBlank() }
        if (nonEmptyFields == 0) return null // 没有搜索条件
        if (nonEmptyFields > 1) throw IllegalArgumentException("只能选择一种搜索条件")
        val w = word?.trim().takeIf { !it.isNullOrEmpty() }
        val p = province?.trim().takeIf { !it.isNullOrEmpty() }
        val c = city?.trim().takeIf { !it.isNullOrEmpty() }
        val pg = page?.trim().takeIf { !it.isNullOrEmpty() } ?: "1"
        return api.getTour(
            key = apiKeyProvider.key,
            word = w,
            num = num,
            page = pg,
            province = p,
            city = c
        )

    }


    suspend fun getTourSpotByType(type: TourSearchType, query: String, num: Int = 10, page: String? = "1"): TouristSpotResponse? {
        val q = query.trim().takeIf { it.isNotEmpty() }
        return when (type) {
            TourSearchType.NAME -> getTourSpot(word = q, province = null, city = null, num = num, page = page)
            TourSearchType.PROVINCE -> getTourSpot(word = null, province = q, city = null, num = num, page = page)
            TourSearchType.CITY -> getTourSpot(word = null, province = null, city = q, num = num, page = page)
        }
    }
}

package com.qiuqiuqiu.weatherPredicate.network


import com.qiuqiuqiu.weatherPredicate.model.JieQiResponse
import com.qiuqiuqiu.weatherPredicate.model.TimeResponse
import com.qiuqiuqiu.weatherPredicate.model.TouristSpotResponse
import retrofit2.http.GET

import retrofit2.http.POST
import retrofit2.http.Query

interface TianApiCities {
    @GET("worldtime/index")
    suspend fun getWorldTime(
        @Query("key") key: String,
        @Query("city") city: String
    ): TimeResponse

    @GET("jieqi/index")
    suspend fun getJieQi(
        @Query("key") key: String,
        @Query("word") word: String,
        @Query("year") year:String
    ): JieQiResponse

    @GET("scenic/index")
    suspend fun getTour(
        @Query("key") key: String,
        @Query("num") num: Int,
        @Query("page") page: String,
        @Query("word") word: String? = null,
        @Query("province") province: String? = null,
        @Query("city") city: String? = null,
    ): TouristSpotResponse

}


package com.qiuqiuqiu.weatherPredicate.network



import com.qiuqiuqiu.weatherPredicate.model.AllNewsResponse
import com.qiuqiuqiu.weatherPredicate.model.DailyFortuneResponse
import com.qiuqiuqiu.weatherPredicate.model.JieQiResponse
import com.qiuqiuqiu.weatherPredicate.model.ShiJuResponse
import com.qiuqiuqiu.weatherPredicate.model.TimeResponse
import com.qiuqiuqiu.weatherPredicate.model.JieJiaRiResponse
import com.qiuqiuqiu.weatherPredicate.model.LunarResponse
import com.qiuqiuqiu.weatherPredicate.model.NewsResponse


import retrofit2.http.GET
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

    //1=风、2=云、3=雨、4=雪、5=霜、6=露 、7=雾、8=雷、9=晴、10=阴。
    @GET("tianqishiju/index")
    suspend fun getShiJu(
        @Query("key") key: String,
        @Query("tqtype") tqtype: Int
    ): ShiJuResponse

    @GET("jiejiari/index")
    suspend fun getJieJiaRi(
        @Query("key") key: String,
        @Query("date") date: String,
        @Query("type")type:Int
    ):JieJiaRiResponse

    @GET("star/index")
    suspend fun getDailyFortune(
        @Query("key") key: String,
        @Query("astro") astro: String, // 星座中文或英文名
        @Query("date") date: String? //指定日期，如2020-10-12
    ): DailyFortuneResponse

    @GET("lunar/index")
    suspend fun getLunar(
        @Query("key") key: String,
        @Query("date") date: String? = null,
        @Query("type") type: Int? = 0 //按农历查询该值为1且日期不能有前导零
    ): LunarResponse

    @GET("generalnews/index")
    suspend fun getNews(
        @Query("key") key: String,
        @Query("num") num: Int? = 10,
        @Query("page") page: Int? = 0,
        @Query("rand") rand: Int? = 0,
        @Query("word") word: String? = null,
        @Query("source") source: String? = null
    ): NewsResponse

    @GET("allnews/index")
    suspend fun getAllNews(
        @Query("key") key: String,       // 你的 API KEY
        @Query("num") num: Int = 10,     // 返回数量，1-50，默认10
        @Query("col") col: Int = 7,          // 新闻频道 ID（必须）
        @Query("page") page: Int? = 1,   // 翻页，可选
        @Query("rand") rand: Int? = 1,   // 随机获取，0->不随机，1->随机
        @Query("word") word: String? = null // 检索关键词，可选
    ): AllNewsResponse


}


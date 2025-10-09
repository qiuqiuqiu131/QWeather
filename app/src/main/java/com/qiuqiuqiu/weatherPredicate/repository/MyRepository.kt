package com.qiuqiuqiu.weatherPredicate.repository

import com.qiuqiuqiu.weatherPredicate.model.*
import com.qiuqiuqiu.weatherPredicate.network.TianApiCities
import com.qiuqiuqiu.weatherPredicate.service.ApiKeyProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository 层：统一负责调用 TianApiCities，并注入 ApiKeyProvider 获取密钥。
 * ViewModel 只需要调用 Repository 的方法，不关心 key 的来源。
 */
@Singleton
class TianRepository @Inject constructor(
    private val api: TianApiCities,
    private val apiKeyProvider: ApiKeyProvider
) {

    /**
     * 世界时间查询
     * @param city 城市名
     */
    suspend fun getWorldTime(city: String): TimeResponse {
        return api.getWorldTime(apiKeyProvider.key, city)
    }

    /**
     * 二十四节气查询
     * @param word 节气名称（如 立春）
     * @param year 年份（如 2024）
     */
    suspend fun getJieQi(word: String, year: String): JieQiResponse {
        return api.getJieQi(apiKeyProvider.key, word, year)
    }

    /**
     * 天气诗句查询
     * @param tqtype 天气类型 (1=风, 2=云, 3=雨, 4=雪, 5=霜, 6=露, 7=雾, 8=雷, 9=晴, 10=阴)
     */
    suspend fun getShiJu(tqtype: Int): ShiJuResponse {
        return api.getShiJu(apiKeyProvider.key, tqtype)
    }

    /**
     * 节假日查询
     * @param date 查询日期 (支持年、月、范围、多个日期)
     * @param type 查询类型 (0=批量, 1=按年, 2=按月, 3=范围)
     */
    suspend fun getJieJiaRi(date: String, type: Int): JieJiaRiResponse {
        return api.getJieJiaRi(apiKeyProvider.key, date, type)
    }

    /**
     * 星座运势查询
     * @param astro 星座中文或英文名 (如 "taurus"、"金牛座")
     * @param date 指定日期 (可选, 格式: yyyy-MM-dd)
     */
    suspend fun getDailyFortune(astro: String, date: String? = null): DailyFortuneResponse {
        return api.getDailyFortune(apiKeyProvider.key, astro, date)
    }

    /**
     * 农历查询
     * @param date 日期 (可选, 默认为当天, 格式 yyyy-MM-dd)
     * @param type 0=公历, 1=农历 (注意: 农历日期不能有前导零)
     */
    suspend fun getLunar(date: String? = null, type: Int? = 0): LunarResponse {
        return api.getLunar(apiKeyProvider.key, date, type)
    }

    /**
     * 新闻资讯查询
     * @param num 返回数量 (1-50, 默认10)
     * @param page 翻页 (默认0)
     * @param rand 是否随机 (0=不随机, 1=随机)
     * @param word 搜索关键词 (可选)
     * @param source 指定来源 (可选, 如 "网易新闻")
     */
    suspend fun getNews(
        num: Int? = 10,
        page: Int? = 0,
        rand: Int? = 0,
        word: String? = null,
        source: String? = null
    ): NewsResponse {
        return api.getNews(apiKeyProvider.key, num, page, rand, word, source)
    }


    /**
     * 新闻大全查询
     *
     * @param num 返回数量，1-50，默认10
     * @param col 新闻频道 ID（必填）
     * @param page 翻页，可选
     * @param rand 随机获取，0=不随机，1=随机
     * @param word 检索关键词，可选
     */
    suspend fun getAllNews(
        num: Int = 10,
        col: Int,
        page: Int? = 1,
        rand: Int? = 0,
        word: String? = null
    ): AllNewsResponse {
        return api.getAllNews(apiKeyProvider.key, num, col, page, rand, word)
    }
}

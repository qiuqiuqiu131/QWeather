package com.qiuqiuqiu.weatherPredicate.manager

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qiuqiuqiu.weatherPredicate.model.weather.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.weather.CityType
import com.qiuqiuqiu.weatherPredicate.model.weather.SearchHistory
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlin.math.abs

interface ILocalDataManager {
    suspend fun getCityList(): List<CityLocationModel>
    suspend fun saveCityList(list: List<CityLocationModel>)
    suspend fun addCity(city: Pair<Double, Double>)
    suspend fun addPositionCity(city: CityLocationModel)
    suspend fun removePositionCity()

    suspend fun getSearchHistories(): List<SearchHistory>
    suspend fun addSearchHistory(history: SearchHistory)
    suspend fun clearSearchHistories()
}

private val Context.dataStore by preferencesDataStore(name = "weather_store")
private val CITY_LIST_KEY = stringPreferencesKey("city_list")
private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")

class LocalDataManager @Inject constructor(private val context: Context) : ILocalDataManager {
    private val gson = Gson()

    // 内存缓存
    @Volatile
    private var cachedCityList: List<CityLocationModel>? = null

    override suspend fun getCityList(): List<CityLocationModel> {
        // 优先返回缓存
        cachedCityList?.let {
            return it
        }
        val prefs = context.dataStore.data.first()
        val json = prefs[CITY_LIST_KEY] ?: return emptyList()
        val list: List<CityLocationModel> =
            gson.fromJson(json, object : TypeToken<List<CityLocationModel>>() {}.type)
        cachedCityList = list
        return list
    }

    override suspend fun saveCityList(list: List<CityLocationModel>) {
        val json = gson.toJson(list)
        cachedCityList = list // 更新缓存
        context.dataStore.edit { prefs -> prefs[CITY_LIST_KEY] = json }
    }

    override suspend fun addCity(city: Pair<Double, Double>) {
        val currentList = getCityList().toMutableList()
        val exist =
            currentList.firstOrNull {
                abs(it.location.first - city.first) < 0.04 && abs(it.location.second - city.second) < 0.04
            }
        if (exist != null) return

        currentList.add(CityLocationModel(CityType.Normal, city))
        saveCityList(currentList)
    }

    override suspend fun addPositionCity(city: CityLocationModel) {
        val currentList = getCityList().toMutableList()
        currentList.removeIf { it.type == CityType.Position }
        currentList.add(0, city)
        saveCityList(currentList)
    }

    override suspend fun removePositionCity() {
        val currentList = getCityList().toMutableList()
        currentList.removeIf { it.type == CityType.Position }
        saveCityList(currentList)
    }


    @Volatile
    private var cachedSearchHistories: List<SearchHistory>? = null

    override suspend fun getSearchHistories(): List<SearchHistory> {
        cachedSearchHistories?.let {
            return it
        }
        val prefs = context.dataStore.data.first()
        val json = prefs[SEARCH_HISTORY_KEY] ?: return emptyList()
        val list: List<SearchHistory> =
            gson.fromJson(json, object : TypeToken<List<SearchHistory>>() {}.type)
        cachedSearchHistories = list
        return list
    }

    override suspend fun addSearchHistory(history: SearchHistory) {
        val currentList = getSearchHistories().toMutableList()
        currentList.removeAll { it.name == history.name }
        currentList.add(0, history) // 新的放最前面
        // 限制最大条数，比如20条
        val maxSize = 10
        if (currentList.size > maxSize)
            currentList.subList(maxSize, currentList.size).clear()
        val json = gson.toJson(currentList)
        cachedSearchHistories = currentList
        context.dataStore.edit { prefs -> prefs[SEARCH_HISTORY_KEY] = json }
    }

    override suspend fun clearSearchHistories() {
        cachedSearchHistories = emptyList()
        context.dataStore.edit { prefs -> prefs.remove(SEARCH_HISTORY_KEY) }
    }
}

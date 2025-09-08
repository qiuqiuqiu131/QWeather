package com.qiuqiuqiu.weatherPredicate.manager

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qiuqiuqiu.weatherPredicate.model.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.CityType
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

interface ILocalDataManager {
    suspend fun getCityList(): List<CityLocationModel>
    suspend fun saveCityList(list: List<CityLocationModel>)
    suspend fun addCity(city: Pair<Double, Double>): CityType
    suspend fun addPositionCity(city: Pair<Double, Double>)
}

private val Context.dataStore by preferencesDataStore(name = "city_list_store")
private val CITY_LIST_KEY = stringPreferencesKey("city_list")

class LocalDataManager @Inject constructor(private val context: Context) : ILocalDataManager {
    private val gson = Gson()

    // 内存缓存
    @Volatile
    private var cachedCityList: List<CityLocationModel>? = null

    override suspend fun getCityList(): List<CityLocationModel> {
        // 优先返回缓存
        cachedCityList?.let { return it }
        val prefs = context.dataStore.data.first()
        val json = prefs[CITY_LIST_KEY] ?: return emptyList()
        val list: List<CityLocationModel> =
            gson.fromJson(json, object : TypeToken<List<CityLocationModel>>() {}.type)
        cachedCityList = list
        return list
    }

    override suspend fun saveCityList(list: List<CityLocationModel>) {
        val json = gson.toJson(list)
        context.dataStore.edit { prefs -> prefs[CITY_LIST_KEY] = json }
        cachedCityList = list // 更新缓存
    }

    override suspend fun addCity(city: Pair<Double, Double>): CityType {
        val currentList = getCityList().toMutableList()
        val type = if (currentList.isEmpty()) CityType.Host else CityType.Normal
        currentList.add(CityLocationModel(type, city))
        saveCityList(currentList)
        return type
    }

    override suspend fun addPositionCity(city: Pair<Double, Double>) {
        val currentList = getCityList().toMutableList()
        currentList.add(0, CityLocationModel(CityType.Position, city))
        saveCityList(currentList)
    }
}

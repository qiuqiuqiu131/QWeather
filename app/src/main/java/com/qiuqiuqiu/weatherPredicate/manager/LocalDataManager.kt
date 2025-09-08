package com.qiuqiuqiu.weatherPredicate.manager

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

interface ILocalDataManager {
    suspend fun getCityList(): List<Pair<Double, Double>>
    suspend fun saveCityList(list: List<Pair<Double, Double>>)
    suspend fun addCity(city: Pair<Double, Double>)
}

private val Context.dataStore by preferencesDataStore(name = "city_list_store")
private val CITY_LIST_KEY = stringPreferencesKey("city_list")

class LocalDataManager @Inject constructor(private val context: Context) : ILocalDataManager {
    private val gson = Gson()

    override suspend fun getCityList(): List<Pair<Double, Double>> {
        val prefs = context.dataStore.data.first()
        val json = prefs[CITY_LIST_KEY] ?: return emptyList()
        return gson.fromJson(json, object : TypeToken<List<Pair<Double, Double>>>() {}.type)
    }

    override suspend fun saveCityList(list: List<Pair<Double, Double>>) {
        val json = gson.toJson(list)
        context.dataStore.edit { prefs -> prefs[CITY_LIST_KEY] = json }
    }

    override suspend fun addCity(city: Pair<Double, Double>) {
        val currentList = getCityList().toMutableList()
        currentList.add(city)
        saveCityList(currentList)
    }
}

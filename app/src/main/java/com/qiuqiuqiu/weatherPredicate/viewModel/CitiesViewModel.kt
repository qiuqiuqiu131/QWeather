package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query
import javax.inject.Inject

// --------- 数据类 ---------
data class TimeResult(
    val country: String,
    val encountry: String?,
    val countrycode: String?,
    val countryezm: String?,
    val city: String,
    val encity: String?,
    val timeZone: String,
    val nowmonth: String?,
    val ennnowmonth: String?,
    val week: String,
    val enweek: String?,
    val weeknum: String?,
    val noon: String?,
    val ennoon: String?,
    val summertime: Int?,
    val cursummertime: Int?,
    val timestamp: Long?,
    val strtime: String
)

data class TianResponse(
    val code: Int,
    val msg: String,
    val result: TimeResult?
)

// --------- Retrofit API ---------
interface TianApi {
    @POST("worldtime/index")
    suspend fun getWorldTime(
        @Query("key") key: String,
        @Query("city") city: String
    ): TianResponse
}

// --------- Repository ---------
class TimeRepository @Inject constructor() {
    private val apiKey = "6cf5ee619295aa8a8f3100d5cfd7a47c"

    private val api = Retrofit.Builder()
        .baseUrl("https://apis.tianapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TianApi::class.java)

    suspend fun getWorldTime(city: String): TianResponse {
        return api.getWorldTime(apiKey, city)
    }
}

// --------- ViewModel ---------
@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val repository: TimeRepository
) : ViewModel() {

    private val _cityResult = MutableStateFlow<TianResponse?>(null)
    val cityResult: StateFlow<TianResponse?> = _cityResult

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchCityTime(city: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getWorldTime(city)
                if (response.code == 200) {
                    _cityResult.value = response
                } else {
                    _error.value = response.msg
                    _cityResult.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
                _cityResult.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}
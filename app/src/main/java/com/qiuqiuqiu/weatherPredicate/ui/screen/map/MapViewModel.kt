package com.qiuqiuqiu.weatherPredicate.ui.screen.map

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.model.LatLng
import com.qiuqiuqiu.weatherPredicate.manager.IMapWeatherManager
import com.qiuqiuqiu.weatherPredicate.model.CityWeather
import com.qiuqiuqiu.weatherPredicate.model.CityCurrentWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapWeatherManager: IMapWeatherManager
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    /**
     * 搜索城市并移动到地图，同时返回 LatLng 回调给 UI
     */
    fun search(
        context: Context,
        mapView: MapView,
        baiduMap: BaiduMap?,
        onResult: (LatLng?) -> Unit  // ✅ 新增回调
    ) {
        val input = _query.value.trim()
        if (input.isNotBlank()) {
            val parts = input.split(Regex("\\s+"), limit = 2)
            val maybeCity = parts.getOrNull(0)?.trim().orEmpty()
            val maybeAddress = parts.getOrNull(1)?.trim().orEmpty()

            val callback: (LatLng?) -> Unit = { latLng ->
                if (latLng != null) {
                    fetchWeatherAt(latLng.latitude, latLng.longitude) // 请求天气
                }
                onResult(latLng) // ✅ 把结果传给 UI
            }

            if (maybeAddress.isNotEmpty()) {
                MapUtils.geocodeAndShow(
                    context = context,
                    mapView = mapView,
                    baiduMap = baiduMap,
                    city = maybeCity,
                    address = maybeAddress,
                    onResult = callback
                )
            } else {
                val token = maybeCity
                MapUtils.geocodeAndShow(
                    context = context,
                    mapView = mapView,
                    baiduMap = baiduMap,
                    city = token,
                    address = token,
                    fallbackAddressOnly = true,
                    onResult = callback
                )
            }
        }
    }



    // ------------------- 热门城市天气 -------------------
    suspend fun getManualCitiesWeather(): List<CityWeather> {
        return mapWeatherManager.getManualCitiesWeather()
    }

    // ------------------- 点击/搜索获取天气 -------------------
    private val _clickedWeather = MutableStateFlow<CityCurrentWeather?>(null)
    val clickedWeather: StateFlow<CityCurrentWeather?> = _clickedWeather

    fun fetchWeatherAt(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val weather = mapWeatherManager.getMapCityWeather(lon, lat)
                _clickedWeather.value = weather
            } catch (e: Exception) {
                _clickedWeather.value = null
            }
        }
    }

    fun clearClickedWeather() {
        _clickedWeather.value = null
    }
}

package com.qiuqiuqiu.weatherPredicate.ui.screen.map


import android.content.Context
import androidx.lifecycle.ViewModel
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun search(context: Context, mapView: MapView, baiduMap: BaiduMap?) {
        val input = _query.value.trim()
        if (input.isNotBlank()) {
            val parts = input.split(Regex("\\s+"), limit = 2)
            val maybeCity = parts.getOrNull(0)?.trim().orEmpty()
            val maybeAddress = parts.getOrNull(1)?.trim().orEmpty()

            if (maybeAddress.isNotEmpty()) {
                MapUtils.geocodeAndShow(
                    context = context,
                    mapView = mapView,
                    baiduMap = baiduMap,
                    city = maybeCity,
                    address = maybeAddress
                )
            } else {
                val token = maybeCity
                MapUtils.geocodeAndShow(
                    context = context,
                    mapView = mapView,
                    baiduMap = baiduMap,
                    city = token,
                    address = token,
                    fallbackAddressOnly = true
                )
            }
        }
    }
}

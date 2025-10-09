package com.qiuqiuqiu.weatherPredicate.ui.screen.map

import android.util.Log
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.HeatMap
import com.baidu.mapapi.map.WeightedLatLng
import com.baidu.mapapi.model.LatLng
import com.qiuqiuqiu.weatherPredicate.service.QWeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.qiuqiuqiu.weatherPredicate.service.GridPointWeather

/**
 * 热力图工具类
 */
object HotMap {

    // 根据类型选择数值
    private fun getValueByType(type: String, weather: GridPointWeather): Double? {
        return when (type) {
            "temp" -> weather.temp
            "precip" -> weather.precip
            "wind" -> weather.windSpeed
            else -> null
        }
    }

    /**
     * 批量获取格点数据（调用 QWeatherService）
     */
    suspend fun fetchGridData(
        type: String,
        points: List<Pair<Double, Double>>,
        service: QWeatherService
    ): List<WeightedLatLng> = withContext(Dispatchers.IO) {
        try {
            val data = service.getBatchGridWeather(points) // 这里返回的是 List<GridPointWeather>
            data.mapNotNull { grid ->
                val value = getValueByType(type, grid)
                if (value != null) {
                    WeightedLatLng(
                        LatLng(grid.latitude, grid.longitude),
                        value
                    )
                } else null
            }
        } catch (e: Exception) {
            Log.e("HotMap", "获取热力图数据失败: $e")
            emptyList()
        }
    }

    /**
     * 在地图上绘制热力图
     */
    fun addHeatmap(baiduMap: BaiduMap, data: List<WeightedLatLng>) {
        if (data.isEmpty()) return
        val heatMap = HeatMap.Builder()
            .weightedData(data) // 设置带权重的点
            .build()
        baiduMap.addHeatMap(heatMap)
    }

    /**
     * 清除热力图
     */
    fun clearHeatmap(baiduMap: BaiduMap) {
        baiduMap.clear()
    }
}

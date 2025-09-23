package com.qiuqiuqiu.weatherPredicate.ui.screen.map

import android.util.Log
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.HeatMap
import com.baidu.mapapi.map.WeightedLatLng
import com.baidu.mapapi.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class GridPoint(
    val lat: Double,
    val lon: Double,
    val value: Double
)

object HotMap {
    private const val API_URL =
        "https://api.qweather.com/v7/grid-weather/now?key=YOUR_KEY&lon=105&lat=35"
    // ⚠️ 注意：这里只是示例，真实全国数据 API 要查和风天气文档，可能是 list[] 格式

    // 当前热力图对象，方便清除
    private var currentHeatmap: HeatMap? = null

    /**
     * 获取全国格点数据（温度/湿度/AQI）
     */

    suspend fun fetchGridData(type: String): List<GridPoint> = withContext(Dispatchers.IO) {
        val url = URL(API_URL)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"

        return@withContext try {
            val response = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)
            val code = json.optInt("code", -1)

            val results = mutableListOf<GridPoint>()
            if (code == 200) {
                val list = json.optJSONArray("grid") ?: return@withContext results

                for (i in 0 until list.length()) {
                    if (i % 10 != 0) continue // 抽样：每隔 10 个点取 1 个
                    val obj = list.getJSONObject(i)
                    val lat = obj.optDouble("lat", 0.0)
                    val lon = obj.optDouble("lon", 0.0)

                    val value = when (type) {
                        "temp" -> obj.optDouble("temp", 0.0)
                        "humidity" -> obj.optDouble("humidity", 0.0)
                        "aqi" -> obj.optDouble("aqi", 0.0)
                        else -> 0.0
                    }

                    results.add(GridPoint(lat, lon, value))
                }
            }
            results
        } catch (e: Exception) {
            Log.e("HotMap", "Error fetching grid data", e)
            emptyList()
        } finally {
            conn.disconnect()
        }
    }

    /**
     * 在地图上绘制热力图
     */
    fun addHeatmap(baiduMap: BaiduMap, data: List<GridPoint>) {
        if (data.isEmpty()) return

        val points: List<WeightedLatLng> = data.map { pt ->
            WeightedLatLng(LatLng(pt.lat, pt.lon), pt.value)
        }

        val heatMap = HeatMap.Builder()
            .weightedData(points)
            .radius(30)
            .build()

        // 直接覆盖旧的热力图
        baiduMap.addHeatMap(heatMap)
    }


    /**
     * 清除热力图（只移除热力图，不清空其他覆盖物）
     */
    fun clearHeatmap(baiduMap: BaiduMap) {
        baiduMap.clear()
    }
}

package com.qiuqiuqiu.weatherPredicate.ui.screen.map

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.search.geocode.*
import com.baidu.mapapi.model.LatLng
import com.qiuqiuqiu.weatherPredicate.service.GridPointWeather
import com.qiuqiuqiu.weatherPredicate.service.QWeatherService
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.pow
import kotlin.random.Random
import com.baidu.mapapi.map.Gradient

@Composable
fun HotMapScreen(
    navController: NavController,
    qWeatherService: QWeatherService
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val mapView = remember { MapView(navController.context) }
    val baiduMap: BaiduMap = mapView.map

    // 禁止地图旋转、倾斜
    baiduMap.uiSettings.isRotateGesturesEnabled = false
    baiduMap.uiSettings.isOverlookingGesturesEnabled = false
    baiduMap.setMaxAndMinZoomLevel(21f, 9f)

    var selectedType by remember { mutableStateOf("温度") }
    var expanded by remember { mutableStateOf(false) }
    var loadingText by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // 初始化地理编码器
    val geoCoder = remember {
        GeoCoder.newInstance().apply {
            setOnGetGeoCodeResultListener(object : OnGetGeoCoderResultListener {
                override fun onGetGeoCodeResult(result: GeoCodeResult?) {
                    if (result == null || result.location == null) {
                        Toast.makeText(context, "未找到地址", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val target = result.location
                    baiduMap.animateMapStatus(
                        MapStatusUpdateFactory.newLatLngZoom(target, 1f)
                    )
                }

                override fun onGetReverseGeoCodeResult(result: ReverseGeoCodeResult?) {}
            })
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 地图
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        // 顶部工具栏
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.9f))
                .padding(8.dp)
        ) {
            // 第一行：返回 + 搜索框 + 搜索按钮
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { navController.popBackStack() }) {
                    Text("返回")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("搜索位置") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    if (searchQuery.isNotBlank()) {
                        geoCoder.geocode(
                            GeoCodeOption().city("").address(searchQuery)
                        )
                    }
                }) {
                    Text("搜索")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 第二行：下拉选择器，占满整行
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("选择") // ✅ 始终显示“选择”
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("温度", "降水", "风力").forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                expanded = false
                                selectedType = type
                                coroutineScope.launch {
                                    loadingText = "正在生成${type}热力图"
                                    kotlinx.coroutines.delay(100)
                                    // 1. 获取地图范围
                                    val bounds = baiduMap.mapStatus.bound
                                    val sw = bounds.southwest
                                    val ne = bounds.northeast

                                    // 2. 生成稀疏格点
                                    val basePoints = mutableListOf<Pair<Double, Double>>()
                                    val latStep = (ne.latitude - sw.latitude) / 16
                                    val lonStep = (ne.longitude - sw.longitude) / 32
                                    var lat = sw.latitude
                                    while (lat <= ne.latitude) {
                                        var lon = sw.longitude
                                        while (lon <= ne.longitude) {
                                            basePoints.add(Pair(lon, lat))
                                            lon += lonStep
                                        }
                                        lat += latStep
                                    }

                                    // 3. 获取真实数据
                                    val rawData = qWeatherService.getBatchGridWeather(basePoints)

                                    // 4. 插值增强
                                    val interpolatedData = interpolateGridWithNoiseIrregular(
                                        rawData,
                                        newStep = 80,
                                        type = selectedType,
                                        noiseLat = 0.0025,
                                        noiseLon = 0.0025,
                                    )

                                    // 5. 绘制热力图
                                    updateHeatMap(baiduMap, interpolatedData, selectedType)
                                    loadingText = null
                                }
                            }
                        )
                    }
                }
            }
        }



        // 中心加载提示
        loadingText?.let { text ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text, color = Color.White)
            }
        }
    }
}

/**
 * 非规则采样 + 扰动增强
 */
fun interpolateGridWithNoiseIrregular(
    data: List<GridPointWeather>,
    newStep: Int,
    type: String,
    noiseLat: Double = 0.0025,
    noiseLon: Double = 0.0025
): List<GridPointWeather> {
    if (data.isEmpty()) return emptyList()

    val lats = data.map { it.latitude }.distinct().sorted()
    val lons = data.map { it.longitude }.distinct().sorted()

    val latMin = lats.first()
    val latMax = lats.last()
    val lonMin = lons.first()
    val lonMax = lons.last()

    val latStep = (latMax - latMin) / newStep
    val lonStep = (lonMax - lonMin) / newStep

    val gridMap = data.associateBy { Pair(it.latitude, it.longitude) }

    val result = mutableListOf<GridPointWeather>()

    for (i in 0..newStep) {
        for (j in 0..newStep) {
            val lat = latMin + i * latStep + Random.nextDouble(-latStep / 2, latStep / 2)
            val lon = lonMin + j * lonStep + Random.nextDouble(-lonStep / 2, lonStep / 2)

            val lat0 = floor((lat - latMin) / (latMax - latMin) * (lats.size - 1)).toInt().coerceIn(0, lats.size - 2)
            val lon0 = floor((lon - lonMin) / (lonMax - lonMin) * (lons.size - 1)).toInt().coerceIn(0, lons.size - 2)

            val lat1 = lats[lat0]
            val lat2 = lats[lat0 + 1]
            val lon1 = lons[lon0]
            val lon2 = lons[lon0 + 1]

            val q11 = gridMap[Pair(lat1, lon1)]
            val q21 = gridMap[Pair(lat1, lon2)]
            val q12 = gridMap[Pair(lat2, lon1)]
            val q22 = gridMap[Pair(lat2, lon2)]

            val fx = if (lon2 != lon1) (lon - lon1) / (lon2 - lon1) else 0.0
            val fy = if (lat2 != lat1) (lat - lat1) / (lat2 - lat1) else 0.0

            fun interp(v11: Double?, v21: Double?, v12: Double?, v22: Double?): Double? {
                if (v11 == null || v21 == null || v12 == null || v22 == null) return null
                return (1 - fx) * (1 - fy) * v11 +
                        fx * (1 - fy) * v21 +
                        (1 - fx) * fy * v12 +
                        fx * fy * v22
            }

            val value = when (type) {
                "温度" -> interp(q11?.temp, q21?.temp, q12?.temp, q22?.temp)
                "降水" -> interp(q11?.precip, q21?.precip, q12?.precip, q22?.precip)
                "风力" -> interp(q11?.windSpeed, q21?.windSpeed, q12?.windSpeed, q22?.windSpeed)
                else -> null
            }

            val noisyLat = lat + Random.nextDouble(-noiseLat, noiseLat)
            val noisyLon = lon + Random.nextDouble(-noiseLon, noiseLon)

            result.add(
                GridPointWeather(
                    latitude = noisyLat,
                    longitude = noisyLon,
                    temp = if (type == "温度") value else null,
                    humidity = null,
                    windSpeed = if (type == "风力") value else null,
                    precip = if (type == "降水") value else null
                )
            )
        }
    }

    return result
}

/**
 * 更新百度地图热力图
 */
fun updateHeatMap(baiduMap: BaiduMap, data: List<GridPointWeather>, type: String) {
    if (data.isEmpty()) return

    val values = data.mapNotNull {
        when (type) {
            "温度" -> it.temp
            "降水" -> it.precip
            "风力" -> it.windSpeed
            else -> null
        }
    }
    if (values.isEmpty()) return

    val minVal = values.minOrNull() ?: return
    val maxVal = values.maxOrNull() ?: return
    val range = (maxVal - minVal).takeIf { it > 1e-6 } ?: 1.0

    val builder = com.baidu.mapapi.map.HeatMap.Builder()
    val weightedData = data.mapNotNull { point ->
        val rawValue = when (type) {
            "温度" -> point.temp
            "降水" -> point.precip
            "风力" -> point.windSpeed
            else -> null
        } ?: return@mapNotNull null

        val norm = ((rawValue - minVal) / range).coerceIn(0.0, 1.0)
        val enhanced = if (norm < 0.5) 0.5 * norm.pow(0.5) else 0.5 + 0.5 * norm.pow(2.0)
        com.baidu.mapapi.map.WeightedLatLng(
            LatLng(point.latitude, point.longitude),
            enhanced
        )
    }

    if (weightedData.isNotEmpty()) {
        val colors = intArrayOf(
            0x660000ff.toInt(), // 深蓝-低值
            0x6600ffff.toInt(), // 青
            0x66ffff00.toInt(), // 黄
            0x66ff8000.toInt(), // 橙
            0x66ff0000.toInt()  // 红-高值
        )
        val startPoints = floatArrayOf(0f, 0.2f, 0.5f, 0.75f, 1f)
        val gradient = Gradient(colors, startPoints)
        builder.gradient(gradient)


        val heatMap = builder.build()
        baiduMap.clear()
        baiduMap.addHeatMap(heatMap)
    }
}

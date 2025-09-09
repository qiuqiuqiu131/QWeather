package com.qiuqiuqiu.weatherPredicate.ui.screen.map

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.model.LatLng
import com.qiuqiuqiu.weatherPredicate.service.QWeatherService
import java.util.Locale

@Composable
fun MapScreen(viewModel: MapViewModel) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val baiduMap = remember { mapView.map }

    val query by viewModel.query.collectAsState()

    // 保存点击的经纬度文本
    var latLngText by remember { mutableStateOf("") }

    // 👉 进入地图时启动定位
    LaunchedEffect(Unit) {
        MapUtils.startLocation(context, baiduMap)
        // 加载热门城市天气
        val service = QWeatherService(context)
        val cities = service.getManualCitiesWeather() // 手动省会列表
        showCityWeatherMarkers(context, baiduMap, cities) // ✅ 可以正常调用

    }

    // 设置地图点击监听
    DisposableEffect(baiduMap) {
        val listener = object : BaiduMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng?) {
                latLng?.let {
                    latLngText = String.format(
                        Locale.getDefault(),
                        "纬度: %.4f, 经度: %.4f",
                        it.latitude,
                        it.longitude
                    )
                }
            }

            override fun onMapPoiClick(poi: com.baidu.mapapi.map.MapPoi?) {
                poi?.let {
                    latLngText = String.format(
                        Locale.getDefault(),
                        "纬度: %.4f, 经度: %.4f",
                        it.position.latitude,
                        it.position.longitude
                    )
                }
            }
        }

        baiduMap?.setOnMapClickListener(listener)

        onDispose {
            baiduMap?.setOnMapClickListener(null)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.updateQuery(it) },
            label = { Text("输入地名，例如：上海 或 上海 南京路") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                if (query.isBlank()) {
                    Toast.makeText(context, "请输入搜索内容", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.search(context, mapView, baiduMap)
                }
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            Text("搜索")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 地图显示
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )

            // 经纬度提示框（黑底白字、居中、随点更新）
            if (latLngText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = latLngText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

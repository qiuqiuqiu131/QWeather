package com.qiuqiuqiu.weatherPredicate.ui.screen.map

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.model.LatLng
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun MapScreen() {
    val viewModel: MapViewModel = hiltViewModel()
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val baiduMap = remember { mapView.map }

    val query by viewModel.query.collectAsState()
    val clickedWeather by viewModel.clickedWeather.collectAsState()

    var latLngText by remember { mutableStateOf("") }
    var clickedLatLng by remember { mutableStateOf<LatLng?>(null) }

    // ---------- 动态权限 ----------
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var hasPermission by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val denied = result.filterValues { !it }.keys
        if (denied.isEmpty()) {
            hasPermission = true
            MapUtils.startLocation(context, baiduMap)
        } else {
            Toast.makeText(context, "未授予定位权限", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val denied = permissions.filter {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        if (denied.isEmpty()) {
            hasPermission = true
            MapUtils.startLocation(context, baiduMap)
        } else {
            launcher.launch(denied.toTypedArray())
        }
    }

    // ---------- 加载热门城市天气 ----------
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val cities = viewModel.getManualCitiesWeather()
            showCityWeatherMarkers(context, baiduMap, cities)
        }
    }

    // ---------- 地图点击监听 ----------
    DisposableEffect(baiduMap) {
        val listener = object : BaiduMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng?) {
                if (latLng != null) {
                    latLngText = String.format(
                        Locale.getDefault(),
                        "纬度: %.4f, 经度: %.4f",
                        latLng.latitude,
                        latLng.longitude
                    )
                    clickedLatLng = latLng
                    viewModel.fetchWeatherAt(latLng.latitude, latLng.longitude)
                } else {
                    clickedLatLng = null
                    viewModel.clearClickedWeather()
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
                    clickedLatLng = it.position
                    viewModel.fetchWeatherAt(it.position.latitude, it.position.longitude)
                }
            }
        }
        baiduMap?.setOnMapClickListener(listener)
        onDispose { baiduMap?.setOnMapClickListener(null) }
    }

    // ---------- 弹窗自动关闭 ----------
    LaunchedEffect(clickedWeather) {
        if (clickedWeather != null) {
            delay(1000) // 1秒后自动关闭
            clickedLatLng = null
            viewModel.clearClickedWeather()
        }
    }

    // ---------- 界面 ----------
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
        ) { Text("搜索") }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

            // ---------- 经纬度显示 ----------
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

            // ---------- 天气弹窗 ----------
            val weather = clickedWeather
            val latLng = clickedLatLng
            val map = baiduMap
            if (weather != null && weather.weatherNow != null && latLng != null && map != null) {
                val projection = map.projection
                val screenPoint = projection.toScreenLocation(latLng)

                Box(
                    modifier = Modifier
                        .offset { IntOffset(screenPoint.x, screenPoint.y - 120) }
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "天气: ${weather.weatherNow?.text ?: "N/A"}\n" +
                                "温度: ${weather.weatherNow?.temp ?: "N/A"}°C\n" +
                                "AQI(CN): ${weather.airNow?.aqi ?: "N/A"}",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else if (latLng != null) {
                // 无数据提示
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, 0) }
                        .background(Color.LightGray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "该位置无天气数据",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

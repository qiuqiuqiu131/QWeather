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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement

@Composable
fun MapScreen(navController: androidx.navigation.NavController) {
    val viewModel: MapViewModel = hiltViewModel()
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val baiduMap = remember {
        mapView.map.apply {
            uiSettings.isRotateGesturesEnabled = false   // 禁止旋转
            uiSettings.isOverlookingGesturesEnabled = false // 可选：禁止俯视
        }
    }

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

    // ---------- 弹窗和经纬度提示自动关闭 ----------
    LaunchedEffect(clickedLatLng, clickedWeather) {
        if (clickedLatLng != null) {
            delay(1000) // 等待1秒
            clickedLatLng = null
            viewModel.clearClickedWeather()
            latLngText = ""
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
                    // 修改：增加搜索结果回调
                    viewModel.search(context, mapView, baiduMap) { resultLatLng ->
                        if (resultLatLng != null) {
                            clickedLatLng = resultLatLng
                            viewModel.fetchWeatherAt(resultLatLng.latitude, resultLatLng.longitude)
                            latLngText = String.format(
                                Locale.getDefault(),
                                "纬度: %.4f, 经度: %.4f",
                                resultLatLng.latitude,
                                resultLatLng.longitude
                            )
                        } else {
                            Toast.makeText(context, "未找到该城市", Toast.LENGTH_SHORT).show()
                        }
                    }
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
            if (latLngText.isNotEmpty() && clickedLatLng != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 35.dp)
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

                Card(
                    modifier = Modifier
                        .offset { IntOffset(screenPoint.x - 150, screenPoint.y - 220) }
                        .width(160.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "天气详情",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Divider()
                        Text("天气：${weather.weatherNow?.text ?: "N/A"}")
                        Text("温度：${weather.weatherNow?.temp ?: "N/A"}°C")
                        Text("空气质量：${weather.airNow?.aqi ?: "N/A"}")
                    }
                }
            } else if (latLng != null) {
                // 无数据提示
                Card(
                    modifier = Modifier
                        .offset { IntOffset(0, 0) }
                        .width(220.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "该位置无天气数据",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // ---------- 开启热力图按钮 ----------
            Button(
                onClick = { navController.navigate("HotMap") },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 80.dp)
            ) {
                Text("开启热力图")
            }
        }
    }
}

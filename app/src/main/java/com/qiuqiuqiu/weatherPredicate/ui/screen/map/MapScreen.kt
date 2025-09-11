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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.model.LatLng
import java.util.Locale

@Composable
fun MapScreen() {
    val viewModel: MapViewModel = hiltViewModel()

    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val baiduMap = remember { mapView.map }

    val query by viewModel.query.collectAsState()

    // 保存点击的经纬度文本
    var latLngText by remember { mutableStateOf("") }

    // ---------- 动态权限处理 ----------
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var hasPermission by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val denied = result.filterValues { !it }.keys
        if (denied.isEmpty()) {
            hasPermission = true
            MapUtils.startLocation(context, baiduMap)
        } else {
            Toast.makeText(context, "未授予定位权限，无法获取当前位置", Toast.LENGTH_SHORT).show()
        }
    }

    // 首次进入页面检查权限
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

    // ---------- 设置地图点击监听 ----------
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

            // 经纬度提示框
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

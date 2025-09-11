package com.qiuqiuqiu.weatherPredicate.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.qiuqiuqiu.weatherPredicate.ui.screen.map.MapViewModel
import com.qiuqiuqiu.weatherPredicate.service.QWeatherService
import kotlinx.coroutines.launch

@Composable
fun MapScreen(viewModel: MapViewModel? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ---------- 权限请求 ----------
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.INTERNET
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val denied = result.filterValues { !it }.keys
        if (denied.isNotEmpty()) {
            // TODO: 提示用户权限被拒绝
        }
    }

    LaunchedEffect(Unit) {
        val denied = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (denied.isNotEmpty()) {
            launcher.launch(denied.toTypedArray())
        }
    }

    // ---------- 调用和风天气服务 ----------
    LaunchedEffect(Unit) {
        val service = QWeatherService(context)
        scope.launch {
            service.getCityTop(10)
        }
    }

    // ---------- 显示百度地图 ----------
    var mapView by remember { mutableStateOf<MapView?>(null) }
    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                mapView = this
            }
        },
        update = { view ->
            val baiduMap: BaiduMap = view.map
            baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15f))
        },
        modifier = Modifier.fillMaxSize()
    )

    // ---------- 生命周期管理 ----------
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDestroy()
        }
    }
}

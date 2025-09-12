package com.qiuqiuqiu.weatherPredicate.ui.screen.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.model.LatLng

@Composable
fun ChinaMapScreen(modifier: Modifier = Modifier) {
    val viewModel: MapViewModel = hiltViewModel()

    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    mapView.showZoomControls(false)
    val baiduMap = remember { mapView.map }

    // 初始设置为中国范围 + 禁止交互
    DisposableEffect(Unit) {
        val chinaCenter = LatLng(39.0, 105.0)
        baiduMap?.apply {
            setMapStatus(MapStatusUpdateFactory.newLatLngZoom(chinaCenter, 4.5f))
            uiSettings.apply {
                isScrollGesturesEnabled = false   // 禁止拖动
                isZoomGesturesEnabled = false     // 禁止缩放
                isRotateGesturesEnabled = false   // 禁止旋转
                isOverlookingGesturesEnabled = false // 禁止俯视
            }
        }
        onDispose { }
    }

    // 加载省会城市天气并显示图标
    LaunchedEffect(Unit) {
        val cities = viewModel.getManualCitiesWeather()
        showCityWeatherMarkers(context, baiduMap, cities)
    }

    // 显示地图
    AndroidView(
        factory = { mapView },
        modifier = modifier,
    )
}

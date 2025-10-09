package com.qiuqiuqiu.weatherPredicate.ui.screen.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng

@Composable
fun MapSideScreen(
    title: String,
    longitude: Double,
    latitude: Double,
    navController: NavController
) {
    val mapView = rememberMapViewWithLifecycle()
    // 尝试隐藏缩放控件（可选）
    try {
        mapView.showZoomControls(false)
    } catch (_: Throwable) {
    }
    val baiduMap = remember { mapView.map }

    LaunchedEffect(Unit) {
        val latLng = LatLng(latitude, longitude)
        baiduMap?.apply {
            isMyLocationEnabled = true
            val locData = MyLocationData.Builder()
                .latitude(latitude)
                .longitude(longitude)
                .accuracy(10f)
                .build()
            setMyLocationData(locData)

            setMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, 14f))
        }
        mapView.invalidate()

        // 禁用旋转/俯仰
        baiduMap?.uiSettings?.apply {
            isRotateGesturesEnabled = false
            isOverlookingGesturesEnabled = false
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        MapSideScreenTopBar(title, navController)
    }) { innerPadding ->
        AndroidView(
            factory = { mapView }, modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun MapSideScreenTopBar(title: String, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(50.dp)
    ) {

        Text(
            text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(
                Alignment.Center
            )
        )

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(8.dp)
                .clickable {}
                .align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

package com.qiuqiuqiu.weatherPredicate.ui.screen.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.baidu.mapapi.map.BaiduMap
import kotlinx.coroutines.launch

@Composable
fun HotmapScreen(navController: NavController) {
    val mapView = rememberMapViewWithLifecycle()
    val baiduMap = remember { mapView.map }

    val options = listOf("temp", "humidity", "aqi")
    var selectedType by remember { mutableStateOf("temp") }
    val coroutineScope = rememberCoroutineScope()

    // 加载热力图
    LaunchedEffect(selectedType) {
        val data = HotMap.fetchGridData(selectedType)
        HotMap.addHeatmap(baiduMap, data)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 地图
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        // 顶部栏
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var dropdownExpanded by remember { mutableStateOf(false) }

            Box {
                Button(onClick = { dropdownExpanded = true }) {
                    Text(
                        when (selectedType) {
                            "temp" -> "温度"
                            "humidity" -> "湿度"
                            "aqi" -> "空气质量"
                            else -> selectedType
                        }
                    )
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    options.forEach { type ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when (type) {
                                        "temp" -> "温度"
                                        "humidity" -> "湿度"
                                        "aqi" -> "空气质量"
                                        else -> type
                                    }
                                )
                            },
                            onClick = {
                                selectedType = type
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // 返回按钮（右上角）
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("返回")
        }
    }
}

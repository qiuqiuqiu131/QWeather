package com.qiuqiuqiu.weatherPredicate.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.ui.Alignment



@Composable
fun TimeScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 三个竖直排放的功能按钮（在页面顶部）
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { navController.navigate("time/global") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Public, contentDescription = "全球时间")
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Text("全球时间查询")
                }

                Button(
                    onClick = { navController.navigate("time/solar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Spa, contentDescription = "二十四节气")
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Text("二十四节气查询")
                }

                Button(
                    onClick = { navController.navigate("time/poem") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.FormatQuote, contentDescription = "天气诗句")
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Text("天气诗句")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 可放快捷城市或说明
            androidx.compose.material3.Text(
                "请选择上方功能开始使用。",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
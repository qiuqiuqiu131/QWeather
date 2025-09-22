package com.qiuqiuqiu.weatherPredicate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TimeScreen(navController: NavController) {


    // 背景渐变
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "生活小助手",
                color = Color.White,
                fontSize = 36.sp,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 功能按钮
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureButton("全球时间查询", Icons.Default.Public) {
                    navController.navigate("time/global")
                }
                FeatureButton("二十四节气查询", Icons.Default.Spa) {
                    navController.navigate("time/solar")
                }
//                FeatureButton("旅游景区查询", Icons.Default.Place) {
//                    navController.navigate("time/city")
//                }
                FeatureButton("天气诗句", Icons.Default.Spa) {
                    navController.navigate("time/shiju")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                "请选择上方功能开始使用",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun FeatureButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Icon(icon, contentDescription = label, tint = Color(0xFF2196F3))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = Color(0xFF2196F3))
    }
}




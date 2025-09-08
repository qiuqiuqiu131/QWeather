package com.qiuqiuqiu.weatherPredicate.tools

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Coronavirus
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.LocalCarWash
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Sick
import androidx.compose.material.icons.outlined.SportsBaseball
import androidx.compose.material.icons.outlined.Traffic
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.ui.graphics.vector.ImageVector

fun String.toMaterialIcon(): ImageVector {
    return when (this) {
        "运动指数" -> Icons.Outlined.SportsBaseball
        "穿衣指数" -> Icons.Outlined.Checkroom
        "紫外线指数" -> Icons.Outlined.LightMode
        "过敏指数" -> Icons.Outlined.Sick
        "感冒指数" -> Icons.Outlined.Coronavirus
        "太阳镜指数" -> Icons.Default.WbSunny
        "晾晒指数" -> Icons.Outlined.LocalLaundryService
        "防晒指数" -> Icons.Outlined.BeachAccess
        "洗车指数" -> Icons.Outlined.LocalCarWash
        "旅游指数" -> Icons.Outlined.TravelExplore
        "钓鱼指数" -> Icons.Outlined.Waves //
        "化妆指数" -> Icons.Default.Face
        "交通指数" -> Icons.Outlined.Traffic
        "空调开启指数" -> Icons.Outlined.AcUnit
        "舒适度指数" -> Icons.Outlined.SentimentSatisfied
        "运动开启指数" -> Icons.Outlined.DirectionsRun
        else -> Icons.Outlined.HelpOutline
    }
}
package com.qiuqiuqiu.weatherPredicate.tools

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Coronavirus
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

fun getWeatherURL(id: String): String {
    return "https://a.hecdn.net/img/common/icon/202106d/${id}.png"
}

fun String.toMaterialIcon(): ImageVector {
    return when (this) {
        "运动指数" -> Icons.Outlined.SportsBaseball //
        "穿衣指数" -> Icons.Outlined.Checkroom //
        "紫外线指数" -> Icons.Outlined.LightMode //
        "过敏指数" -> Icons.Outlined.Sick //
        "感冒指数" -> Icons.Outlined.Coronavirus //
        "太阳镜指数" -> Icons.Default.WbSunny //
        "晾晒指数" -> Icons.Outlined.LocalLaundryService //
        "防晒指数" -> Icons.Outlined.BeachAccess //
        "洗车指数" -> Icons.Outlined.LocalCarWash //
        "旅游指数" -> Icons.Outlined.TravelExplore //
        "钓鱼指数" -> Icons.Outlined.Waves //
        "化妆指数" -> Icons.Default.Face //
        "交通指数" -> Icons.Outlined.Traffic //
        "空调开启指数" -> Icons.Outlined.AcUnit //
        "舒适度指数" -> Icons.Outlined.SentimentSatisfied //
        "空气污染扩散条件指数" -> Icons.Outlined.Air
        else -> Icons.Outlined.HelpOutline
    }
}

fun String.toMaterialFillIcon(): ImageVector {
    return when (this) {
        "运动指数" -> Icons.Default.SportsBaseball
        "穿衣指数" -> Icons.Default.Checkroom
        "紫外线指数" -> Icons.Default.LightMode
        "过敏指数" -> Icons.Default.Sick
        "感冒指数" -> Icons.Default.Coronavirus
        "太阳镜指数" -> Icons.Default.WbSunny
        "晾晒指数" -> Icons.Default.LocalLaundryService
        "防晒指数" -> Icons.Default.BeachAccess
        "洗车指数" -> Icons.Default.LocalCarWash
        "旅游指数" -> Icons.Default.TravelExplore
        "钓鱼指数" -> Icons.Default.Waves //
        "化妆指数" -> Icons.Default.Face
        "交通指数" -> Icons.Default.Traffic
        "空调开启指数" -> Icons.Default.AcUnit
        "舒适度指数" -> Icons.Default.SentimentSatisfied
        "空气污染扩散条件指数" -> Icons.Default.Air
        else -> Icons.Outlined.HelpOutline
    }
}
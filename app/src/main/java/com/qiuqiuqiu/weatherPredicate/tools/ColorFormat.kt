package com.qiuqiuqiu.weatherPredicate.tools

import androidx.compose.ui.graphics.Color

fun String.getLightColorByName(): Color =
    when (this) {
        "White" -> Color(0xCCE0E0E0)
        "Blue" -> Color(0xCC1976D2) // 深蓝
        "Green" -> Color(0xCC388E3C) // 深绿
        "Yellow" -> Color(0xCCFBC02D) // 深黄
        "Orange" -> Color(0xCCF57C00) // 深橙
        "Red" -> Color(0xCCD32F2F) // 深红
        "Black" -> Color(0xCC212121) // 深黑
        else -> Color(0xCC757575) // 默认深灰
    }

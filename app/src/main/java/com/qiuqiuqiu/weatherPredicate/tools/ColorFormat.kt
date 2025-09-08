package com.qiuqiuqiu.weatherPredicate.tools

import androidx.compose.ui.graphics.Color

fun String.getLightColorByName(): Color =
    when (this) {
        "White" -> Color(0xFFFFFFFF)
        "Blue" -> Color(0xFF90CAF9) // 稍深一点的浅蓝
        "Green" -> Color(0xFFA5D6A7) // 稍深一点的浅绿
        "Yellow" -> Color(0xFFFFF59D) // 稍深一点的浅黄
        "Orange" -> Color(0xFFFFCC80) // 稍深一点的浅橙
        "Red" -> Color(0xFFEF9A9A) // 稍深一点的浅红
        "Black" -> Color(0xFFBDBDBD) // 稍深一点的浅灰，模拟浅黑
        else -> Color(0xFFF0F0F0) // 默认稍深一点的浅灰
    }

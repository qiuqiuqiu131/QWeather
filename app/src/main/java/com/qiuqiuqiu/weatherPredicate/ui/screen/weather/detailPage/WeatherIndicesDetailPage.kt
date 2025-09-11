package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.qiuqiuqiu.weatherPredicate.tools.toMaterialIcon
import com.qweather.sdk.response.indices.IndicesDaily

@Composable
fun WeatherIndicesPage(
    weatherIndices: IndicesDaily?,
    modifier: Modifier = Modifier,
    currentPageIndex: Int,
    pageIndex: Int,
    onColorChanged: ((Color) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        weatherIndices?.let {
            val indicesData = indicesMapper(weatherIndices.type.toInt())
            val color = if (isSystemInDarkTheme()) indicesData.nightColor else indicesData.dayColor
            LaunchedEffect(currentPageIndex) {
                if (currentPageIndex == pageIndex)
                    onColorChanged?.invoke(color)
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                shape =
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp
                    ),
                colors = CardDefaults.cardColors()
                    .copy(containerColor = color)
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight()
                    ) {
                        Text(text = "${weatherIndices.level.toInt() * 100 / indicesData.maxLevel}%")
                        Text(text = weatherIndices.category)
                        Text(text = weatherIndices.text)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Icon(
                            weatherIndices.name.toMaterialIcon(),
                            null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

data class IndicesData(
    val type: Int,
    val maxLevel: Int,
    val dayColor: Color,
    val nightColor: Color
)

fun indicesMapper(type: Int): IndicesData {
    return when (type) {
        1 -> IndicesData(1, 3, Color(0xFF81C784), Color(0xFF388E3C)) // 运动指数
        2 -> IndicesData(2, 4, Color(0xFFFFE082), Color(0xFFFBC02D)) // 洗车指数
        3 -> IndicesData(3, 7, Color(0xFF90CAF9), Color(0xFF1976D2)) // 穿衣指数
        4 -> IndicesData(4, 3, Color(0xFFA5D6A7), Color(0xFF388E3C)) // 钓鱼指数
        5 -> IndicesData(5, 5, Color(0xFFFFCDD2), Color(0xFFD32F2F)) // 紫外线指数
        6 -> IndicesData(6, 5, Color(0xFFFFF59D), Color(0xFFFBC02D)) // 旅游指数
        7 -> IndicesData(7, 5, Color(0xFFE1BEE7), Color(0xFF7B1FA2)) // 花粉过敏指数
        8 -> IndicesData(8, 7, Color(0xFFB3E5FC), Color(0xFF0288D1)) // 舒适度指数
        9 -> IndicesData(9, 4, Color(0xFFB2DFDB), Color(0xFF00796B)) // 感冒指数
        10 -> IndicesData(10, 5, Color(0xFFD7CCC8), Color(0xFF5D4037)) // 空气污染扩散条件指数
        11 -> IndicesData(11, 4, Color(0xFFFFF9C4), Color(0xFFFBC02D)) // 空调开启指数
        12 -> IndicesData(12, 5, Color(0xFFB2EBF2), Color(0xFF0097A7)) // 太阳镜指数
        13 -> IndicesData(13, 8, Color(0xFFFFE0B2), Color(0xFFF57C00)) // 化妆指数
        14 -> IndicesData(14, 6, Color(0xFFFFF8E1), Color(0xFFFFA000)) // 晾晒指数
        15 -> IndicesData(15, 5, Color(0xFFCFD8DC), Color(0xFF455A64)) // 交通指数
        16 -> IndicesData(16, 5, Color(0xFFFFF3E0), Color(0xFFE65100)) // 防晒指数
        else -> IndicesData(0, 1, Color(0xFFEEEEEE), Color(0xFF616161)) // 其他
    }
}

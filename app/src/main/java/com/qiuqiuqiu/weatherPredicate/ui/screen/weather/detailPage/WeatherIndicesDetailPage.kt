package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.qiuqiuqiu.weatherPredicate.tools.toMaterialIcon
import com.qweather.sdk.response.indices.IndicesDaily

@Composable
fun WeatherIndicesPage(weatherIndices: IndicesDaily?, modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
    ) {
        weatherIndices?.let {
            val indicesData = indicesMapper(weatherIndices.type.toInt())
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .background(indicesData.color)
            ) {
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

data class IndicesData(
    val type: Int,
    val maxLevel: Int,
    val color: Color
)

fun indicesMapper(type: Int): IndicesData {
    return when (type) {
        1 -> IndicesData(1, 3, Color(0xFF4CAF50))
        2 -> IndicesData(2, 4, Color(0xFFFF9800))
        3 -> IndicesData(3, 7, Color(0xFFFF9800))
        4 -> IndicesData(4, 3, Color(0xFFFF9800))
        5 -> IndicesData(5, 5, Color(0xFFF44336))
        6 -> IndicesData(6, 5, Color(0xFFF44336))
        7 -> IndicesData(7, 5, Color(0xFFF44336))
        8 -> IndicesData(8, 7, Color(0xFFF44336))
        9 -> IndicesData(9, 4, Color(0xFFF44336))
        10 -> IndicesData(10, 5, Color(0xFFF44336))
        11 -> IndicesData(11, 4, Color(0xFFF44336))
        12 -> IndicesData(12, 5, Color(0xFFF44336))
        13 -> IndicesData(13, 8, Color(0xFFF44336))
        14 -> IndicesData(14, 6, Color(0xFFF44336))
        15 -> IndicesData(15, 5, Color(0xFFF44336))
        16 -> IndicesData(16, 5, Color(0xFFF44336))
        else -> IndicesData(0, 1, Color(0xFF9E9E9E))
    }
}
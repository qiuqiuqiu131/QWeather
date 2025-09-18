package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.tools.toMaterialIcon
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseItem
import com.qweather.sdk.response.indices.IndicesDaily
import com.qweather.sdk.response.weather.WeatherNow

/** 生活指数卡片 */
@Composable
fun LifeIndexCard(
    indices: List<IndicesDaily>,
    onClick: (() -> Unit)? = null,
    onItemClick: ((IndicesDaily) -> Unit)? = null
) {
    BaseCard(
        title = "生活指数",
        onClick = onClick,
        endCorner = {
            IconButton(onClick = { onClick?.invoke() }, modifier = Modifier.size(25.dp)) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    modifier = Modifier.size(20.dp),
                    contentDescription = null
                )
            }
        },
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val newIndices = indices.filter { it.name.length <= 8 }
            items(newIndices) { it ->
                // val data = indicesMapper(it.type.toInt())
                TipItem(
                    it.name.toMaterialIcon(),
                    it.category,
                    it.name,
                    onClick = { onItemClick?.invoke(it) },
                    // data.nightColor
                )
            }
        }
    }
}

/** 天气指数卡片 */
@Composable
fun WeatherIndexCard(weather: WeatherNow, uvIndex: String?, onClick: (() -> Unit)? = null) {
    BaseCard(
        title = "天气指数",
        onClick = onClick,
        endCorner = {
            IconButton(onClick = { onClick?.invoke() }, modifier = Modifier.size(25.dp)) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    modifier = Modifier.size(20.dp),
                    contentDescription = null
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        LazyRow(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            item { TipItem(Icons.Default.Thermostat, "${weather.feelsLike}℃", "体感温度", onClick) }

            item { TipItem(Icons.Outlined.WaterDrop, "${weather.humidity}%", "湿度", onClick) }

            item {
                var level = "未知"
                uvIndex?.let { level = it.toUV() }
                TipItem(Icons.Outlined.LightMode, level, "紫外线", onClick)
            }

            item { TipItem(Icons.Outlined.Air, "${weather.windScale} 级", "风力", onClick) }

            item { TipItem(Icons.Default.OutlinedFlag, "${weather.windDir}", "风向", onClick) }

            if (!weather.cloud.isNullOrBlank())
                item { TipItem(Icons.Outlined.CloudQueue, "${weather.cloud}%", "云量", onClick) }

            item { TipItem(Icons.Outlined.Speed, "${weather.pressure} 百帕", "气压", onClick) }

            item { TipItem(Icons.Outlined.RemoveRedEye, "${weather.vis} 公里", "能见度", onClick) }
        }
    }
}

@Composable
fun TipItem(
    icon: ImageVector,
    content: String,
    name: String,
    onClick: (() -> Unit)? = null,
    color: Color = MaterialTheme.colorScheme.onSecondary,
) {
    BaseItem(innerModifier = Modifier.padding(vertical = 8.dp), onClick = onClick) {
        Column(
            modifier = Modifier.width(85.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(40.dp)
                    .padding(top = 6.dp, bottom = 8.dp)
            )
            Text(text = content, style = MaterialTheme.typography.labelLarge, fontSize = 13.sp)
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 10.sp,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.6f)
            )
        }
    }
}

fun String.toUV(): String {
    return when (this.toIntOrNull()) {
        in 0..2 -> "低"
        in 3..5 -> "中"
        in 6..7 -> "高"
        in 8..10 -> "很高"
        in 11..Int.MAX_VALUE -> "极高"
        else -> "未知"
    }
}
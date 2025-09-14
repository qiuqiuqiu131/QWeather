package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.tools.toMaterialFillIcon
import com.qiuqiuqiu.weatherPredicate.ui.normal.IconList
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.WeatherDailyInfoCard
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherDetailViewModel
import com.qweather.sdk.response.indices.IndicesDaily
import com.qweather.sdk.response.weather.WeatherDaily
import java.time.LocalDate
import kotlin.math.max

@SuppressLint("NewApi")
@Composable
fun WeatherIndicesPage(
    weatherIndices: List<IndicesDaily>?,
    weatherDailies: List<WeatherDaily>?,
    viewModel: WeatherDetailViewModel,
    modifier: Modifier = Modifier,
    currentPageIndex: Int,
    pageIndex: Int,
    onColorChanged: ((Color) -> Unit)? = null,
    onSwitchPage: ((index: Int) -> Unit)? = null
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val radioGroup = listOf("今天", "明天", "后天")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (weatherIndices != null) {
            val currentIndices = weatherIndices[currentIndex]
            val indicesData = indicesMapper(currentIndices.type.toInt())
            val colorBg =
                if (isSystemInDarkTheme()) indicesData.nightColor else indicesData.dayColor
            val colorIcon =
                if (isSystemInDarkTheme()) indicesData.dayColor else indicesData.nightColor
            val level = currentIndices.level.toInt()
            LaunchedEffect(currentPageIndex) {
                if (currentPageIndex == pageIndex)
                    onColorChanged?.invoke(colorBg)
            }
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape =
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 36.dp,
                        bottomEnd = 36.dp
                    ),
                colors = CardDefaults.cardColors()
                    .copy(containerColor = colorBg)
            ) {
                Row(modifier = Modifier.padding(start = 26.dp, top = 20.dp, end = 8.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1.7f)
                            .fillMaxHeight()
                    ) {
                        SingleChoiceSegmentedButtonRow(
                            space = 0.dp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            radioGroup.forEachIndexed { index, string ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = radioGroup.size
                                    ),
                                    border = SegmentedButtonDefaults.borderStroke(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                        width = 0.dp
                                    ),
                                    onClick = {
                                        currentIndex = index
                                    },
                                    modifier = Modifier
                                        .height(30.dp),
                                    selected = (index == currentIndex),
                                    colors = SegmentedButtonDefaults.colors()
                                        .copy(
                                            activeContainerColor = MaterialTheme.colorScheme.primary,
                                            activeContentColor = MaterialTheme.colorScheme.background,
                                            inactiveContentColor = MaterialTheme.colorScheme.onSecondary,
                                            inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainer
                                        ),
                                    icon = {},
                                    label = {
                                        Text(
                                            text = string,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    })
                            }
                        }
                        Text(
                            text = currentIndices.category,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Card(
                            modifier = Modifier.padding(bottom = 8.dp, top = 2.dp),
                            colors = CardDefaults.cardColors().copy(
                                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
                            )
                        ) {
                            IconList(
                                itemNumber = 5,
                                activeNumber = max(5 - (level * 5 / indicesData.maxLevel), 1),
                                iconSize = 22.dp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.dp),
                                icon = {
                                    val size = if (it) 20.dp else 19.5.dp
                                    val tint =
                                        if (it) Color(0xFFFFC107) else Color.Gray.copy(alpha = 0.4f)
                                    androidx.compose.material.Icon(
                                        Icons.Rounded.Star,
                                        null,
                                        tint = tint,
                                        modifier = Modifier.size(size)
                                    )
                                })
                        }

                        Text(
                            text = currentIndices.text,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3,
                            lineHeight = 17.sp,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.alpha(0.8f)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            currentIndices.name.toMaterialFillIcon(),
                            null,
                            tint = colorIcon,
                            modifier = Modifier
                                .size(60.dp)
                                .alpha(0.9f)
                        )
                        Text(
                            text = currentIndices.name,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 14.dp, top = 6.dp)
                        )
                    }
                }
            }

            if (weatherDailies != null) {
                WeatherDailyInfoCard(
                    weatherDailies[currentIndex],
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    onClick = { type ->
                        viewModel.moveToHourlyPage(type, LocalDate.parse(currentIndices.date))
                        onSwitchPage?.invoke(1)
                    }
                )
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
        1 -> IndicesData(1, 3, Color(0xFFB2DFDB), Color(0xFF388E3C)) // 运动指数
        2 -> IndicesData(2, 4, Color(0xFFFFF8E1), Color(0xFFFBC02D)) // 洗车指数
        3 -> IndicesData(3, 7, Color(0xFFE3F2FD), Color(0xFF1976D2)) // 穿衣指数
        4 -> IndicesData(4, 3, Color(0xFFC8E6C9), Color(0xFF388E3C)) // 钓鱼指数
        5 -> IndicesData(5, 5, Color(0xFFFFEBEE), Color(0xFFD32F2F)) // 紫外线指数
        6 -> IndicesData(6, 5, Color(0xFFFFFFE0), Color(0xFFFBC02D)) // 旅游指数
        7 -> IndicesData(7, 5, Color(0xFFF3E5F5), Color(0xFF7B1FA2)) // 花粉过敏指数
        8 -> IndicesData(8, 7, Color(0xFFE1F5FE), Color(0xFF0288D1)) // 舒适度指数
        9 -> IndicesData(9, 4, Color(0xFFE0F2F1), Color(0xFF00796B)) // 感冒指数
        10 -> IndicesData(10, 5, Color(0xFFF5F5F5), Color(0xFF5D4037)) // 空气污染扩散条件指数
        11 -> IndicesData(11, 4, Color(0xFFFFFDE7), Color(0xFFFBC02D)) // 空调开启指数
        12 -> IndicesData(12, 5, Color(0xFFE0F7FA), Color(0xFF0097A7)) // 太阳镜指数
        13 -> IndicesData(13, 8, Color(0xFFFFF3E0), Color(0xFFF57C00)) // 化妆指数
        14 -> IndicesData(14, 6, Color(0xFFFFFDE7), Color(0xFFFFA000)) // 晾晒指数
        15 -> IndicesData(15, 5, Color(0xFFECEFF1), Color(0xFF455A64)) // 交通指数
        16 -> IndicesData(16, 5, Color(0xFFFFF8E1), Color(0xFFE65100)) // 防晒指数
        else -> IndicesData(0, 1, Color(0xFFF5F5F5), Color(0xFF616161)) // 其他
    }
}
package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage

import NewsCard
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.qiuqiuqiu.weatherPredicate.tools.toMaterialFillIcon
import com.qiuqiuqiu.weatherPredicate.ui.normal.CustomLineChartView
import com.qiuqiuqiu.weatherPredicate.ui.normal.DefaultElevatedCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.IconList
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.WeatherDailyInfoCard
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherDetailViewModel
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherIndicesDetailViewModel
import com.qweather.sdk.response.indices.IndicesDaily
import com.qweather.sdk.response.weather.WeatherDaily
import kotlinx.coroutines.delay
import java.time.LocalDate
import kotlin.math.max

@SuppressLint("NewApi")
@Composable
fun WeatherIndicesPage(
    weatherIndices: List<IndicesDaily>?,
    weatherDailies: List<WeatherDaily>?,
    viewModel: WeatherDetailViewModel,
    key: String,
    modifier: Modifier = Modifier,
    currentPageIndex: Int,
    pageIndex: Int,
    onThemeChanged: ((Color?, Color?, Boolean?) -> Unit)? = null,
    onSwitchPage: ((index: Int) -> Unit)? = null
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val radioGroup = listOf("今天", "明天", "后天")
    var showChart by remember { mutableStateOf(false) }

    val detailModel: WeatherIndicesDetailViewModel = hiltViewModel(key = key)

    if (!weatherIndices.isNullOrEmpty()) {
        val currentIndices = weatherIndices[currentIndex]
        val indicesData = indicesMapper(currentIndices.type.toInt())
        val colorBg = indicesData.nightColor
        val colorIcon = indicesData.dayColor
        val level = currentIndices.level.toInt()

        val contentColor = MaterialTheme.colorScheme.onSecondary

        LaunchedEffect(currentPageIndex) {
            if (currentPageIndex == pageIndex) {
                onThemeChanged?.invoke(colorBg, contentColor, false)
                if (detailModel._chartModel.value == null)
                    detailModel.initChartModel(weatherIndices.first(), viewModel)
                if (!showChart) {
                    delay(300)
                    showChart = true
                }
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (showChart) {
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
                        .copy(containerColor = colorBg, contentColor = contentColor)
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
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0f),
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
                                                activeContainerColor = MaterialTheme.colorScheme.background,
                                                activeContentColor = colorBg,
                                                inactiveContentColor = contentColor,
                                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(
                                                    alpha = 0.25f
                                                )
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
                                            if (it) Color(0xFFFFD607) else Color.Gray.copy(alpha = 0.4f)
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

                detailModel._chartModel.value?.let { model ->
                    DefaultElevatedCard(
                        bgColor = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 12.dp, horizontal = 10.dp),
                        ) {
                            Text(
                                text = model.type.text,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = Color.Transparent,
                                    contentColor = MaterialTheme.colorScheme.surface,
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.surface.copy(0.6f)
                                )
                            ) {
                                CustomLineChartView(
                                    chartModel = model,
                                    showLabel = true,
                                    selectedIndex = detailModel.selectedIndex.intValue,
                                    onEntryLocked = {
                                        detailModel.selectedIndex.intValue = it
                                    },
                                    modifier = Modifier.height(200.dp),
                                )
                            }
                        }

                    }

                    val col = when (weatherIndices.first().type.toInt()) {
                        1 -> 12
                        2 -> 35
                        3 -> 38
                        4 -> 18
                        5 -> -1
                        6 -> 18
                        7 -> -1
                        8 -> 10
                        9 -> 17
                        10 -> 41
                        11 -> 17
                        12 -> -1
                        13 -> 43
                        14 -> 13
                        15 -> 18
                        16 -> -1
                        else -> -1
                    }
                    if (col != -1)
                        NewsCard(key = key, col = col)
                }
            }
        }

        Spacer(modifier = Modifier.height(150.dp))
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
        1 -> IndicesData(1, 3, Color(0xFFA5D6A7), Color(0xFF4CAF50)) // 运动指数 青绿色
        2 -> IndicesData(2, 4, Color(0xFFB2DFDB), Color(0xFF26A69A)) // 洗车指数 浅紫
        3 -> IndicesData(3, 7, Color(0xFFE1BEE7), Color(0xFF8E24AA)) // 穿衣指数 粉紫
        4 -> IndicesData(4, 3, Color(0xFFA5D6A7), Color(0xFF4CAF50)) // 钓鱼指数 绿色
        5 -> IndicesData(5, 5, Color(0xFFB3E5FC), Color(0xFF039BE5)) // 紫外线指数 浅蓝
        6 -> IndicesData(6, 5, Color(0xFF76B3FF), Color(0xFF1976D2)) // 旅游指数 深蓝
        7 -> IndicesData(7, 5, Color(0xFF795548), Color(0xFFA67C52)) // 花粉过敏指数 粉色
        8 -> IndicesData(8, 7, Color(0xFFB2DFDB), Color(0xFF26A69A)) // 舒适度指数 青绿色
        9 -> IndicesData(9, 4, Color(0xFF80DEEA), Color(0xFF26C6DA)) // 感冒指数 蓝绿
        10 -> IndicesData(10, 5, Color(0xFFC1D1D9), Color(0xFF78909C)) // 空气污染扩散条件指数 灰蓝
        11 -> IndicesData(11, 4, Color(0xFFB3E5FC), Color(0xFF039BE5)) // 空调开启指数 浅蓝
        12 -> IndicesData(12, 5, Color(0xFFE1BEE7), Color(0xFF8E24AA)) // 太阳镜指数 粉紫
        13 -> IndicesData(13, 8, Color(0xFF795548), Color(0xFFA67C52)) // 化妆指数 粉色
        14 -> IndicesData(14, 6, Color(0xFF90CAF9), Color(0xFF1976D2)) // 晾晒指数 蓝色
        15 -> IndicesData(15, 5, Color(0xFFBBD5E1), Color(0xFF78909C)) // 交通指数 灰蓝
        16 -> IndicesData(16, 5, Color(0xFFCE93D8), Color(0xFFAB47BC)) // 防晒指数 浅紫
        else -> IndicesData(0, 1, Color(0xFFECEFF1), Color(0xFF607D8B)) // 其他 灰色
    }
}
package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qiuqiuqiu.weatherPredicate.model.LocationWeatherModel
import com.qiuqiuqiu.weatherPredicate.ui.normal.CustomLineChartView
import com.qiuqiuqiu.weatherPredicate.ui.normal.DefaultCard
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherAirDetailViewModel
import com.qweather.sdk.response.air.v1.AirV1CurrentResponse
import com.qweather.sdk.response.air.v1.Pollutant
import kotlinx.coroutines.delay

@Composable
fun WeatherAirDetailPage(
    model: LocationWeatherModel,
    currentPageIndex: Int,
    pageIndex: Int,
    modifier: Modifier = Modifier,
    onColorChanged: ((Color) -> Unit)? = null
) {
    val airViewModel: WeatherAirDetailViewModel = hiltViewModel()
    val airModel = airViewModel.model.collectAsState()
    val color = MaterialTheme.colorScheme.background
    var showChart by remember { mutableStateOf(false) }

    var selectedIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentPageIndex) {
        if (currentPageIndex == pageIndex) {
            onColorChanged?.invoke(color)
            model.airHourlies?.let {
                airViewModel.initModel(it)
            }
            if (!showChart) {
                delay(300)
                showChart = true
            }
        }

    }

    if (showChart) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            model.airCurrent?.let {
                CurrentAirCard(it)
            }

            airModel.value?.let {
                Spacer(modifier = Modifier.height(8.dp))
                DefaultCard(
                    bgColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp, horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "未来24小时空气质量趋势",
                            style = MaterialTheme.typography.titleSmall,
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
                                chartModel = it,
                                showLabel = true,
                                longLine = false,
                                drawYGrid = false,
                                selectedIndex = selectedIndex,
                                onEntryLocked = { index -> selectedIndex = index },
                                modifier = Modifier.height(180.dp),
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun CurrentAirCard(air: AirV1CurrentResponse) {
    val indices = air.indexes.first()
    val color = Color(indices.color.red, indices.color.green, indices.color.blue)
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 24.dp)
            ) {
                CircularProgressIndicator(
                    progress = { indices.aqi.toFloat() / 100 },
                    modifier = Modifier
                        .size(140.dp)
                        .alpha(0.6f),
                    strokeWidth = 10.dp,
                    color = color,
                    trackColor = MaterialTheme.colorScheme.surface
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "☘\uFE0F",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AQI",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp)
                        )
                    }
                    Text(
                        text = String.format("%.0f", indices.aqi),
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 42.sp)
                    )
                    Text(
                        text = indices.category,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(air.pollutants) { pop ->
                    PopulateTip(pop, color)
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Notifications, null, tint = Color(0xFFFFC107),
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .size(19.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = indices.health.effect,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                )
            }
        }
    }
}

@Composable
fun PopulateTip(pollutant: Pollutant, color: Color) {
    if (pollutant.subIndexes.isNotEmpty() && pollutant.subIndexes.first().aqi != null) {
        val aqi = pollutant.subIndexes.first().aqi
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(40.dp)
        ) {
            Text(
                text = String.format("%.0f", aqi), style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            LinearProgressIndicator(
                progress = aqi.toFloat() / 100f, color = color,
                backgroundColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(20.dp),
                strokeCap = StrokeCap.Round
            )
            Text(
                text = pollutant.name,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                modifier = Modifier
                    .alpha(0.6f)
                    .padding(top = 2.dp)
            )
        }
    }

}
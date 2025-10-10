package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage

import NewsCard
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.ui.normal.DefaultElevatedCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.map.ChinaMapScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.WeatherDailyInfoCard
import com.qweather.sdk.response.weather.WeatherDaily
import com.qweather.sdk.response.weather.WeatherNow
import kotlinx.coroutines.delay

@Composable
fun WeatherCurrentDetailPage(
    weatherNow: WeatherNow?,
    weatherDaily: WeatherDaily?,
    lastUpdateTime: String?,
    currentPageIndex: Int,
    pageIndex: Int,
    modifier: Modifier = Modifier,
    onThemeChanged: ((Color?, Color?, Boolean?) -> Unit)? = null,
    onSwitchPage: ((index: Int) -> Unit)? = null
) {
    var showChart by remember { mutableStateOf(false) }
    LaunchedEffect(currentPageIndex) {
        if (currentPageIndex == pageIndex) {
            onThemeChanged?.invoke(Color.Transparent, null, null)
            if (!showChart) {
                delay(300)
                showChart = true
            }
        }
    }

    if (showChart) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(170.dp))
            weatherNow?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .height(75.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Text(fontWeight = FontWeight.Light, fontSize = 70.sp, text = it.temp)
                    Box(modifier = Modifier.fillMaxHeight()) {
                        Text(
                            "℃",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .align(alignment = Alignment.TopStart)
                        )

                        Text(
                            text = it.text,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                            modifier = Modifier
                                .padding(start = 4.dp, bottom = 4.dp)
                                .align(alignment = Alignment.BottomStart)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "实时指数",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.4f)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (!lastUpdateTime.isNullOrBlank()) "更新于 $lastUpdateTime" else "",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.4f)
                )
            }

            weatherDaily?.let {
                WeatherDailyInfoCard(
                    weatherDaily,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    onClick = { onSwitchPage?.invoke(1) }
                )
            }

            DefaultElevatedCard(modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)) {
                ChinaMapScreen(
                    modifier = Modifier
                        .height(230.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            NewsCard(col = 5, count = 5, key = "weatherCurrent")

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
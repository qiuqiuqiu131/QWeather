package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.tools.toDay
import com.qiuqiuqiu.weatherPredicate.tools.toDayLabel
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseItem
import com.qiuqiuqiu.weatherPredicate.ui.theme.QWeatherFontFamily
import com.qiuqiuqiu.weatherPredicate.ui.theme.getQWeatherIconUnicode
import com.qweather.sdk.response.weather.WeatherDaily

@Composable
fun WeatherDailyDetailPage(weatherDailies: List<WeatherDaily>?, modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
    ) { WeatherDailyDetailCard(weatherDailies) }
}

@Composable
fun WeatherDailyDetailCard(weatherDailies: List<WeatherDaily>?) {
    weatherDailies?.let {
        LazyRow(
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .height(385.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(weatherDailies) { hw ->
                BaseItem(innerModifier = Modifier.padding(vertical = 12.dp), onClick = {}) {
                    DailyDetailWeatherItem(hw)
                }
            }

            item {
                // 占位，防止最后一个被遮挡
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "到底了",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.alpha(0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyDetailWeatherItem(hw: WeatherDaily) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(68.dp)
    ) {
        Text(text = hw.fxDate.toDayLabel(), style = MaterialTheme.typography.bodySmall)
        Text(
            text = hw.fxDate.toDay(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        HorizontalDivider(
            modifier = Modifier.padding(4.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = hw.iconDay.getQWeatherIconUnicode(),
            fontFamily = QWeatherFontFamily,
            fontSize = 26.sp,
            modifier = Modifier.padding(top = 6.dp, bottom = 10.dp)
        )
        Text(text = hw.textDay, style = MaterialTheme.typography.bodySmall)

        Text(
            text = "${hw.tempMax}℃",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )
        Text(
            text = "${hw.tempMin}℃",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
        )

        Text(
            text = hw.iconNight.getQWeatherIconUnicode(),
            fontFamily = QWeatherFontFamily,
            fontSize = 26.sp,
            modifier = Modifier.padding(top = 6.dp, bottom = 10.dp)
        )
        Text(text = hw.textNight, style = MaterialTheme.typography.bodySmall)

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Icon(
            Icons.Rounded.ArrowLeft,
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .graphicsLayer { rotationZ = windDirToAngle(hw.windDirDay) }
        )

        Text(
            text = hw.windDirDay,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(text = "${hw.windScaleDay}级", style = MaterialTheme.typography.bodySmall)
    }
}

// 风向转角度，默认0度（朝左）
fun windDirToAngle(windDir: String): Float =
    when {
        windDir.contains("东") && windDir.contains("南") -> -45f // 东南风
        windDir.contains("南") && windDir.contains("西") -> -135f // 西南风
        windDir.contains("西") && windDir.contains("北") -> -225f // 西北风
        windDir.contains("北") && windDir.contains("东") -> -315f // 东北风
        windDir.contains("东") -> 0f // 东风，箭头向左
        windDir.contains("南") -> -90f // 南风，箭头向下
        windDir.contains("西") -> -180f // 西风，箭头向右
        windDir.contains("北") -> -270f // 北风，箭头向上
        else -> 0f
    }

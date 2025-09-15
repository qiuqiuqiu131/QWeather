package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseItem
import com.qiuqiuqiu.weatherPredicate.ui.normal.NullNestScrollConnection
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.HourlyDetailType
import com.qweather.sdk.response.weather.WeatherDaily

@Composable
fun WeatherDailyInfoCard(
    weather: WeatherDaily,
    modifier: Modifier = Modifier,
    onClick: ((HourlyDetailType) -> Unit)? = null
) {
    val nestScrollConnection = remember { NullNestScrollConnection() }
    LazyRow(
        horizontalArrangement = Arrangement.Start,
        modifier = modifier.nestedScroll(nestScrollConnection)
    ) {
        var text =
            if (weather.textDay == weather.textNight) weather.textDay else "${weather.textDay}转${weather.textNight}"
        if (text.length > 4)
            text = weather.textDay

        item {
            DetailTipItem(
                Icons.Default.WbSunny,
                text,
                Color(HourlyDetailType.Temp.primaryColor),
                "天气状况",
                onClick = {
                    onClick?.invoke(HourlyDetailType.Temp)
                })
        }

        item {
            DetailTipItem(
                Icons.Default.DeviceThermostat,
                "${weather.tempMax}/${weather.tempMin}℃",
                Color(HourlyDetailType.Temp.primaryColor),
                "温度",
                onClick = {
                    onClick?.invoke(HourlyDetailType.Temp)
                }
            )
        }

        item {
            DetailTipItem(
                HourlyDetailType.Pop.icon,
                "${weather.precip}mm",
                Color(HourlyDetailType.Pop.primaryColor),
                "降水量", onClick = {
                    onClick?.invoke(HourlyDetailType.Pop)
                }
            )
        }

        item {
            DetailTipItem(
                HourlyDetailType.Hum.icon,
                "${weather.humidity}%",
                Color(HourlyDetailType.Hum.primaryColor),
                "湿度",
                onClick = {
                    onClick?.invoke(HourlyDetailType.Hum)
                })
        }

        item {
            DetailTipItem(
                HourlyDetailType.Wind.icon,
                "${weather.windScaleDay}级",
                Color(HourlyDetailType.Wind.primaryColor),
                weather.windDirDay,
                onClick = {
                    onClick?.invoke(HourlyDetailType.Wind)
                }
            )
        }



        item {
            DetailTipItem(
                HourlyDetailType.Cloud.icon,
                "${weather.cloud}%",
                Color(HourlyDetailType.Cloud.primaryColor),
                "云量", onClick = {
                    onClick?.invoke(HourlyDetailType.Cloud)
                }
            )
        }

        item {
            DetailTipItem(
                HourlyDetailType.Pressure.icon,
                "${weather.pressure}hPa",
                Color(HourlyDetailType.Pressure.primaryColor),
                "气压", onClick = {
                    onClick?.invoke(HourlyDetailType.Pressure)
                }
            )
        }

        if (!weather.sunset.isNullOrBlank())
            item {
                DetailTipItem(
                    Icons.Default.WbSunny, weather.sunset,
                    Color(HourlyDetailType.Temp.primaryColor), "日落",
                    onClick = {
                        onClick?.invoke(HourlyDetailType.Temp)
                    })
            }
    }
}

@Composable
fun DetailTipItem(
    icon: ImageVector,
    content: String,
    color: Color,
    name: String,
    onClick: (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = Modifier
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        BaseItem(onClick = onClick, bgColor = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier
                    .padding(4.dp, vertical = 8.dp)
                    .width(58.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(top = 6.dp, bottom = 6.dp)
                )
                Text(text = content, style = MaterialTheme.typography.labelLarge, fontSize = 13.sp)
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 10.sp,
                    fontSize = 10.sp,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }

    }
}
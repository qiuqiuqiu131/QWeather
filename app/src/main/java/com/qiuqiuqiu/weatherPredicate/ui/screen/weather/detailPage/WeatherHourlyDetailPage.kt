package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.tools.toDayLabel
import com.qiuqiuqiu.weatherPredicate.tools.toMaterialFillIcon
import com.qiuqiuqiu.weatherPredicate.ui.normal.CustomLineChartView
import com.qiuqiuqiu.weatherPredicate.ui.normal.DefaultCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.NullNestScrollConnection
import com.qiuqiuqiu.weatherPredicate.ui.normal.ScrollableCenterRowList
import com.qiuqiuqiu.weatherPredicate.ui.normal.WeatherIcon
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.DetailTipItem
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.WeatherStatusInfoCard
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.HourlyDetailType
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherDetailViewModel
import com.qweather.sdk.response.weather.WeatherHourly
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@Composable
fun WeatherHourlyDetailPage(
    viewModel: WeatherDetailViewModel,
    currentPageIndex: Int,
    pageIndex: Int,
    modifier: Modifier = Modifier,
    onColorChanged: ((Color) -> Unit)? = null,
    onSwitchPage: ((name: String) -> Unit)? = null
) {
    val weatherModel by viewModel.locationWeather.collectAsState()
    val chartModel by viewModel.chartModel.collectAsState()

    val color = MaterialTheme.colorScheme.background
    LaunchedEffect(currentPageIndex) {
        if (currentPageIndex == pageIndex)
            onColorChanged?.invoke(color)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
    ) {
        viewModel.dates.value?.let {
            ScrollableCenterRowList(
                itemCount = it.size,
                itemIndex = viewModel.selectedDate.intValue,
                canScroll = true,
                selectedItemChanged = { index ->
                    viewModel.onDateChanged(index)
                }, modifier = Modifier
                    .background(color)
                    .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
            ) { index, isSelected ->
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = it[index].toString().toDayLabel(),
                    )
                    Card(
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors().copy(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.background
                        ),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(30.dp),
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp),
                            text = it[index].dayOfMonth.toString(),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 5.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        chartModel?.let {
            Spacer(modifier = Modifier.height(4.dp))
            val nestScrollConnection = remember { NullNestScrollConnection() }
            DefaultCard(
                bgColor = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .nestedScroll(nestScrollConnection)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    HourlyTipCard(
                        weatherModel.weatherHourliesMore?.get(viewModel.selectedEntry.intValue),
                        viewModel.selectedHourlyType.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )

                    OutlinedCard(
                        modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.surface,
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surface.copy(0.6f))
                    ) {
                        CustomLineChartView(
                            it,
                            selectedIndex = viewModel.selectedEntry.intValue,
                            modifier = Modifier.height(240.dp),
                            onEntryLocked = { entry ->
                                viewModel.onEntryChanged(entry)
                            })
                    }

                    ScrollableCenterRowList(
                        itemCount = viewModel.hourlyTypes.size,
                        itemIndex = viewModel.selectedHourlyType.value.intValue,
                        selectedItemChanged = { index ->
                            viewModel.onSwitchChartType(index)
                        },
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth()
                    ) { index, isSelected ->
                        val type =
                            viewModel.hourlyTypes.firstOrNull { ind -> ind.intValue == index }
                        type?.let { tp ->
                            Card(
                                colors = CardDefaults.cardColors().copy(
                                    containerColor = if (isSelected) Color(type.primaryColor) else MaterialTheme.colorScheme.surface.copy(
                                        0.7f
                                    )
                                ),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        tp.icon,
                                        null,
                                        modifier = Modifier.size(16.dp),
                                        tint =
                                            if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                                alpha = 0.5f
                                            )
                                    )
                                    if (isSelected) {
                                        Text(
                                            text = tp.text,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.background,
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        weatherModel.indicesDailies?.let {
            val nestScrollConnection = remember { NullNestScrollConnection() }
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .nestedScroll(nestScrollConnection)
            ) {
                items(it) { indices ->
                    val data = indicesMapper(indices.type.toInt())
                    val color = if (isSystemInDarkTheme()) data.dayColor else data.nightColor
                    DetailTipItem(
                        indices.name.toMaterialFillIcon(),
                        indices.category,
                        color,
                        indices.name,
                        onClick = {
                            onSwitchPage?.invoke(indices.name.replace("指数", ""))
                        })
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            WeatherStatusInfoCard(
                it, bgColor = MaterialTheme.colorScheme.background,
                icon = Icons.Default.NotificationsActive,
                iconColor = Color(0xFFFFC107),
                onIndicesClick = { name ->
                    onSwitchPage?.invoke(name.replace("指数", ""))
                }, modifier = Modifier.padding(vertical = 1.dp)
            )
        }
    }
}


@SuppressLint("NewApi")
@Composable
fun HourlyTipCard(
    hourlyWeather: WeatherHourly?,
    selectedType: HourlyDetailType,
    modifier: Modifier = Modifier
) {

    hourlyWeather?.let {
        Box(modifier = modifier) {
            when (selectedType) {
                HourlyDetailType.Temp -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = it.temp,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 26.sp
                                )
                            )
                            Text(
                                text = "℃",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                modifier = Modifier.align(
                                    Alignment.Top
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            WeatherIcon(id = it.icon)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = OffsetDateTime.parse(it.fxTime)
                                    .format(DateTimeFormatter.ofPattern("HH:mm")),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = it.text,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${it.windDir}${it.windScale}级",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                HourlyDetailType.Pop -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = it.pop,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 26.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "%",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                modifier = Modifier.align(
                                    Alignment.Bottom
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = OffsetDateTime.parse(it.fxTime)
                                    .format(DateTimeFormatter.ofPattern("hh:mm")),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "全天降水量${it.precip}mm",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                HourlyDetailType.Wind -> {
                    var windMin: String = ""
                    var windMax: String? = null
                    if (it.windScale.contains("-")) {
                        windMin = it.windScale.split("-").first()
                        windMax = it.windScale.split("-").last()
                    } else
                        windMin = it.windScale
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${it.windDir}${windMin}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 26.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "级",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                modifier = Modifier.align(
                                    Alignment.Bottom
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = OffsetDateTime.parse(it.fxTime)
                                    .format(DateTimeFormatter.ofPattern("hh:mm")),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "阵风${windMax}级",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "风速${it.windSpeed}km/h",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                HourlyDetailType.Hum -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = it.humidity,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 26.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "%",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                modifier = Modifier.align(
                                    Alignment.Bottom
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = OffsetDateTime.parse(it.fxTime)
                                    .format(DateTimeFormatter.ofPattern("hh:mm")),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "露点${it.dew}℃",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                HourlyDetailType.Cloud -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = it.cloud,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 26.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "%",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                modifier = Modifier.align(
                                    Alignment.Bottom
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = OffsetDateTime.parse(it.fxTime)
                                    .format(DateTimeFormatter.ofPattern("hh:mm")),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = it.text,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                HourlyDetailType.Pressure -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = it.pressure,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 26.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "hPa",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                modifier = Modifier.align(
                                    Alignment.Bottom
                                )
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = OffsetDateTime.parse(it.fxTime)
                                    .format(DateTimeFormatter.ofPattern("hh:mm")),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = it.text,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
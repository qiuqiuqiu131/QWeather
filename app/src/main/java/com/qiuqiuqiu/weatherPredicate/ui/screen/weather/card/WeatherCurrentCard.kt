package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.LocalAppViewModel
import com.qiuqiuqiu.weatherPredicate.model.weather.CityType
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseItem
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background.JieQiType
import com.qweather.sdk.response.geo.Location
import com.qweather.sdk.response.weather.WeatherDaily
import com.qweather.sdk.response.weather.WeatherNow

/** 当前天气卡片 */
@Composable
fun WeatherCurrentCard(
    type: CityType,
    location: Location?,
    weatherNow: WeatherNow?,
    weatherDaily: WeatherDaily?,
    alpha: State<Float>,
    cityHide: State<Boolean>,
    centerScreen: Boolean,
    onCityClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val appViewModel = LocalAppViewModel.current
    Box(modifier = Modifier.fillMaxWidth()) {
        appViewModel.jieqi.value?.let {
            val type = JieQiType.entries.firstOrNull { t -> t.text == it.name } ?: JieQiType.LiChun
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = type.backgroundColor),
                modifier = Modifier
                    .padding(top = 72.dp, start = 32.dp)
                    .alpha(1 - alpha.value)
                    .width(40.dp)
            ) {
                Text(
                    text = it.name,
                    letterSpacing = 5.sp,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 26.sp,
                        lineHeight = 36.sp,
                    ),
                    softWrap = true,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 18.dp, horizontal = 4.5.dp)
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 24.dp, bottom = 8.dp)
                .pointerInput(Unit) {
                    detectTapGestures { onClick?.invoke() }
                }
        ) {
            // 城市
            location?.let {
                if (centerScreen) {
                    BaseItem(
                        innerModifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        onClick = onCityClick
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .alpha((if (cityHide.value) 0f else 1f))
                        ) {
                            Text(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 19.sp,
                                text =
                                    "${it.adm1} " +
                                            (if (it.adm2.equals(it.name)) "" else it.adm2 + " ") +
                                            "${it.name}",
                            )
                            if (type == CityType.Position) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(bottom = 2.dp)
                                )
                            }
                        }

                    }
                } else {
                    Text(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp,
                        text =
                            "${it.adm1} " +
                                    (if (it.adm2.equals(it.name)) "" else it.adm2 + " ") +
                                    "${it.name}",
                        modifier = Modifier.alpha((if (cityHide.value) 0f else 1f))
                    )
                }
            }

            // 温度
            weatherNow?.let {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(start = 12.dp, top = 4.dp)
                        .alpha(1 - alpha.value)
                ) {
                    Text(fontWeight = FontWeight.Light, fontSize = 90.sp, text = it.temp)
                    Text(
                        "℃",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(alignment = Alignment.Top)
                    )
                }
            }

            weatherDaily?.let {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "${it.tempMax} / ${it.tempMin}℃",
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .alpha(1 - alpha.value)
                )
            }

            // 天气描述
            weatherNow?.let {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .alpha(1 - alpha.value)
                ) {
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = it.text,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(style = MaterialTheme.typography.titleMedium, text = it.windDir)
                }
            }
        }
    }
}
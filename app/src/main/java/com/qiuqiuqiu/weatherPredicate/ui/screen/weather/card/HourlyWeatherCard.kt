package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qiuqiuqiu.weatherPredicate.tools.toTimeWithPeriod
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseItem
import com.qiuqiuqiu.weatherPredicate.ui.normal.CustomDivider
import com.qiuqiuqiu.weatherPredicate.ui.normal.WeatherIcon
import com.qweather.sdk.response.weather.WeatherHourly

/** 24小时内天气列表 */
@Composable
fun HourlyWeatherCard(
    weathers: List<WeatherHourly>,
    onClick: (() -> Unit)? = null,
    onItemClick: ((WeatherHourly) -> Unit)? = null
) {
    BaseCard(onClick = onClick) {
        LazyRow(horizontalArrangement = Arrangement.Start) {
            items(weathers) { hw ->
                BaseItem(
                    innerModifier = Modifier.padding(vertical = 12.dp),
                    onClick = { onItemClick?.invoke(hw) }
                ) { HourlyWeatherItem(hw) }
            }
        }
    }
}

@Composable
fun HourlyWeatherItem(hw: WeatherHourly) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Text(text = hw.fxTime.toTimeWithPeriod(), style = MaterialTheme.typography.bodySmall)
        CustomDivider(modifier = Modifier.padding(4.dp))
        WeatherIcon(
            hw.icon, modifier = Modifier
                .padding(top = 6.dp, bottom = 10.dp)
        )
        Text(text = hw.text, style = MaterialTheme.typography.bodySmall)
        Text(text = "${hw.temp}℃", style = MaterialTheme.typography.labelLarge)
    }
}
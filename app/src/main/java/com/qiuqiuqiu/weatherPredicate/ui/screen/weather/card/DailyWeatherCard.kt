package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.tools.toDayLabel
import com.qiuqiuqiu.weatherPredicate.tools.toMonthDay
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseItem
import com.qiuqiuqiu.weatherPredicate.ui.normal.CustomDivider
import com.qiuqiuqiu.weatherPredicate.ui.normal.WeatherIcon
import com.qweather.sdk.response.weather.WeatherDaily

/** 未来7天天气列表 */
@Composable
fun DailyWeatherCard(
    weathers: List<WeatherDaily>,
    onClick: (() -> Unit)? = null,
    onItemClick: ((WeatherDaily) -> Unit)? = null
) {
    BaseCard(
        title = "多日预报",
        endCorner = {
            IconButton(onClick = { onClick?.invoke() }, modifier = Modifier.size(25.dp)) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    modifier = Modifier.size(20.dp),
                    contentDescription = null
                )
            }
        },
        onClick = onClick
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.Start,
        ) {
            items(weathers) { hw ->
                BaseItem(
                    innerModifier = Modifier.padding(vertical = 12.dp),
                    onClick = { onItemClick?.invoke(hw) }
                ) { DailyWeatherItem(hw) }
            }
        }
    }
}

@Composable
fun DailyWeatherItem(hw: WeatherDaily) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Text(text = hw.fxDate.toDayLabel(), style = MaterialTheme.typography.bodySmall)
        Text(
            text = hw.fxDate.toMonthDay(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alpha(0.6f),
            fontSize = 10.sp
        )

        CustomDivider(modifier = Modifier.padding(4.dp))

        WeatherIcon(hw.iconDay, modifier = Modifier.padding(top = 6.dp, bottom = 10.dp))
        Text(text = hw.textDay, style = MaterialTheme.typography.bodySmall)

        Text(
            text = "${hw.tempMax}℃",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
        )
        Text(
            text = "${hw.tempMin}℃",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
        )

        WeatherIcon(hw.iconNight, modifier = Modifier.padding(top = 6.dp, bottom = 10.dp))
        Text(text = hw.textNight, style = MaterialTheme.typography.bodySmall)

        CustomDivider(modifier = Modifier.padding(4.dp))

        Text(text = hw.windDirDay, style = MaterialTheme.typography.bodySmall)

        Text(text = "${hw.windScaleDay}级", style = MaterialTheme.typography.bodySmall)
    }
}



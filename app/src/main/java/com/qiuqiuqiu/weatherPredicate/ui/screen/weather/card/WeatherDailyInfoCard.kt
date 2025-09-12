package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qweather.sdk.response.weather.WeatherDaily

@Composable
fun WeatherDailyInfoCard(
    weather: WeatherDaily,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    LazyRow(horizontalArrangement = Arrangement.Start, modifier = modifier) {
        var text =
            if (weather.textDay == weather.textNight) weather.textDay else "${weather.textDay}转${weather.textNight}"
        if (text.length > 4)
            text = weather.textDay

        item { DetailTipItem(Icons.Default.WbSunny, text, "天气状况", onClick) }

        item {
            DetailTipItem(
                Icons.Default.Thermostat,
                "${weather.tempMax}/${weather.tempMin}℃",
                "温度",
                onClick
            )
        }

        item { DetailTipItem(Icons.Outlined.LightMode, weather.uvIndex.toUV(), "紫外线", onClick) }

        item { DetailTipItem(Icons.Outlined.WaterDrop, "${weather.humidity}%", "湿度", onClick) }

        item {
            DetailTipItem(
                Icons.Outlined.Air,
                "${weather.windScaleDay}级",
                weather.windDirDay,
                onClick
            )
        }

        if (!weather.sunset.isNullOrBlank())
            item { DetailTipItem(Icons.Default.WbSunny, weather.sunset, "日落", onClick) }
    }
}

@Composable
fun DetailTipItem(icon: ImageVector, content: String, name: String, onClick: (() -> Unit)? = null) {
    val color1 = CardColors(
        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
        disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )

    Card(
        modifier = Modifier
            .padding(4.dp)
            .pointerInput(Unit) {
                detectTapGestures { onClick?.invoke() }
            },
        shape = RoundedCornerShape(12.dp),
        colors = color1
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp, vertical = 8.dp)
                .width(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(top = 6.dp, bottom = 6.dp)
            )
            Text(text = content, style = MaterialTheme.typography.labelLarge, fontSize = 13.sp)
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 10.sp,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.6f)
            )
        }
    }
}
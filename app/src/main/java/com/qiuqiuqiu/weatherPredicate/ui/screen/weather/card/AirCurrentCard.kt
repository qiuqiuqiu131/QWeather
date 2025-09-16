package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseCard
import com.qweather.sdk.response.air.v1.AirV1CurrentResponse

/** 空气质量卡片 */
@SuppressLint("DefaultLocale")
@Composable
fun AirCurrentCard(airCurrent: AirV1CurrentResponse, onClick: (() -> Unit)? = null) {
    val indices = airCurrent.indexes.first()
    val populates = airCurrent.pollutants.map { it.fullName }.take(3).joinToString("、")
    val text = indices.health.effect
    BaseCard(
        title = "空气质量", onClick = onClick,
        endCorner = {
            IconButton(onClick = { onClick?.invoke() }, modifier = Modifier.size(25.dp)) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    modifier = Modifier.size(20.dp),
                    contentDescription = null
                )
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .height(92.dp)
        )
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .width(100.dp)
            )
            {
                CircularProgressIndicator(
                    progress = { indices.aqi.toFloat() / 100 },
                    modifier = Modifier
                        .size(75.dp)
                        .alpha(0.6f),
                    color = Color(indices.color.red, indices.color.green, indices.color.blue),
                    trackColor = MaterialTheme.colorScheme.surfaceContainer
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = indices.category, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = String.format("%.0f", indices.aqi),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.titleSmall,
                    text = "当前AQI指数为 ${indices.aqi}",
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    modifier = Modifier.alpha(0.6f),
                    text = text, softWrap = false, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Justify
                )
            }
        }
    }
}
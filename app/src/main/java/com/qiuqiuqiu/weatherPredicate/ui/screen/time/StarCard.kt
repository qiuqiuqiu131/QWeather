package com.qiuqiuqiu.weatherPredicate.ui.screen.time

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qiuqiuqiu.weatherPredicate.model.DailyFortuneItem
import com.qiuqiuqiu.weatherPredicate.model.DailyFortuneResult
import com.qiuqiuqiu.weatherPredicate.model.StarModel
import com.qiuqiuqiu.weatherPredicate.ui.normal.ElevatedBaseCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.IconList
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.DailyWeatherItem

@Composable
fun StarCard(star: StarModel) {
    ElevatedBaseCard(title = "星座运势") {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(), verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(60.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = star.name, style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "今日运势",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(0.6f)
                        )

                        star.content.list.firstOrNull { it.type == "综合指数" }?.content.let { score ->
                            val activeNumber =
                                (score?.filter { it.isDigit() }?.toIntOrNull() ?: 0) / 20
                            IconList(
                                itemNumber = 5,
                                activeNumber = activeNumber,
                                iconSize = 20.dp,
                                icon = { isActive ->
                                    Icon(
                                        if (isActive) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                        null,
                                        tint = Color(0xFFFFC107),
                                        modifier = Modifier.size(16.dp)
                                    )
                                })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            star.content.list.firstOrNull{it.type == "今日概述"}?.content?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.8f),
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun StarCardPreview() {
    val model = StarModel(
        "处女座",
        DailyFortuneResult(
            listOf(
                DailyFortuneItem("综合指数", "80%"),
                DailyFortuneItem("今日概述", "今天朋友里面有些计较算计的人，沟通不顺利。部分人可能会比较想宅家躺吃...")
            )
        ))
    StarCard(model)
}
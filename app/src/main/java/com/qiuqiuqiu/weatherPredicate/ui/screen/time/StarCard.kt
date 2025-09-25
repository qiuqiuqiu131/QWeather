package com.qiuqiuqiu.weatherPredicate.ui.screen.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.model.DailyFortuneItem
import com.qiuqiuqiu.weatherPredicate.model.DailyFortuneResult
import com.qiuqiuqiu.weatherPredicate.model.StarModel
import com.qiuqiuqiu.weatherPredicate.ui.normal.ElevatedBaseCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.IconList

@Composable
fun StarCard(star: StarModel) {
    val type = StarType.entries.firstOrNull{ t -> t.text == star.name} ?: StarType.Aries
    ElevatedBaseCard(title = "星座运势") {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(60.dp)) {
                    Text(text = type.icon, fontSize = 50.sp)
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 14.dp)
                ) {
                    Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = type.text, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = type.date,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .align(Alignment.Bottom)
                                .alpha(0.6f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "今日运势",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(0.6f).padding(end = 4.dp)
                        )

                        star.content.list.firstOrNull { it.type == "综合指数" }?.content.let { score ->
                            val activeNumber =
                                (score?.filter { it.isDigit() }?.toIntOrNull() ?: 0) / 20
                            IconList(
                                itemNumber = 5,
                                activeNumber = activeNumber,
                                iconSize = 17.dp,
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
            Spacer(modifier = Modifier.height(6.dp))
            star.content.list.firstOrNull { it.type == "今日概述" }?.content?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 17.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp)
                        .alpha(0.8f),
                    maxLines = 2,
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
                DailyFortuneItem(
                    "今日概述",
                    "今天朋友里面有些计较算计的人，沟通不顺利。部分人可能会比较想宅家躺吃..."
                )
            )
        )
    )
    StarCard(model)
}

enum class StarType(val text: String, val label: String, val icon: String, val date: String) {
    Aries("白羊座", "aries", "♈", "03/21 - 04/19"),
    Taurus("金牛座", "taurus", "♉", "04/20 - 05/20"),
    Gemini("双子座", "gemini", "♊", "05/21 - 06/21"),
    Cancer("巨蟹座", "cancer", "♋", "06/22 - 07/22"),
    Leo("狮子座", "leo", "♌", "07/23 - 08/22"),
    Virgo("处女座", "virgo", "♍", "08/23 - 09/22"),
    Libra("天秤座", "libra", "♎", "09/23 - 10/23"),
    Scorpio("天蝎座", "scorpio", "♏", "10/24 - 11/22"),
    Sagittarius("射手座", "sagittarius", "♐", "11/23 - 12/21"),
    Capricorn("摩羯座", "capricorn", "♑", "12/22 - 01/19"),
    Aquarius("水瓶座", "aquarius", "♒", "01/20 - 02/18"),
    Pisces("双鱼座", "pisces", "♓", "02/19 - 03/20")
}
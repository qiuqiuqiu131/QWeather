package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseItem
import com.qiuqiuqiu.weatherPredicate.ui.normal.CustomDivider
import com.qiuqiuqiu.weatherPredicate.ui.normal.SearchBaseCard
import com.qweather.sdk.response.geo.Location

@Composable
fun PoiCard(
    rangePois: List<Location>,
    modifier: Modifier = Modifier,
    onClick: ((Location) -> Unit)? = null
) {
    val pois = rangePois.take(5).withIndex()
    val count = pois.count()
    SearchBaseCard(title = "附近景点", endCorner = {
        val pos = pois.first().value
        Text(text = "${pos.adm1} ${pos.adm2}", style = MaterialTheme.typography.labelMedium)
    }) {
        Column {
            pois.forEach {
                PoiItem(it, modifier, onClick)
                if (it.index != count - 1) {
                    CustomDivider(modifier = Modifier.padding(horizontal = 14.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun PoiItem(
    poi: IndexedValue<Location>,
    modifier: Modifier = Modifier,
    onClick: ((Location) -> Unit)? = null
) {
    BaseItem(onClick = { onClick?.invoke(poi.value) }) {
        Row(
            modifier = Modifier
                .height(40.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(3f)
            ) {
                Text(
                    text = (poi.index + 1).toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${poi.value.name}", softWrap = false, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1.25f)
            ) {
                Text(text = "评分：${poi.value.rank}分", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
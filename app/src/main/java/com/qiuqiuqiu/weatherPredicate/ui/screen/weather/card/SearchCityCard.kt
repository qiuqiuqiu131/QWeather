package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseItem
import com.qiuqiuqiu.weatherPredicate.ui.normal.SearchBaseCard
import com.qweather.sdk.response.geo.Location

@Composable
fun SearchCityCard(cities: List<Location>, onClick: ((Location) -> Unit)? = null) {
    SearchBaseCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        val size = cities.size
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(cities) { index, it ->
                SearchCityItem(it, onClick = onClick)
                if (index != size - 1)
                    HorizontalDivider(modifier = Modifier.alpha(0.7f))
            }
        }
    }
}

@Composable
fun SearchCityItem(city: Location, onClick: ((Location) -> Unit)? = null) {
    var text = ""
    if (city.name == city.adm2)
        text = "${city.name}市, ${city.adm1}, ${city.country}"
    else
        text = "${city.name}, ${city.adm2}市, ${city.adm1}"

    BaseItem(
        modifier = Modifier
            .fillMaxSize()
            .height(50.dp),
        onClick = { onClick?.invoke(city) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxSize()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
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
import com.qiuqiuqiu.weatherPredicate.ui.normal.InfinitePageContainer
import com.qiuqiuqiu.weatherPredicate.ui.normal.SearchBaseCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.WeatherIcon
import com.qweather.sdk.response.geo.Location
import com.qweather.sdk.response.weather.WeatherDaily

@Composable
fun TopCityCard(
    cities: List<Pair<Location, WeatherDaily>>,
    onClick: ((Location) -> Unit)? = null
) {
    val composes = cities.withIndex().map { it.index to it.value }
        .chunked(3).map { @Composable { TopCityList(it, onClick) } }

    SearchBaseCard(title = "热门城市") {
        InfinitePageContainer(composes)
    }
}

@Composable
fun TopCityList(
    cities: List<Pair<Int, Pair<Location, WeatherDaily>>>,
    onClick: ((Location) -> Unit)? = null
) {
    val count = cities.size
    Column {
        cities.forEachIndexed { index, it ->
            CityItem(it, onClick)
            if (index != count - 1) {
                CustomDivider(modifier = Modifier.padding(horizontal = 14.dp))
            }
        }
    }
}

@Composable
fun CityItem(city: Pair<Int, Pair<Location, WeatherDaily>>, onClick: ((Location) -> Unit)? = null) {
    val location = city.second.first
    val weather = city.second.second
    BaseItem(onClick = { onClick?.invoke(location) }) {
        Row(
            modifier = Modifier
                .height(40.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = (city.first + 1).toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${location.name}市", softWrap = false, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(2f)
            ) {
                val isEqual = weather.textDay == weather.textNight
                WeatherIcon(id = weather.iconDay, imageSize = 26.dp)
                if (!isEqual) {
                    Spacer(modifier = Modifier.width(4.dp))
                    WeatherIcon(id = weather.iconNight, imageSize = 26.dp)
                } else
                    Spacer(modifier = Modifier.width(10.dp))

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (isEqual) weather.textDay else "${weather.textDay}转${weather.textNight}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${weather.tempMax} /${weather.tempMin}℃",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }

}
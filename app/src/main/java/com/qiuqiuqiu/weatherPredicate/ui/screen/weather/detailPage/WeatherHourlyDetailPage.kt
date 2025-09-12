package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.tools.toDateString
import com.qiuqiuqiu.weatherPredicate.tools.toDay
import com.qiuqiuqiu.weatherPredicate.tools.toDayLabel
import com.qiuqiuqiu.weatherPredicate.ui.normal.ScrollableCenterRowList
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.WeatherStatusInfoCard
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherDetailViewModel

@Composable
fun WeatherHourlyDetailPage(
    viewModel: WeatherDetailViewModel,
    currentPageIndex: Int,
    pageIndex: Int,
    modifier: Modifier = Modifier,
    onColorChanged: ((Color) -> Unit)? = null,
    onSwitchPage: ((name: String) -> Unit)? = null
) {
    val weatherModel by viewModel.locationWeather.collectAsState()
    var selectedDate by remember { mutableIntStateOf(0) }

    val color = MaterialTheme.colorScheme.background
    LaunchedEffect(currentPageIndex) {
        if (currentPageIndex == pageIndex)
            onColorChanged?.invoke(color)
    }

    val date =
        weatherModel.weatherHourliesMore?.groupBy { it.fxTime.toDateString() }?.map { it.key }

    Column(
        modifier =
            modifier
                .fillMaxSize()
    ) {
        date?.let {
            ScrollableCenterRowList(
                itemCount = date.size,
                itemIndex = selectedDate,
                canScroll = false,
                selectedItemChanged = {
                    selectedDate = it
                }, modifier = Modifier
                    .background(color)
                    .padding(horizontal = 8.dp)
            ) { index, isSelected ->
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        text = date[index].toDayLabel(),
                    )
                    Card(
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors().copy(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.background
                        ),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(30.dp),
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp),
                            text = date[index].toDay(),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 5.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        weatherModel.indicesDailies?.let {
            Spacer(modifier = Modifier.height(8.dp))
            WeatherStatusInfoCard(
                it, bgColor = MaterialTheme.colorScheme.background,
                onIndicesClick = { name ->
                    onSwitchPage?.invoke(name.replace("指数", ""))
                }, modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun WeatherAirDetailPage(
    modifier: Modifier = Modifier,
    currentPageIndex: Int,
    pageIndex: Int,
    onColorChanged: ((Color) -> Unit)? = null
) {
    val color = MaterialTheme.colorScheme.background
    LaunchedEffect(currentPageIndex) {
        if (currentPageIndex == pageIndex)
            onColorChanged?.invoke(color)
    }
}
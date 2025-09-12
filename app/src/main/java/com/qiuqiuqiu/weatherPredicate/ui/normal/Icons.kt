package com.qiuqiuqiu.weatherPredicate.ui.normal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.qiuqiuqiu.weatherPredicate.tools.getWeatherURL
import com.qiuqiuqiu.weatherPredicate.ui.theme.QWeatherFontFamily
import com.qiuqiuqiu.weatherPredicate.ui.theme.getQWeatherIconUnicode

@Composable
fun WeatherIcon(id: String, modifier: Modifier = Modifier) {
    val useFont = false
    Box(modifier = modifier) {
        if (useFont)
            Text(
                text = id.getQWeatherIconUnicode(),
                fontFamily = QWeatherFontFamily,
                fontSize = 26.sp
            )
        else
            AsyncImage(
                model = getWeatherURL(id), null,
                modifier = Modifier.size(30.dp)
            )
    }
}
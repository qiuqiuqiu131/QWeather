package com.qiuqiuqiu.weatherPredicate.ui.screen

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.qiuqiuqiu.weatherPredicate.model.weather.TimelyChartModel
import com.qiuqiuqiu.weatherPredicate.ui.normal.ChartPoint
import com.qiuqiuqiu.weatherPredicate.ui.normal.CustomLineChartView
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.HourlyDetailType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// 示例用法
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun CustomLineChartViewPreview() {
    val now = OffsetDateTime.of(LocalDateTime.of(2025, 9, 13, 12, 0), ZoneOffset.UTC)
    val url = "https://a.hecdn.net/img/common/icon/202106d/101.png"
    val context = androidx.compose.ui.platform.LocalContext.current

    var iconBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 异步加载 Bitmap
    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            val bmp = Glide.with(context)
                .asBitmap()
                .load(url)
                .submit(48, 48)
                .get()
            withContext(Dispatchers.Main) {
                iconBitmap = bmp
            }
        }
    }

    if (iconBitmap == null) {
        Text("图标加载中…")
        return
    }

    val data = List(168) { i ->
        ChartPoint(
            time = now.plusHours(i.toLong()),
            icon = iconBitmap!!,
            value = (20 + Math.random() * 10).toFloat()
        )
    }

    val model by remember {
        mutableStateOf(
            TimelyChartModel(
                data,
                "test",
                type = HourlyDetailType.Pressure
            )
        )
    }

    var locked by remember { mutableStateOf<ChartPoint?>(null) }
    var index by remember { mutableIntStateOf(0) }
    Column(modifier = Modifier.statusBarsPadding()) {
        OutlinedCard(modifier = Modifier.padding(8.dp)) {
            CustomLineChartView(
                chartModel = model,
                onEntryLocked = {
                    locked = data[it]
                    index = it
                },
                showLabel = true,
                selectedIndex = index,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        Text(
            text = locked?.let { "锁定: ${it.time.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${it.value}" }
                ?: "未锁定",
            color = androidx.compose.ui.graphics.Color.Red,
            modifier = Modifier.padding(8.dp)
        )
    }
}
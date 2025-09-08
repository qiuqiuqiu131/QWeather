package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.tools.getLightColorByName
import com.qiuqiuqiu.weatherPredicate.ui.normal.AutoInfinitePageContainer
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseCard
import com.qiuqiuqiu.weatherPredicate.ui.theme.QWeatherFontFamily
import com.qiuqiuqiu.weatherPredicate.ui.theme.getQWeatherIconUnicode
import com.qweather.sdk.response.warning.Warning

/** 预警信息卡片 */
@Composable
fun WarningInfoCard(warnings: List<Warning>, onClick: (() -> Unit)? = null) {
    var composes = warnings.map { @Composable { WarningItem(it) } }

    composes = composes + @Composable { WarningItem(testWarning()) } + @Composable {
        WarningItem(testWarning())
    }

    BaseCard(onClick = onClick) {
        AutoInfinitePageContainer(
            composes,
            modifier = Modifier.padding(top = 12.dp),
            idleThresholdMillis = 3000,
            pageDurationMillis = 5000
        )
    }
}

@Composable
fun WarningItem(warning: Warning) {
    val title = "${warning.typeName}${warning.level}预警"
    val text = warning.title + ":" + warning.text.split(":", "：", limit = 2)[1]
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    null,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp,
                    text = title,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = text,
                textAlign = TextAlign.Justify,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alpha(0.6f)
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .width(90.dp)
                .fillMaxHeight()
        ) {
            Text(
                text = warning.type.getQWeatherIconUnicode(),
                fontSize = 45.sp,
                fontFamily = QWeatherFontFamily
            )
            Box(
                modifier =
                    Modifier
                        .padding(top = 8.dp)
                        .background(
                            color = warning.severityColor.getLightColorByName(),
                            shape = MaterialTheme.shapes.medium
                        )
            ) {
                Text(
                    "${warning.level}预警",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

private fun testWarning(): Warning {
    val warning = Warning()
    warning.title = "这是一个测试预警信息，用于展示预警信息的显示效果。请注意，这不是实际的天气预警。"
    warning.level = "蓝色"
    warning.type = "1006"
    warning.severityColor = "Blue"
    warning.typeName = "暴雨"
    warning.text =
        "测试预警:这是一个测试预警信息，用于展示预警信息的显示效果。请注意，这不是实际的天气预警。"
    return warning
}

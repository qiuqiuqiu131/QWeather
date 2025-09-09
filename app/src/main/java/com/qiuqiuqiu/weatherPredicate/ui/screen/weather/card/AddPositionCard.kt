package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.ui.normal.SearchBaseCard

@Preview
@Composable
fun AddPositionCard(isLoading: Boolean = false, onClick: (() -> Unit)? = null) {
    // 上下浮动动画
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val offsetY by
    infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = -8f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(300, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
        label = "iconFloat"
    )

    SearchBaseCard(
        modifier =
            Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        if (!isLoading) onClick?.invoke()
                    })
                }
                .alpha(if (isLoading) 0.75f else 1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier =
                    Modifier
                        .alpha(0.6f)
                        .padding(horizontal = 12.dp)
                        .size(30.dp)
                        .graphicsLayer { translationY = if (isLoading) offsetY else 0f }
            )
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isLoading) "正在获取位置信息..." else "添加当前位置",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = "通过访问当前位置信息，获取当前位置天气",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }
    }
}

package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.ui.normal.DefaultCard

/** 查看更多天气按钮 */
@Composable
fun MoreWeatherButton(onClick: (() -> Unit)? = null) {
    var isPressed by remember { mutableStateOf(false) }
    val animSize by
    animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec =
            spring(
                stiffness = Spring.StiffnessMedium,
                dampingRatio = Spring.DampingRatioNoBouncy
            )
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .height(40.dp)
    ) {
        DefaultCard(
            modifier =
                Modifier
                    .fillMaxSize()
                    .scale(animSize)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                try {
                                    awaitRelease()
                                } finally {
                                    isPressed = false
                                }
                            },
                            onTap = { onClick?.invoke() }
                        )
                    }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp)
                        .background(
                            color =
                                if (isPressed)
                                    MaterialTheme.colorScheme
                                        .surfaceContainer
                                else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .alpha(if (isPressed) 0.6f else 1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "查看更多天气",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .alpha(0.6f)
                )
            }
        }
    }
}
package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseCard
import com.qweather.sdk.response.indices.IndicesDaily
import kotlinx.coroutines.delay

/** 通知栏显示的天气信息 */
@Composable
fun WeatherStatusInfoCard(indicesDaily: List<IndicesDaily>) {
    val categories = indicesDaily.mapNotNull { it.text }.filter { it.isNotBlank() }
    var currentIndex by remember { mutableStateOf(0) }

    if (categories.size != 0) {
        BaseCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(35.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    targetState = currentIndex,
                    transitionSpec = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec =
                                spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                        ) togetherWith
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Up,
                                    animationSpec =
                                        spring(
                                            dampingRatio =
                                                Spring.DampingRatioNoBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                )
                    }
                ) { idx ->
                    val text = categories.getOrNull(idx) ?: ""
                    val scrollState = rememberScrollState()
                    LaunchedEffect(text) {
                        scrollState.scrollTo(0)
                        delay(2000)
                        val needScroll = scrollState.maxValue > 0
                        if (needScroll) {
                            val speedPxPerSec = 150f
                            val durationMillis =
                                ((scrollState.maxValue / speedPxPerSec) * 1000).toInt()
                            val remainMillis = 2000 - durationMillis
                            scrollState.animateScrollTo(
                                scrollState.maxValue,
                                animationSpec = tween(durationMillis = durationMillis)
                            )
                            delay(timeMillis = remainMillis.coerceAtLeast(500).toLong())
                        } else {
                            delay(3500)
                        }
                        currentIndex = (currentIndex + 1) % categories.size
                    }

                    val infiniteTransition = rememberInfiniteTransition(label = "shake")
                    val rotation by
                    infiniteTransition.animateFloat(
                        initialValue = -18f,
                        targetValue = 18f,
                        animationSpec =
                            infiniteRepeatable(
                                animation =
                                    tween(
                                        durationMillis = 500,
                                        easing = LinearEasing
                                    ),
                                repeatMode = RepeatMode.Reverse
                            ),
                        label = "shakeAnim"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier
                                .graphicsLayer { this.rotationZ = rotation }
                                .size(20.dp)
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(
                                        scrollState,
                                        enabled = false
                                    ) // 禁止用户滑动
                                    .padding(end = 8.dp)
                        ) {
                            Text(
                                text = text,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


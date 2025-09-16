package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlin.random.Random

enum class RainType {
    DRIZZLE, // 细雨
    LIGHT,   // 小雨
    MODERATE,// 中雨
    HEAVY,   // 大雨
    STORM,   // 暴雨
    SEVERE,  // 大暴雨
    EXTREME  // 特大暴雨
}

fun getRaindropCount(type: RainType): Int = when (type) {
    RainType.DRIZZLE -> 5
    RainType.LIGHT -> 8
    RainType.MODERATE -> 16
    RainType.HEAVY -> 24
    RainType.STORM -> 32
    RainType.SEVERE -> 40
    RainType.EXTREME -> 48
}

data class Raindrop(
    var x: Float,
    var y: Float,
    val length: Float,
    val speed: Float,
    val alpha: Float
)

@Preview
@Composable
fun RainyWindowBackground(
    modifier: Modifier = Modifier,
    type: RainType = RainType.MODERATE,
    isDay: Boolean = true
) {
    val raindropCount = getRaindropCount(type)
    val raindrops = remember {
        List(raindropCount) {
            Raindrop(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                length = Random.nextFloat() * 40f + 30f,
                speed = Random.nextFloat() * 6f + 3f,
                alpha = Random.nextFloat() * 0.5f + 0.3f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rainAnimation")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainProgress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            drawRect(
                brush = Brush.verticalGradient(
                    colors = if (isDay) listOf(Color(0xFF2E4459), Color(0xFF597596))
                    else listOf(Color(0xFF181D23), Color(0xFF384452))
                ),
                size = size
            )

            raindrops.forEach { drop ->
                val newY = (drop.y * height + drop.speed * progress * height) % height
                val start = Offset(drop.x * width, newY)
                val end = Offset(drop.x * width, newY + drop.length)

                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,         // 终点
                            Color(0xFFAAAAAA).copy(alpha = drop.alpha) // 起点
                        ),
                        startY = start.y,
                        endY = end.y
                    ),
                    start = start,
                    end = end,
                    strokeWidth = 3.5f
                )
            }
        }
    }
}
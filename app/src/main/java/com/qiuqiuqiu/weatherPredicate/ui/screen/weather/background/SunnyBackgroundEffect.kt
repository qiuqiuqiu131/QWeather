package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.qiuqiuqiu.weatherPredicate.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@SuppressLint("ConfigurationScreenWidthHeight")
@Preview
@Composable
fun SunnyLensFlareBackground(modifier: Modifier = Modifier, isCloudy: Boolean = true) {
    // 创建无限动画过渡
    val infiniteTransition = rememberInfiniteTransition(label = "lensFlareAnimation")

    // 控制光晕角度偏转的动画值（0f 到 1f，代表角度变化）
    val angleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 19000 // 总共8秒完成一个循环
                0f at 0 with LinearOutSlowInEasing // 开始位置
                0.3f at 2500 with LinearOutSlowInEasing // 2秒时偏转20%
                0.3f at 9500 with LinearOutSlowInEasing // 停顿1秒（保持20%）
                0f at 12000 with LinearOutSlowInEasing // 回到初始位置
                0f at 19000 with LinearOutSlowInEasing // 停顿2秒（保持初始位置）
            },
            repeatMode = RepeatMode.Restart
        ), label = "angleAnimation"
    )


    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxDimension = size.maxDimension

            // 根据动画进度计算光晕方向的变化
            // 从原始方向偏转最多30度，然后返回
            val baseDirection = Offset(size.width * 0.5f, size.height * 0.6f)
            val animatedDirection = with(angleProgress) {
                // 计算偏转角度（-15度到+15度之间变化）
                val angle = (this * 30f - 15f) * (3.14159f / 180f)
                val cos = cos(angle)
                val sin = sin(angle)
                Offset(
                    x = baseDirection.x * cos - baseDirection.y * sin,
                    y = baseDirection.x * sin + baseDirection.y * cos
                )
            }

            // 1. 绘制渐变蓝色天空背景
            drawRect(
                brush = Brush.verticalGradient(
                    // colors = listOf(Color(0xFF60A0FF), Color(0xFFB1C1DE))
                    colors = listOf(Color(0xFF2586FF), Color(0xFF6EB1E8))
                ),
                size = size
            )

            // 2. 在左上角创建太阳位置
            val sunCenter = Offset(size.width * 0.025f, size.height * 0f)

            // 3. 绘制太阳的大范围泛光（最外层的柔和光晕）
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Yellow.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = sunCenter,
                    radius = maxDimension * 0.4f
                ),
                radius = maxDimension * 0.4f,
                center = sunCenter,
                blendMode = BlendMode.Screen
            )

            // 4. 绘制中层的太阳光晕
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    center = sunCenter,
                    radius = maxDimension * 0.25f
                ),
                radius = maxDimension * 0.25f,
                center = sunCenter,
                blendMode = BlendMode.Screen
            )

            // 5. 绘制太阳核心（明亮的部分）
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        Color.White.copy(alpha = 0.7f),
                        Color.White.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = sunCenter,
                    radius = maxDimension * 0.1f
                ),
                radius = maxDimension * 0.1f,
                center = sunCenter,
                blendMode = BlendMode.Screen
            )

            if (!isCloudy) {
                // 7. 绘制斜向左下的长光晕（镜头光晕效果）
                val flareDirection = animatedDirection

                // 7.1 先绘制大的背景光晕沿着光晕路径
                val largeFlarePoints = listOf(0.3f, 0.6f)
                largeFlarePoints.forEach { progress ->
                    val flarePoint = sunCenter + flareDirection * progress
                    val flareSize = maxDimension * (0.08f + 0.04f * (1 - progress)) // 更大的光晕

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            center = flarePoint,
                            radius = flareSize * 1.5f
                        ),
                        radius = flareSize * 1.5f,
                        center = flarePoint,
                        blendMode = BlendMode.Screen
                    )
                }

                // 7.2 然后绘制中等大小的光晕
                val mediumFlarePoints = listOf(0.2f, 0.5f, 0.8f)
                mediumFlarePoints.forEach { progress ->
                    val flarePoint = sunCenter + flareDirection * progress
                    val flareSize = maxDimension * (0.05f + 0.03f * (1 - progress))

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.Yellow.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            center = flarePoint,
                            radius = flareSize
                        ),
                        radius = flareSize,
                        center = flarePoint,
                        blendMode = BlendMode.Screen
                    )
                }

                // 7.3 最后绘制原来的小光晕
                val smallFlarePoints = listOf(0.15f, 0.35f, 0.55f, 0.75f, 1f)
                smallFlarePoints.forEach { progress ->
                    val flarePoint = sunCenter + flareDirection * progress
                    val flareSize = maxDimension * (0.02f + 0.01f * (1 - progress))

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.4f),
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = flarePoint,
                            radius = flareSize
                        ),
                        radius = flareSize,
                        center = flarePoint,
                        blendMode = BlendMode.Screen
                    )
                }
            }


            // 8. 添加一些随机的大光晕在画面其他位置，增强整体效果
            val randomLargeFlares = listOf(
                Offset(size.width * 0.7f, size.height * 0.2f) to maxDimension * 0.12f,
                Offset(size.width * 0.8f, size.height * 0.6f) to maxDimension * 0.15f,
                Offset(size.width * 0.3f, size.height * 0.7f) to maxDimension * 0.1f
            )

            randomLargeFlares.forEach { (position, size) ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.Yellow.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = position,
                        radius = size
                    ),
                    radius = size,
                    center = position,
                    blendMode = BlendMode.Screen
                )
            }

        }
    }

    if (isCloudy) {
        val groupCount = 3
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.toFloat() // dp 单位的 float

        val infinite2Transition = rememberInfiniteTransition(label = "cloudStackFloat")
        val floatProgress by infinite2Transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(60000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "cloudStackFloatProgress"
        )

        val cloudSmallLayers = remember {
            List(groupCount) { i ->
                listOf(
                    CloudLayer(
                        resId = R.drawable.cloud3,
                        baseY = 0.1f + Random.nextFloat() * 0.2f,
                        sizeRatio = 2.8f,
                        alpha = 0.7f,
                        startX = (i - 1) * (screenWidth),
                        speed = 1.1f
                    ),
                )
            }.flatten()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {
            cloudSmallLayers.forEachIndexed { idx, layer ->
                // 计算云层横向偏移，让云层从右侧屏幕外进入，左侧屏幕外消失
                val totalDistance = screenWidth * 3 // 左右各留一个云宽
                val offsetX =
                    ((layer.startX - floatProgress * totalDistance) % totalDistance) + screenWidth
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = (layer.baseY * 400).dp)
                ) {
                    Image(
                        painter = painterResource(id = layer.resId),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(x = offsetX.dp, y = 0.dp)
                            .graphicsLayer(
                                scaleX = layer.sizeRatio,
                                scaleY = layer.sizeRatio,
                                transformOrigin = TransformOrigin(0.5f, 0.5f)
                            )
                            .alpha(layer.alpha),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

}
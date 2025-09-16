package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
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

data class CloudLayer(
    val resId: Int,
    val baseY: Float,
    val startX: Float,
    val sizeRatio: Float,
    val alpha: Float,
    val speed: Float
)

@SuppressLint("ConfigurationScreenWidthHeight")
@Preview
@Composable
fun CloudyAnimationBackground(modifier: Modifier = Modifier) {
    // cloud2底层大云
    val bgCloudsLayer = remember {
        listOf(
            CloudLayer(
                resId = R.drawable.cloud2,
                baseY = 0.4f,
                sizeRatio = 3.5f,
                alpha = 0.2f,
                startX = 0f,
                speed = 0f
            ),
            CloudLayer(
                resId = R.drawable.cloud2,
                baseY = 1f,
                sizeRatio = 3f,
                alpha = 0.2f,
                startX = -1.5f,
                speed = 0f
            ),
            CloudLayer(
                resId = R.drawable.cloud2,
                baseY = 1.8f,
                sizeRatio = 3f,
                alpha = 0.2f,
                startX = 1.5f,
                speed = 0f
            )
        )
    }

    val groupCount = 3
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat() // dp 单位的 float

    val infiniteTransition = rememberInfiniteTransition(label = "cloudStackFloat")
    val floatProgress by infiniteTransition.animateFloat(
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
                    resId = R.drawable.cloud1,
                    baseY = 0.05f,
                    sizeRatio = 2f,
                    alpha = 0.4f,
                    startX = (i - 1) * (screenWidth),
                    speed = 1.2f
                ),
                CloudLayer(
                    resId = R.drawable.cloud3,
                    baseY = 0.3f,
                    sizeRatio = 2.5f,
                    alpha = 0.4f,
                    startX = (i - 1) * (screenWidth) - 180f,
                    speed = 1.1f
                )
            )
        }.flatten()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF546F88), Color(0xFF839AB4))
                ),
                size = size
            )
        }


        // 先画底层大云
        bgCloudsLayer.forEach { cloud2Layer ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = (cloud2Layer.baseY * 100).dp)
            ) {
                Image(
                    painter = painterResource(id = cloud2Layer.resId),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .graphicsLayer(
                            scaleX = cloud2Layer.sizeRatio,
                            scaleY = cloud2Layer.sizeRatio,
                            transformOrigin = TransformOrigin(0.5f, 0.5f)
                        )
                        .alpha(cloud2Layer.alpha)
                        .offset(
                            x = (((floatProgress - 0.5f) * cloud2Layer.speed + cloud2Layer.startX) * 60f).dp,
                            y = 0.dp
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }

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


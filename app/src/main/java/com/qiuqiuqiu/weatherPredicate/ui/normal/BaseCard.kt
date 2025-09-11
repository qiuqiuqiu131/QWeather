package com.qiuqiuqiu.weatherPredicate.ui.normal

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun BaseItem(
    modifier: Modifier = Modifier,
    bgColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    innerModifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable (BoxScope.() -> Unit)
) {
    var isPressed by remember { mutableStateOf(false) }
    Box(
        modifier =
            modifier
                .background(
                    color = if (isPressed) bgColor.copy(alpha = 0.6f) else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
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
    ) { Box(innerModifier.alpha(if (isPressed) 0.6f else 1f)) { content() } }
}

@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    onClick: (() -> Unit)? = null,
    endCorner: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit)
) {
    DefaultCard(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
        BaseItem(onClick = onClick) {
            Column(modifier = modifier) {
                if (title != null || endCorner != null) {
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .padding(bottom = 4.dp)
                            .fillMaxWidth()
                    ) {
                        title?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                modifier =
                                    Modifier
                                        .alpha(0.6f)
                                        .padding(horizontal = 12.dp)
                                        .align(Alignment.BottomStart)
                            )
                        }

                        endCorner?.let {
                            Box(
                                modifier =
                                    Modifier
                                        .padding(horizontal = 12.dp)
                                        .alpha(0.6f)
                                        .align(Alignment.BottomEnd)
                            ) { it() }
                        }
                    }
                }
                content()
            }
        }
    }
}

@Composable
fun SearchBaseCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    endCorner: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit)
) {
    DefaultCard(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
        Column(modifier = modifier) {
            if (title != null || endCorner != null) {
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .padding(bottom = 4.dp)
                        .fillMaxWidth()
                ) {
                    title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleSmall,
                            modifier =
                                Modifier
                                    .padding(horizontal = 12.dp)
                                    .align(Alignment.BottomStart)
                        )
                    }

                    endCorner?.let {
                        Box(
                            modifier =
                                Modifier
                                    .padding(horizontal = 12.dp)
                                    .alpha(0.6f)
                                    .align(Alignment.BottomEnd)
                        ) { it() }
                    }
                }
            }
            content()
        }
    }
}

@Composable
fun DefaultCard(modifier: Modifier = Modifier, content: @Composable (ColumnScope.() -> Unit)) {
    val color1 = CardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = color1
    ) {
        content()
    }
}
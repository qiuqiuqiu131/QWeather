package com.qiuqiuqiu.weatherPredicate.ui.normal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadingContainer(
    isInit: Boolean,
    color: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    if (isInit) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                color = color,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center)
            )
        }
    } else {
        content()
    }
}
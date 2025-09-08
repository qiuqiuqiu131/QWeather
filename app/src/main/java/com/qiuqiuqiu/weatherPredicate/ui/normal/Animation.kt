package com.qiuqiuqiu.weatherPredicate.ui.normal

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberScrollAlpha(
    scrollState: ScrollState,
    startPx: Int = 100,
    endPx: Int = 200,
    reverse: Boolean = false
): State<Float> {
    return remember {
        derivedStateOf {
            val progress = when {
                scrollState.value < startPx -> 0f
                scrollState.value > endPx -> 1f
                else -> (scrollState.value - startPx).toFloat() / (endPx - startPx)
            }
            if (reverse) 1f - progress else progress
        }
    }
}

@Composable
fun rememberScrollThreshold(
    scrollState: ScrollState,
    threshold: Int
): State<Boolean> {
    return remember {
        derivedStateOf {
            when {
                scrollState.value <= threshold -> false
                else -> true
            }
        }
    }
}
package com.qiuqiuqiu.weatherPredicate.ui.normal

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CustomDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSecondary
) {
    HorizontalDivider(
        modifier = modifier,
        color = color.copy(alpha = 0.7f)
    )
}
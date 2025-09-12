package com.qiuqiuqiu.weatherPredicate.ui.normal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconList(
    itemNumber: Int, activeNumber: Int,
    icon: @Composable (active: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 26.dp
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(itemNumber) {
            Box(modifier = Modifier.size(iconSize), contentAlignment = Alignment.Center) {
                icon(it < activeNumber)
            }
        }
    }
}

@Preview
@Composable
fun IconListTest() {
    IconList(itemNumber = 5, activeNumber = 3, icon = {
        val size = if (it) 24.dp else 22.dp
        val tint = if (it) Color.Yellow else Color.Gray
        Icon(Icons.Rounded.Star, null, tint = tint, modifier = Modifier.size(size))
    })
}
package com.qiuqiuqiu.weatherPredicate.ui.normal

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@SuppressLint("FrequentlyChangingValue")
@Composable
fun ScrollableCenterRowList(
    modifier: Modifier = Modifier,
    itemCount: Int,
    itemIndex: Int,
    selectedItemChanged: (index: Int) -> Unit,
    content: @Composable (index: Int, isSelected: Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var triedScrollToItem by remember { mutableStateOf(false) }

    LaunchedEffect(itemIndex, listState.layoutInfo.visibleItemsInfo) {
        if (itemCount > 0 && listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
            val itemInfo = listState.layoutInfo.visibleItemsInfo.find { it.index == itemIndex }
            if (itemInfo != null) {
                val viewportStart = listState.layoutInfo.viewportStartOffset
                val viewportEnd = listState.layoutInfo.viewportEndOffset
                val viewportCenter = (viewportStart + viewportEnd) / 2
                val itemCenter = (itemInfo.offset + itemInfo.offset + itemInfo.size) / 2
                val diff = itemCenter - viewportCenter
                coroutineScope.launch { listState.animateScrollBy(diff.toFloat()) }
                triedScrollToItem = false // 重置
            } else if (!triedScrollToItem) {
                triedScrollToItem = true
                coroutineScope.launch { listState.animateScrollToItem(itemIndex) }
            }
        }
    }

    LazyRow(state = listState, modifier = modifier) {
        items(itemCount) { index ->
            Box(
                Modifier
                    .padding(horizontal = 8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { selectedItemChanged(index) }
                    },
                contentAlignment = Alignment.Center
            ) { content(index, index == itemIndex) }
        }
    }
}

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
import androidx.compose.runtime.rememberCoroutineScope
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

    LaunchedEffect(itemIndex) {
        if (itemCount > 0 && listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
            val itemInfo = listState.layoutInfo.visibleItemsInfo.find { it.index == itemIndex }
            if (itemInfo != null) {
                // 已经可见，微调到中心
                val viewportStart = listState.layoutInfo.viewportStartOffset
                val viewportEnd = listState.layoutInfo.viewportEndOffset
                val viewportCenter = (viewportStart + viewportEnd) / 2
                val itemCenter = (itemInfo.offset + itemInfo.offset + itemInfo.size) / 2
                val diff = itemCenter - viewportCenter
                coroutineScope.launch { listState.animateScrollBy(diff.toFloat()) }
            } else {
                // 不可见，先滚动到item，再微调到中心
                coroutineScope.launch {
                    listState.animateScrollToItem(itemIndex)
                    // 等待布局刷新
                    kotlinx.coroutines.yield()
                    val newItemInfo =
                        listState.layoutInfo.visibleItemsInfo.find { it.index == itemIndex }
                    if (newItemInfo != null) {
                        val viewportStart = listState.layoutInfo.viewportStartOffset
                        val viewportEnd = listState.layoutInfo.viewportEndOffset
                        val viewportCenter = (viewportStart + viewportEnd) / 2
                        val itemCenter =
                            (newItemInfo.offset + newItemInfo.offset + newItemInfo.size) / 2
                        val diff = itemCenter - viewportCenter
                        listState.animateScrollBy(diff.toFloat())
                    }
                }
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

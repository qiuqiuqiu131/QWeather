package com.qiuqiuqiu.weatherPredicate.ui.normal

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

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

    // 自动滚动选中项到中间（考虑不同宽度）
    LaunchedEffect(itemIndex) {
        if (itemCount > 0 && listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
            val itemInfo = listState.layoutInfo.visibleItemsInfo.find { it.index == itemIndex }
            if (itemInfo != null) {
                val viewportStart = listState.layoutInfo.viewportStartOffset
                val viewportEnd = listState.layoutInfo.viewportEndOffset
                val viewportCenter = (viewportStart + viewportEnd) / 2
                val itemCenter = (itemInfo.offset + itemInfo.offset + itemInfo.size) / 2
                val diff = itemCenter - viewportCenter
                // 计算需要滚动的像素距离
                coroutineScope.launch { listState.animateScrollBy(diff.toFloat()) }
            } else {
                // 如果目标item不在可见区域，先滚动到它，再触发一次LaunchedEffect
                listState.animateScrollToItem(itemIndex)
            }
        }
    }

    LazyRow(state = listState, modifier = modifier) {
        items(itemCount) { index ->
            Box(
                Modifier
                    .padding(horizontal = 8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            selectedItemChanged(index)
                        }
                    }
            ) { content(index, index == itemIndex) }
        }
    }
}

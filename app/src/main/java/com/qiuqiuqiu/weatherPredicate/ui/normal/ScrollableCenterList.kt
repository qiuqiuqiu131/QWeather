package com.qiuqiuqiu.weatherPredicate.ui.normal

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class NullNestScrollConnection : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return available
    }
}

@SuppressLint("FrequentlyChangingValue")
@Composable
fun ScrollableCenterRowList(
    modifier: Modifier = Modifier,
    itemCount: Int,
    itemIndex: Int,
    selectedItemChanged: (index: Int) -> Unit,
    canScroll: Boolean = true,
    content: @Composable (index: Int, isSelected: Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val nestScrollConnection = remember { NullNestScrollConnection() }

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


    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.nestedScroll(nestScrollConnection),
        userScrollEnabled = canScroll
    ) {
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

@Composable
fun ScrollableCenterNormalRowList(
    modifier: Modifier = Modifier,
    itemCount: Int,
    itemIndex: Int,
    selectedItemChanged: (index: Int) -> Unit,
    content: @Composable (index: Int, isSelected: Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val itemOffsets = remember { mutableStateListOf<Int>() }
    val itemWidths = remember { mutableStateListOf<Int>() }

    // 记录每个 item 的宽度和偏移
    Row(
        modifier = modifier
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(itemCount) { index ->
            Box(
                Modifier
                    .padding(horizontal = 8.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        val x = layoutCoordinates.positionInParent().x.toInt()
                        val w = layoutCoordinates.size.width
                        if (itemOffsets.size <= index) itemOffsets.add(x) else itemOffsets[index] =
                            x
                        if (itemWidths.size <= index) itemWidths.add(w) else itemWidths[index] = w
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { selectedItemChanged(index) }
                    },
                contentAlignment = Alignment.Center
            ) { content(index, index == itemIndex) }
        }
    }

    // itemIndex变化时自动滚动居中
    LaunchedEffect(itemIndex, itemOffsets, itemWidths) {
        if (itemIndex in itemOffsets.indices && itemIndex in itemWidths.indices) {
            val itemStart = itemOffsets[itemIndex]
            val itemWidth = itemWidths[itemIndex]
            val itemCenter = itemStart + itemWidth / 2
            val viewportCenter = scrollState.viewportSize / 2
            val scrollTo = (itemCenter - viewportCenter).coerceAtLeast(0)
            coroutineScope.launch {
                scrollState.animateScrollTo(scrollTo, spring(stiffness = Spring.StiffnessLow))
            }
        }
    }
}
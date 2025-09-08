package com.qiuqiuqiu.weatherPredicate.ui.normal

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@Composable
fun InfinitePageContainer(pages: List<@Composable () -> Unit>, modifier: Modifier = Modifier) {
    val pageCount = pages.size
    val virtualPageCount = pageCount * 10000
    val startIndex = virtualPageCount / 2
    val pagerState = rememberPagerState(initialPage = startIndex) { virtualPageCount }
    val coroutineScope = rememberCoroutineScope()

    // 保证滑动到边界时自动跳回中间，避免滑到头
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage < pageCount ||
            pagerState.currentPage > virtualPageCount - pageCount
        ) {
            coroutineScope.launch {
                pagerState.scrollToPage(startIndex + (pagerState.currentPage % pageCount))
            }
        }
    }

    val realIndex = pagerState.currentPage % pageCount

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            val index = page % pageCount
            pages[index]()
        }
        PagerIndicator(size = pageCount, index = realIndex, modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun AutoInfinitePageContainer(
    pages: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
    idleThresholdMillis: Long = 2000, // 未拖拽多久后可自动翻页
    pageDurationMillis: Long = 3000 // 页面保留时长
) {
    val pageCount = pages.size
    val virtualPageCount = pageCount * 10000
    val startIndex = virtualPageCount / 2
    val pagerState = rememberPagerState(initialPage = startIndex) { virtualPageCount }
    val coroutineScope = rememberCoroutineScope()
    var userScrollEnabled by remember { mutableStateOf(true) }

    // 保证滑动到边界时自动跳回中间，避免滑到头
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage < pageCount ||
            pagerState.currentPage > virtualPageCount - pageCount
        ) {
            coroutineScope.launch {
                pagerState.scrollToPage(startIndex + (pagerState.currentPage % pageCount))
            }
        }
    }

    // 自动轮播逻辑
    LaunchedEffect(Unit) {
        var lastUserScrollTime = System.currentTimeMillis()
        // 监听用户手动滑动
        launch {
            snapshotFlow { pagerState.isScrollInProgress }.filter { !it }.collectLatest {
                lastUserScrollTime = System.currentTimeMillis()
            }
        }

        while (true) {
            val now = System.currentTimeMillis()
            val idleTime = now - lastUserScrollTime
            if (idleTime > idleThresholdMillis && !pagerState.isScrollInProgress) {
                delay(pageDurationMillis)
                if (pagerState.isScrollInProgress) continue
                userScrollEnabled = false
                pagerState.animateScrollToPage(
                    pagerState.currentPage + 1,
                    animationSpec =
                        spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioNoBouncy
                        )
                )
                userScrollEnabled = true
                lastUserScrollTime = System.currentTimeMillis()
            } else {
                delay(500)
            }
        }
    }

    val realIndex = pagerState.currentPage % pageCount

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = userScrollEnabled
        ) { page ->
            val index = page % pageCount
            pages[index]()
        }
        PagerIndicator(
            size = pageCount,
            index = realIndex,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun PagerIndicator(
    size: Int,
    index: Int,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    unselectedColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
    dotSize: Dp = 3.dp,
    selectedDotSize: Dp = 4.dp,
    spacing: Dp = 2.dp
) {
    Row(
        modifier = modifier.height(selectedDotSize),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(size) { i ->
            val animatedSize by
            animateDpAsState(
                targetValue = if (i == index) selectedDotSize else dotSize,
                animationSpec =
                    spring(
                        stiffness = Spring.StiffnessMedium,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
            )
            Box(
                modifier =
                    Modifier
                        .padding(horizontal = spacing)
                        .size(animatedSize)
                        .clip(MaterialTheme.shapes.medium)
                        .background(if (i == index) selectedColor else unselectedColor)
            )
        }
    }
}

@file:OptIn(ExperimentalLayoutApi::class)

package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qiuqiuqiu.weatherPredicate.model.weather.SearchHistory
import com.qiuqiuqiu.weatherPredicate.ui.normal.DefaultCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.SearchBaseCard

@Composable
fun SearchHistoryCard(
    searchHistory: List<SearchHistory>,
    onClick: ((SearchHistory) -> Unit)? = null,
    onClearClick: (() -> Unit)? = null
) {
    SearchBaseCard(title = "搜索历史", endCorner = {
        if (!searchHistory.isEmpty()) {
            Text(
                text = "清空",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onClearClick?.invoke() })
                    })
        }
    }) {
        if (!searchHistory.isEmpty()) {
            FlowRow(
                maxLines = 3,
                modifier = Modifier.padding(bottom = 8.dp, start = 12.dp, end = 12.dp)
            ) {
                searchHistory.forEach {
                    SearchHistoryItem(it, onClick)
                }
            }
        } else {
            Text(
                text = "暂无搜索记录",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .alpha(0.6f)
                    .padding(bottom = 14.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun SearchHistoryItem(
    history: SearchHistory,
    onClick: ((SearchHistory) -> Unit)? = null
) {
    DefaultCard(
        bgColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .widthIn(max = 120.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick?.invoke(history) })
            }
    ) {
        Text(
            text = history.name,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.5.sp),
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}
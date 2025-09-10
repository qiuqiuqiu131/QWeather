package com.qiuqiuqiu.weatherPredicate.ui.screen.time

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.core.text.HtmlCompat
import com.qiuqiuqiu.weatherPredicate.viewModel.TourSearchType
import com.qiuqiuqiu.weatherPredicate.viewModel.TourViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourScreen(
    onBack: () -> Unit,
    viewModel: TourViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    var searchMode by remember { mutableStateOf(TourSearchType.NAME) }
    val spots by viewModel.spots.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 清理 HTML 标签、控制字符，并首行缩进两个全角空格
    fun cleanAndIndent(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        val withBrNormalized = raw.replace(Regex("(?i)<br\\s*/?>"), "<br/>")
        val spanned = HtmlCompat.fromHtml(withBrNormalized, HtmlCompat.FROM_HTML_MODE_LEGACY)
        var text = spanned.toString()
        text = text.replace(Regex("[\\u0000-\\u0008\\u000B\\u000C\\u000E-\\u001F]"), "")
        text = text.replace(Regex("[\\u00A0\\u1680\\u180E\\u2000-\\u200A\\u202F\\u205F\\u3000]+"), " ")
            .replace(Regex("[ \\t]+"), " ")
            .replace(Regex(" *\\n+ *"), "\n")
            .trim()
        val paragraphs = text.split("\n").map { it.trim() }
        return paragraphs.joinToString("\n") { if (it.isEmpty()) it else "　　$it" }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("景点查询", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 搜索模式选择 - 使用Segmented Button样式
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    TourSearchType.NAME to "景点名称",
                    TourSearchType.PROVINCE to "省份",
                    TourSearchType.CITY to "城市"
                ).forEach { (modeEnum, label) ->
                    val isSelected = searchMode == modeEnum
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            searchMode = modeEnum
                            query = ""
                        },
                        label = { Text(label) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                    if (modeEnum != TourSearchType.CITY) Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 输入框 + 搜索按钮（限制中文输入）
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { input ->
                        query = input.filter { it in '\u4E00'..'\u9FFF' }
                    },
                    label = {
                        Text(
                            when (searchMode) {
                                TourSearchType.NAME -> "请输入景点名称"
                                TourSearchType.PROVINCE -> "请输入省份"
                                TourSearchType.CITY -> "请输入城市"
                            }
                        )
                    },
                    placeholder = {
                        Text(
                            when (searchMode) {
                                TourSearchType.NAME -> "例如：长城、黄山"
                                TourSearchType.PROVINCE -> "例如：广东、四川"
                                TourSearchType.CITY -> "例如：深圳、成都"
                            }
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "清除")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.fetchTourSpotsByType(searchMode, query)
                        coroutineScope.launch { listState.scrollToItem(0) }
                    },
                    modifier = Modifier
                        .height(56.dp) // 与输入框高度匹配
                        .padding(top = 8.dp) // 微调对齐
                ) {
                    Icon(Icons.Default.Search, contentDescription = "搜索", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("搜索")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 错误或加载提示
            if (loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Text(
                    error ?: "发生错误",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 景点列表
            if (spots.isEmpty() && !loading && error == null && query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "未找到相关景点",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(spots) { spot ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cleanAndIndent(spot.name).trim(), // 去除首尾空白
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // 内容左对齐、首行缩进
                                Text(
                                    text = cleanAndIndent(spot.content),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // 省份 + 城市右下角
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = cleanAndIndent("${spot.province} · ${spot.city}"),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 分页按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        viewModel.loadPreviousPageUsingCurrent(10)
                        coroutineScope.launch { listState.scrollToItem(0) }
                    },
                    enabled = viewModel.currentPageNumber() > 1,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) { Text("上一页") }

                Text(
                    "第 ${viewModel.currentPageNumber()} 页",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Button(
                    onClick = {
                        viewModel.loadNextPageUsingCurrent(10)
                        coroutineScope.launch { listState.scrollToItem(0) }
                    },
                    enabled = spots.size == 10,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) { Text("下一页") }
            }
        }
    }
}
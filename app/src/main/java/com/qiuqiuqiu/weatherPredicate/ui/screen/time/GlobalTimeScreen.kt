package com.qiuqiuqiu.weatherPredicate.ui.screen.time

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qiuqiuqiu.weatherPredicate.viewModel.CitiesViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GlobalTimeScreen(
    onBack: () -> Unit,
    viewModel: CitiesViewModel = hiltViewModel()
) {
    var cityInput by remember { mutableStateOf("") }
    var searchHistory by remember { mutableStateOf(listOf<String>()) }
    val cityResult by viewModel.cityResult.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 热门城市列表
    val popularCities = listOf(
        "London", "New York", "Tokyo", "Paris",
        "Sydney", "Beijing", "Dubai", "Moscow"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("全球时间查询") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 搜索区域
            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                label = { Text("输入城市名称 (中文或英文)") },
                trailingIcon = {
                    if (cityInput.isNotBlank()) {
                        IconButton(
                            onClick = {
                                if (cityInput.isNotBlank()) {
                                    viewModel.fetchCityTime(cityInput.trim())
                                    // 添加到搜索历史
                                    searchHistory = listOf(cityInput.trim()) + searchHistory.take(4)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "搜索")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 热门城市快捷选择
            Text("热门城市", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                popularCities.forEach { city ->
                    FilterChip(
                        selected = cityInput == city,
                        onClick = {
                            cityInput = city
                            viewModel.fetchCityTime(city)
                            // 添加到搜索历史
                            searchHistory = listOf(city) + searchHistory.take(4)
                        },
                        label = { Text(city) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 搜索结果展示
            when {
                loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "查询失败: ${error ?: "未知错误"}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchCityTime(cityInput.trim()) }) {
                            Text("重试")
                        }
                    }
                }
                cityResult?.result != null -> {
                    val result = cityResult!!.result!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                result.city,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("国家: ${result.country}")
                            Text("时区: ${result.timeZone}")
                            Text("星期: ${result.week}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                result.strtime,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                else -> {
                    // 显示搜索历史或提示
                    if (searchHistory.isNotEmpty()) {
                        Text("最近搜索", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn {
                            items(searchHistory) { historyItem ->
                                ListItem(
                                    headlineContent = { Text(historyItem) },
                                    supportingContent = { Text("点击重新查询") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            cityInput = historyItem
                                            viewModel.fetchCityTime(historyItem)
                                        }
                                )
                                Divider()
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "输入城市名称查询时间\n(例如: Beijing, London, New York)",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.qiuqiuqiu.weatherPredicate.ui.screen.time

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qiuqiuqiu.weatherPredicate.R
import com.qiuqiuqiu.weatherPredicate.viewModel.JieQiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarTermScreen(
    onBack: () -> Unit,
    viewModel: JieQiViewModel = hiltViewModel()
) {
    var year by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedTerm by remember { mutableStateOf("立春") }

    val jieQiResult by viewModel.jieQiResult.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val solarTerms = listOf(
        "立春", "雨水", "惊蛰", "春分", "清明", "谷雨",
        "立夏", "小满", "芒种", "夏至", "小暑", "大暑",
        "立秋", "处暑", "白露", "秋分", "寒露", "霜降",
        "立冬", "小雪", "大雪", "冬至", "小寒", "大寒"
    )

    // 本地图片映射
    val solarTermImageMap = mapOf(
        "白露" to R.drawable.bailu,
        // 其他节气图片映射...
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("二十四节气查询", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 输入年份
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("输入年份", textAlign = TextAlign.Center) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth(0.7f), // 占屏幕宽度70%
                        trailingIcon = {
                            if (year.isNotEmpty()) {
                                IconButton(onClick = { year = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "清除")
                                }
                            }
                        }
                    )
                }
            }

            // 选择节气
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth(0.7f) // 占屏幕宽度70%
                    ) {
                        OutlinedTextField(
                            value = selectedTerm,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("选择节气", textAlign = TextAlign.Center) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            solarTerms.forEach { term ->
                                DropdownMenuItem(
                                    text = { Text(term, textAlign = TextAlign.Center) },
                                    onClick = {
                                        selectedTerm = term
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 查询按钮
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { if (year.isNotBlank()) viewModel.fetchJieQi(selectedTerm, year) },
                        modifier = Modifier
                            .fillMaxWidth(0.5f) // 占屏幕宽度50%，可调整
                            .height(48.dp), // 高度可调
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "查询", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "查询节气信息", fontSize = 16.sp)
                    }
                }
            }

            // 加载状态
            if (loading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // 错误信息
            error?.let {
                item {
                    Text(
                        text = "错误: $it",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 显示结果
            jieQiResult?.let { res ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 节气名称
                            Text(
                                text = res.result.name,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                letterSpacing = 1.sp ,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // 节气本地图片
                            solarTermImageMap[res.result.name]?.let { imageRes ->
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = res.result.name,
                                    modifier = Modifier
                                        .size(250.dp) // 可调整大小
                                        .clip(CircleShape)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )

                            // 日期信息
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally // 添加这个使内容居中
                            ) {
                                Text("公历: ${res.result.date.gregdate}")
                                Text("农历: ${res.result.date.lunardate}")
                            }

                            // 节气介绍
                            Text(
                                text = "【节气介绍】",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "        ${res.result.jieshao}")

                            // 诗句
                            Text(
                                text = "【诗句】",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "        ${res.result.shiju}")

                            // 饮食
                            Text(
                                text = "【饮食】",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "        ${res.result.meishi}")

                            // 宜忌
                            Text(
                                text = "【宜忌】",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "        ${res.result.yiji}")
                        }
                    }
                }
            }
        }
    }
}
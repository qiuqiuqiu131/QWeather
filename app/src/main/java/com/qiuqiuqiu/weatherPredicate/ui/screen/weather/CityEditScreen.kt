package com.qiuqiuqiu.weatherPredicate.ui.screen.weather

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.qiuqiuqiu.weatherPredicate.LocalAppViewModel
import com.qiuqiuqiu.weatherPredicate.model.CityType
import com.qiuqiuqiu.weatherPredicate.model.LocationWeatherModel
import com.qiuqiuqiu.weatherPredicate.ui.normal.DefaultCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.LoadingContainer
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background.WeatherBackgroundCard
import com.qiuqiuqiu.weatherPredicate.viewModel.AppViewModel
import com.qiuqiuqiu.weatherPredicate.viewModel.CityEditViewModel
import java.time.LocalDateTime

@Composable
fun CityEditScreen(navController: NavController) {
    val appViewModel: AppViewModel = LocalAppViewModel.current
    val viewModel: CityEditViewModel = hiltViewModel()
    viewModel.refreshCities()

    Scaffold(
        topBar = {
            CityEditTopBar(
                cancelClick = { navController.popBackStack() },
                saveClick = {
                    viewModel.saveEdit(appViewModel) {
                        navController.popBackStack()
                    }
                })
        }
    ) { innerPadding ->
        Card(
            modifier = Modifier
                .padding(innerPadding)
                .padding(bottom = 20.dp)
                .fillMaxSize(),
            colors = CardColors(
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                disabledContentColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            LoadingContainer(isInit = viewModel.isInit.value) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(viewModel.cityList) { index, it ->
                        EditCityCard(
                            it,
                            onDeleteClick = { viewModel.removeCity(viewModel.cityList[index]) },
                            onHomeClick = { viewModel.setHomeCity(viewModel.cityList[index]) })
                    }
                }
            }
        }
    }
}

@Composable
fun CityEditTopBar(cancelClick: () -> Unit, saveClick: () -> Unit) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                .height(55.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = cancelClick, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close, null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "编辑城市",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = saveClick, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check, null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        SearchButton(
            "搜索城市(中文/拼音)",
            Modifier
                .alpha(0.6f)
                .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                .fillMaxWidth()
                .height(42.dp),
            enable = false
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun EditCityCard(
    weather: LocationWeatherModel,
    onHomeClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    DefaultCard(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .height(110.dp)
    )
    {
        Box(modifier = Modifier.fillMaxSize()) {
            weather.weatherNow?.let {
                WeatherBackgroundCard(
                    weather.weatherNow.icon,
                    modifier = Modifier.matchParentSize(),
                    isDay = LocalDateTime.now().hour in 6..17
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxSize()
                ) {
                    weather.location?.let {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp)
                        ) {
                            if (weather.type == CityType.Position) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    null,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .alpha(0.9f)
                                )
                            }
                            Text(
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                text = "${it.adm1} " +
                                        (if (it.adm2.equals(it.name)) "" else it.adm2 + " ") +
                                        "${it.name}",
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                    }

                    if (weather.warnings != null && weather.warnings.isNotEmpty()) {
                        val warning = weather.warnings.first()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 4.dp)
                            )
                            Text(
                                style = MaterialTheme.typography.bodyMedium,
                                text = "${warning.typeName}${warning.level}预警",
                                modifier = Modifier.height(20.dp),
                                softWrap = false, overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        weather.weatherNow?.let {
                            Text(
                                style = MaterialTheme.typography.bodyMedium,
                                text = it.text,
                                modifier = Modifier.height(20.dp),
                                softWrap = false, overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { onHomeClick?.invoke() }
                                )
                            }
                            .padding(end = 24.dp)) {
                        val item =
                            if (weather.type == CityType.Host) Pair(
                                Icons.Rounded.Home,
                                "取消常驻地"
                            ) else Pair(Icons.Outlined.Home, "设为常驻地")
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = item.first,
                                null,
                                modifier = Modifier
                                    .padding(bottom = 4.dp, top = 8.dp)
                                    .size(24.dp)
                            )
                            Text(text = item.second, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { onDeleteClick?.invoke() }
                                )
                            }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                null,
                                modifier = Modifier
                                    .padding(bottom = 4.dp, top = 8.dp)
                                    .size(24.dp)

                            )
                            Text(text = "删除", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
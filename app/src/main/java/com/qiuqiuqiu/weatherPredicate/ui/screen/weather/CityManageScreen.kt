package com.qiuqiuqiu.weatherPredicate.ui.screen.weather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.qiuqiuqiu.weatherPredicate.LocalAppViewModel
import com.qiuqiuqiu.weatherPredicate.model.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.CityType
import com.qiuqiuqiu.weatherPredicate.model.LocationWeatherModel
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseItem
import com.qiuqiuqiu.weatherPredicate.ui.normal.DefaultElevatedCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.LoadingContainer
import com.qiuqiuqiu.weatherPredicate.viewModel.AppViewModel
import com.qiuqiuqiu.weatherPredicate.viewModel.CityManageViewModel

@Composable
fun CityManageScreen(navController: NavController) {
    val viewModel: CityManageViewModel = hiltViewModel()
    val appViewModel: AppViewModel = LocalAppViewModel.current
    viewModel.refreshCities()
    Scaffold(
        topBar = {
            CityManagerTopBar(
                navBack = { navController.popBackStack() },
                searchClick = { navController.navigate("WeatherSearch") },
                editClick = { navController.navigate("CityEdit") })
        },
        bottomBar = {
            CityManagerBottomBar(onClick = { navController.navigate("WeatherSearch") })
        }
    ) { innerPadding ->
        Card(
            modifier = Modifier
                .padding(innerPadding)
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
                    items(viewModel.cityList) {
                        CityCard(it, onClick = {
                            it.location?.let { location ->
                                appViewModel.setCurrentCity(
                                    CityLocationModel(
                                        it.type,
                                        location = Pair(
                                            it.location.lon.toDouble(),
                                            it.location.lat.toDouble()
                                        )
                                    )
                                )
                                navController.navigate("Main") {
                                    popUpTo("Main") { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        })
                    }
                }
            }
        }

    }
}

@Composable
fun CityManagerTopBar(
    navBack: () -> Unit,
    searchClick: () -> Unit,
    editClick: () -> Unit
) {
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
                onClick = navBack, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft, null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "管理城市",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = editClick, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(36.dp)
            )
            {
                Icon(
                    imageVector = Icons.Outlined.Settings, null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        SearchButton(
            "搜索城市(中文/拼音)",
            Modifier
                .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                .fillMaxWidth()
                .height(42.dp),
            onClick = searchClick
        )
    }

}

@Composable
fun CityManagerBottomBar(onClick: (() -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(90.dp)
    ) {
        ElevatedButton(
            onClick = { onClick?.invoke() },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.elevatedButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
            modifier = Modifier
                .width(200.dp)
                .padding(bottom = 25.dp)
        ) {
            Text(
                text = "添加城市",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun SearchButton(
    label: String,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    DefaultElevatedCard(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = { onClick?.invoke() }, enabled = enable)
        ) {
            Icon(
                imageVector = Icons.Default.Search, null,
                modifier = Modifier
                    .padding(start = 12.dp, end = 8.dp)
                    .size(22.dp)
            )

            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            {
                Text(
                    label,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .alpha(0.6f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                )
            }
        }
    }
}

@Composable
fun CityCard(weather: LocationWeatherModel, onClick: (() -> Unit)? = null) {
    DefaultElevatedCard(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .height(110.dp),
    )
    {
        BaseItem(onClick = onClick) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(2.5f)
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
                            } else if (weather.type == CityType.Host) {
                                Icon(
                                    Icons.Rounded.Home,
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

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        weather.weatherNow?.let {
                            Text(
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 34.sp),
                                text = weather.weatherNow!!.temp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Text(text = "℃", style = MaterialTheme.typography.titleMedium)
                    }

                    weather.weatherDailies?.let {
                        val weatherDay = it.first()
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = "${weatherDay.tempMax}/${weatherDay.tempMin}℃",
                            modifier = Modifier.alpha(0.6f)
                        )
                    }
                }
            }
        }

    }
}
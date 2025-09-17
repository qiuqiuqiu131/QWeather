package com.qiuqiuqiu.weatherPredicate.ui.screen.weather

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.qiuqiuqiu.weatherPredicate.LocalAppViewModel
import com.qiuqiuqiu.weatherPredicate.service.hasLocationPermissions
import com.qiuqiuqiu.weatherPredicate.service.isLocationPermanentlyDenied
import com.qiuqiuqiu.weatherPredicate.ui.normal.LoadingContainer
import com.qiuqiuqiu.weatherPredicate.ui.normal.SearchTextBox
import com.qiuqiuqiu.weatherPredicate.ui.normal.showPermissionSettingDialog
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background.WeatherBackground
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.AddPositionCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.PoiCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.SearchCityCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.SearchHistoryCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.TopCityCard
import com.qiuqiuqiu.weatherPredicate.viewModel.AppViewModel
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherSearchViewModel
import java.time.LocalDateTime

@SuppressLint("NewApi")
@Composable
fun WeatherSearchScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    var showDialog by remember { mutableStateOf(false) }

    val viewModel: WeatherSearchViewModel = hiltViewModel()
    val searchCityModel by viewModel.searchCityModel.collectAsState()
    val searchHistory by viewModel.searchHistories.collectAsState()
    val rangePoi by viewModel.rangePois.collectAsState()
    val input by viewModel.searchInputFlow.collectAsState()

    val appViewModel: AppViewModel = LocalAppViewModel.current
    val currentCity by appViewModel.currentCity.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(currentCity) {
        viewModel.initSearchData(currentCity)
    }

    if (showDialog) {
        showPermissionSettingDialog(onDismiss = { showDialog = false }, context = context)
    }

    if (appViewModel.currentBg.value != null)
        WeatherBackground(
            appViewModel.currentBg.value!!, modifier = Modifier.fillMaxSize(),
            isDay = LocalDateTime.now().hour in 6..17
        )
    else
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )


    Scaffold(
        topBar = {
            WeatherSearchTopBar(
                input,
                {
                    viewModel.onSearchInputChange(it)
                },
                { navController.popBackStack() }
            )
        },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSecondary
    ) { innerPadding ->
        LoadingContainer(
            isInit = viewModel.isInit.value,
            color = if (appViewModel.currentBg.value == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
        ) {
            if (!viewModel.searchCities.value.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .imePadding()
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "全部城市",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Spacer(modifier = Modifier.size(2.dp))
                    SearchCityCard(
                        viewModel.searchCities.value!!,
                        onClick = {
                            focusManager.clearFocus(true)
                            viewModel.addSearchHistory(it)
                            viewModel.initCityWeather(it, {
                                navController.navigate(
                                    "CityWeather?longitude=${it.lon}&latitude=${it.lat}",
                                    NavOptions.Builder()
                                        .setLaunchSingleTop(true)
                                        .build()
                                )
                            })
                        }
                    )
                }
            } else {
                Column(
                    modifier =
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .verticalScroll(
                                scrollState,
                                enabled = !viewModel.isInit.value
                            ),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SearchHistoryCard(
                        searchHistory, onClearClick = {
                            viewModel.clearSearchHistories()
                        },
                        onClick = {
                            viewModel.searchHistoryClick(it)
                        })

                    if (viewModel.requirePosition()) {
                        val permissionLauncher =
                            rememberLauncherForActivityResult(
                                ActivityResultContracts.RequestMultiplePermissions(),
                                { permissions ->
                                    val granted =
                                        permissions[
                                            Manifest.permission
                                                .ACCESS_FINE_LOCATION] ==
                                                true ||
                                                permissions[
                                                    Manifest.permission
                                                        .ACCESS_COARSE_LOCATION] ==
                                                true
                                    if (granted) {
                                        viewModel.addPositionCity {
                                            appViewModel.setCurrentCity(it)
                                            navController.popBackStack()
                                        }
                                    }
                                }
                            )
                        AddPositionCard(
                            viewModel.isLoadingLocation.value,
                            onClick = {
                                if (!hasLocationPermissions(context)) {
                                    if (!isLocationPermanentlyDenied(context)) {
                                        showDialog = true
                                    } else {
                                        // 正常请求权限
                                        permissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission
                                                    .ACCESS_FINE_LOCATION,
                                                Manifest.permission
                                                    .ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                } else if (!viewModel.isLocationEnabled()) {
                                    // 请求开启定位功能
                                } else {
                                    viewModel.addPositionCity {
                                        appViewModel.setCurrentCity(it)
                                        navController.popBackStack()
                                    }
                                }
                            }
                        )
                    }

                    searchCityModel.topCities?.let { cities ->
                        TopCityCard(
                            cities,
                            onClick = {
                                viewModel.initCityWeather(
                                    it,
                                    {
                                        navController.navigate(
                                            "CityWeather?longitude=${it.lon}&latitude=${it.lat}",
                                            NavOptions.Builder()
                                                .setLaunchSingleTop(true)
                                                .build()
                                        )
                                    }
                                )
                            }
                        )
                    }

                    rangePoi?.let {
                        if (!it.isEmpty()) {
                            PoiCard(it, onClick = { poi ->
                                // 地图侧边栏
                                navController.navigate("SideMap?title=${poi.name}&longitude=${poi.lon}&latitude=${poi.lat}")
                            })
                        }
                    }

                    Spacer(modifier = Modifier.height(180.dp))
                }
            }
        }
    }
}

@Composable
fun WeatherSearchTopBar(input: String?, inputChanged: (String) -> Unit, navBack: () -> Unit) {
    Row(
        modifier =
            Modifier
                .statusBarsPadding()
                .height(60.dp)
                .padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = navBack, modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                null,
                modifier = Modifier.size(24.dp)
            )
        }
        SearchTextBox(
            "搜索城市(中文/拼音)",
            input ?: "",
            inputChanged,
            { inputChanged("") },
            Modifier
                .padding(vertical = 2.dp)
                .weight(1f)
                .fillMaxSize()
        )
    }
}
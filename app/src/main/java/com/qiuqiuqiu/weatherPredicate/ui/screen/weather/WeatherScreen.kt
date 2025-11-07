package com.qiuqiuqiu.weatherPredicate.ui.screen.weather

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.qiuqiuqiu.weatherPredicate.LocalAppViewModel
import com.qiuqiuqiu.weatherPredicate.SwitchStatusBarColor
import com.qiuqiuqiu.weatherPredicate.model.weather.CityType
import com.qiuqiuqiu.weatherPredicate.model.weather.LocationWeatherModel
import com.qiuqiuqiu.weatherPredicate.service.hasLocationPermissions
import com.qiuqiuqiu.weatherPredicate.service.isLocationPermanentlyDenied
import com.qiuqiuqiu.weatherPredicate.tools.isToday
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseItem
import com.qiuqiuqiu.weatherPredicate.ui.normal.LoadingContainer
import com.qiuqiuqiu.weatherPredicate.ui.normal.PagerIndicator
import com.qiuqiuqiu.weatherPredicate.ui.normal.rememberScrollAlpha
import com.qiuqiuqiu.weatherPredicate.ui.normal.rememberScrollThreshold
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background.JieQiBackground
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background.JieQiType
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background.WeatherBackground
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background.getIndicatorColor
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.AirCurrentCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.DailyWeatherCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.HourlyWeatherCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.LifeIndexCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.MoreWeatherButton
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.WarningInfoCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.WeatherCurrentCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.WeatherIndexCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.WeatherStatusInfoCard
import com.qiuqiuqiu.weatherPredicate.viewModel.AppViewModel
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherMainViewModel
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherViewModel
import com.qweather.sdk.response.geo.Location
import java.time.LocalDateTime

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController) {
    val appViewModel: AppViewModel = LocalAppViewModel.current
    val context = LocalContext.current

    val mainViewModel: WeatherMainViewModel = hiltViewModel()
    val weatherViewModel = mainViewModel.cityWeather.collectAsState()
    mainViewModel.initCities()

    val scrollState: ScrollState = rememberScrollState()
    val centerCardAlpha = rememberScrollAlpha(scrollState, 70, 230)
    val bgAlpha = rememberScrollAlpha(scrollState, 300, 600)
    val cityTextHide = rememberScrollThreshold(scrollState, 70)

    val pageState: PagerState = rememberPagerState(
        appViewModel.currentIndex.intValue,
        pageCount = { mainViewModel.cities.value.size })

    LaunchedEffect(appViewModel.currentIndex.intValue) {
        if (appViewModel.currentIndex.intValue != pageState.targetPage)
            pageState.scrollToPage(appViewModel.currentIndex.intValue)
    }

    LaunchedEffect(pageState.currentPage) {
        if (appViewModel.currentIndex.intValue != pageState.currentPage)
            appViewModel.currentIndex.intValue = pageState.currentPage
        if (mainViewModel.pageIndex.intValue != pageState.currentPage) {
            mainViewModel.pageIndex.intValue = pageState.currentPage
        }
    }

    val hasPermissions = hasLocationPermissions(context)
    val isDenied = isLocationPermanentlyDenied(context)

    LoadingContainer(
        isInit = mainViewModel.isInit.value,
        color = MaterialTheme.colorScheme.primary
    ) {
        var indicatorColor = MaterialTheme.colorScheme.surfaceVariant

        // 初始化背景
        val start: WeatherViewModel =
            hiltViewModel(key = appViewModel.currentIndex.intValue.toString())
        start.locationWeather.value.weatherNow?.let {
            appViewModel.currentBg.value = it.icon
        }

        // background
        if (weatherViewModel.value?.locationWeather?.value?.weatherNow != null) {
            if (appViewModel.jieqi.value != null) {
                val name = appViewModel.jieqi.value!!.name
                JieQiBackground(name, bgAlpha.value)
                val type =
                    JieQiType.entries.firstOrNull { it.text == name }
                        ?: JieQiType.LiChun
                indicatorColor = type.backgroundColor
            } else {
                val id = weatherViewModel.value!!.locationWeather.value.weatherNow!!.icon
                val isDay = LocalDateTime.now().hour in 6..17
                WeatherBackground(
                    id,
                    modifier = Modifier.fillMaxSize(),
                    isDay = isDay
                )
                indicatorColor = getIndicatorColor(id, isDay, indicatorColor)
            }
        } else
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )

        val state = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = mainViewModel.isRefreshing.value,
            state = state,
            onRefresh = {
                if (!mainViewModel.isRefreshing.value) {
                    mainViewModel.isRefreshing.value = true
                    weatherViewModel.value?.updateWeatherModel {
                        mainViewModel.isRefreshing.value = false
                    }
                }
            },
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = mainViewModel.isRefreshing.value,
                    state = state,
                    containerColor = indicatorColor,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            },
        ) {
            Scaffold(
                topBar = {
                    WeatherTopBar(
                        weatherViewModel.value?.locationWeather?.value,
                        navController,
                        centerCardAlpha,
                        cityTextHide,
                        onCityClick = { navController.navigate("CityManage") }
                    )
                },
                bottomBar = {
                    PagerIndicator(
                        mainViewModel.cities.value.size,
                        mainViewModel.pageIndex.intValue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .align(Alignment.Center),
                        selectedColor = MaterialTheme.colorScheme.surfaceContainer,
                        unselectedColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.4f),
                        dotSize = 4.5.dp,
                        selectedDotSize = 6.5.dp,
                        spacing = 4.dp
                    )
                },
                modifier = Modifier.padding(bottom = 0.dp),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) { innerPadding ->
                SwitchStatusBarColor(false)

                HorizontalPager(
                    pageState,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) { it ->
                    val currentCity =
                        mainViewModel.cities.value.getOrNull(it) ?: return@HorizontalPager
                    val index = remember { mutableIntStateOf(it) }

                    val viewModel: WeatherViewModel =
                        hiltViewModel(key = it.toString())

                    val permissionLauncher =
                        rememberLauncherForActivityResult(
                            ActivityResultContracts.RequestMultiplePermissions()
                        ) {
                            viewModel.initLocation(currentCity)
                        }

                    LaunchedEffect(Unit) {
                        if (!viewModel.isInit.value) {
                            if (currentCity.type == CityType.Position && !hasPermissions && isDenied) {
                                // 只在副作用中调用 launch
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            } else {
                                viewModel.initLocation(currentCity)
                            }
                        } else {
                            viewModel.initLocation(currentCity)
                        }
                    }

                    LaunchedEffect(pageState.currentPage) {
                        if (pageState.currentPage == index.intValue) {
                            mainViewModel.setCurrentCity(viewModel)
                            viewModel.locationWeather.value.weatherNow?.let {
                                appViewModel.currentBg.value = it.icon
                            }
                        }
                    }

                    if (!viewModel.isInit.value) {
                        WeatherCenterPage(
                            weatherModel = viewModel.locationWeather.value,
                            scrollState = scrollState,
                            navController = navController,
                            alpha = centerCardAlpha,
                            cityHide = cityTextHide
                        )
                    }
                }
            }
        }
    }
}

/** 天气页面顶部栏 */
@Composable
fun WeatherTopBar(
    weatherModel: LocationWeatherModel?,
    navController: NavController,
    alpha: State<Float>,
    cityHide: State<Boolean>,
    onCityClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(50.dp)
    ) {
        weatherModel?.location?.let {
            val text =
                "${it.adm1} " + (if (it.adm2.equals(it.name)) "" else it.adm2 + " ") + "${it.name}"
            BaseItem(
                onClick = onCityClick,
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(top = ((1 - alpha.value) * 26).dp)
                        .alpha((if (cityHide.value) 1f else 0f)),
                innerModifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = (15 + 2 * (1 - alpha.value)).sp,
                        text = text,
                        modifier = Modifier.widthIn(max = 300.dp),
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false
                    )
                    if (weatherModel.type == CityType.Position) {
                        Icon(
                            Icons.Default.LocationOn,
                            null,
                            modifier = Modifier
                                .size(22.dp)
                                .padding(bottom = 2.dp)
                        )
                    }
                }
            }

//            IconButton(
//                onClick = {
//                    navController.navigate(
//                        "SideMap?title=${text}&longitude=${it.lon}&latitude=${it.lat}"
//                    )
//                },
//                modifier = Modifier
//                    .padding(8.dp)
//                    .clickable {}
//                    .align(Alignment.CenterStart)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.MyLocation,
//                    null,
//                    modifier = Modifier.size(26.dp)
//                )
//            }
        }


        var expended by remember { mutableStateOf(false) }
        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            IconButton(
                onClick = {
                    navController.navigate(
                        "WeatherSearch",
                        NavOptions.Builder().setLaunchSingleTop(true).build()
                    )
                },
                modifier = Modifier
                    .padding(0.dp)
                    .clickable {}
            ) { Icon(imageVector = Icons.Default.Search, null, modifier = Modifier.size(26.dp)) }
            IconButton(
                onClick = {
                    expended = !expended
                }, modifier = Modifier
                    .padding(end = 4.dp)
                    .clickable {}) {
                Icon(imageVector = Icons.Default.MoreVert, null, modifier = Modifier.size(26.dp))
            }
            DropdownMenu(
                expanded = expended,
                onDismissRequest = { expended = false },
                shape = RoundedCornerShape(16.dp),
                offset = DpOffset(x = (-10).dp, y = 0.dp),
                modifier = Modifier.width(140.dp)
            ) {
                BaseItem(
                    onClick = {
                        expended = false
                        navController.navigate("CityManage")
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "城市管理",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .align(Alignment.Center)
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .alpha(0.2f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                BaseItem(onClick = {
                    expended = false
                    navController.navigate("WeatherSearch")
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "搜索",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .align(Alignment.Center)
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .alpha(0.2f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                BaseItem(onClick = {
                    expended = false
                    weatherModel?.let {
                        navToWeatherDetail(
                            navController, pageName = "实况天气",
                            location = it.location
                        )
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "天气信息",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherCenterPage(
    weatherModel: LocationWeatherModel,
    scrollState: ScrollState,
    alpha: State<Float>,
    cityHide: State<Boolean>,
    modifier: Modifier = Modifier,
    centerScreen: Boolean = true,
    navController: NavController? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val weatherDaily = weatherModel.weatherDailies?.first({ d -> d.fxDate.isToday() })

        // 天气中心卡片
        WeatherCurrentCard(
            weatherModel.type,
            weatherModel.location,
            weatherModel.weatherNow,
            weatherDaily,
            alpha,
            cityHide,
            centerScreen,
            onJieQiClick = { navController?.navigate("JieQi") },
            onCityClick = { navController?.navigate("CityManage") },
            onClick = {
                navToWeatherDetail(
                    navController, pageName = "实况天气",
                    location = weatherModel.location
                )
            }
        )

        Spacer(modifier = Modifier.height(50.dp))

        // 通知栏天气信息
        weatherModel.indicesDailies?.let {
            WeatherStatusInfoCard(it, onIndicesClick = { name ->
                navToWeatherDetail(
                    navController, pageName = name.replace("指数", ""), pageInfo = name,
                    location = weatherModel.location
                )
            })
        }

        // 预警信息卡片
        WarningInfoCard(weatherModel.warnings, weatherModel.shiJu, onClick = {
            navToWeatherDetail(
                navController, pageName = "预警信息",
                location = weatherModel.location
            )
        })

        // 每小时天气列表
        weatherModel.weatherHourlies?.let {
            HourlyWeatherCard(it, onClick = {
                navToWeatherDetail(
                    navController, pageName = "每日天气",
                    location = weatherModel.location
                )
            }, onItemClick = {
                navToWeatherDetail(
                    navController, pageName = "每日天气",
                    location = weatherModel.location
                )
            })
        }

        // 未来7天天气列表
        weatherModel.weatherDailies?.let {
            DailyWeatherCard(it, onClick = {
                navToWeatherDetail(
                    navController, pageName = "多日天气",
                    location = weatherModel.location
                )
            }, onItemClick = {
                navToWeatherDetail(
                    navController, pageName = "多日天气",
                    location = weatherModel.location
                )
            })
        }

        // 更多天气按钮
        MoreWeatherButton(onClick = {
            navToWeatherDetail(
                navController, pageName = "多日天气",
                location = weatherModel.location
            )
        })

        // 空气质量卡片
        weatherModel.airCurrent?.let {
            AirCurrentCard(it, onClick = {
                navToWeatherDetail(
                    navController, pageName = "空气质量",
                    location = weatherModel.location
                )
            })
        }

        // 天气指数卡片
        weatherModel.weatherNow?.let {
            WeatherIndexCard(it, weatherDaily?.uvIndex, onClick = {
                navToWeatherDetail(
                    navController, pageName = "每日天气",
                    location = weatherModel.location
                )
            })
        }

        // 生活指数卡片
        weatherModel.indicesDailies?.let {
            LifeIndexCard(it, onClick = {
                navToWeatherDetail(
                    navController, pageName = it.firstOrNull()?.name?.replace("指数", ""),
                    location = weatherModel.location
                )
            }, onItemClick = { item ->
                navToWeatherDetail(
                    navController, pageName = item.name.replace("指数", ""),
                    location = weatherModel.location
                )
            })
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

fun navToWeatherDetail(
    navController: NavController?,
    pageName: String? = null,
    pageInfo: String? = null,
    location: Location? = null
) {
    navController?.navigate("WeatherDetail?pageName=$pageName&pageInfo=$pageInfo&longitude=${location?.lon}&latitude=${location?.lat}")
}
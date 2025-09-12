package com.qiuqiuqiu.weatherPredicate.ui.screen.weather

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.qiuqiuqiu.weatherPredicate.LocalAppViewModel
import com.qiuqiuqiu.weatherPredicate.model.CityType
import com.qiuqiuqiu.weatherPredicate.model.LocationWeatherModel
import com.qiuqiuqiu.weatherPredicate.service.hasLocationPermissions
import com.qiuqiuqiu.weatherPredicate.service.isLocationPermanentlyDenied
import com.qiuqiuqiu.weatherPredicate.tools.isToday
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseItem
import com.qiuqiuqiu.weatherPredicate.ui.normal.LoadingContainer
import com.qiuqiuqiu.weatherPredicate.ui.normal.rememberScrollAlpha
import com.qiuqiuqiu.weatherPredicate.ui.normal.rememberScrollThreshold
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
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherViewModel
import com.qweather.sdk.response.geo.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController) {
    val appViewModel: AppViewModel = LocalAppViewModel.current
    val currentCity by appViewModel.currentCity.collectAsState()
    val context = LocalContext.current

    val viewModel: WeatherViewModel = hiltViewModel()
    val weatherModel by viewModel.locationWeather.collectAsState()

    val scrollState: ScrollState = rememberScrollState()
    val centerCardAlpha = rememberScrollAlpha(scrollState, 70, 230)
    val cityTextHide = rememberScrollThreshold(scrollState, 70)

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { viewModel.initLocation(currentCity) }

    LaunchedEffect(currentCity) {
        val hasPermissions = hasLocationPermissions(context)
        val isDenied = isLocationPermanentlyDenied(context)
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
    }

//    Box(
//        modifier = Modifier
//            .background(
//                Color(
//                    red = 0.8f,
//                    green = 0.9f,
//                    blue = 1f,
//                    alpha = 1f,
//                    colorSpace = ColorSpaces.Srgb
//                )
//            )
//            .fillMaxSize()
//    )

    PullToRefreshBox(
        isRefreshing = viewModel.isRefreshing.value,
        onRefresh = { viewModel.refreshing() },
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                WeatherTopBar(
                    weatherModel.type,
                    weatherModel.location,
                    navController,
                    centerCardAlpha,
                    cityTextHide,
                    onCityClick = { navController.navigate("CityManage") }
                )
            }
        ) { innerPadding ->
            LoadingContainer(isInit = viewModel.isInit.value) {
                WeatherCenterPage(
                    weatherModel = weatherModel,
                    scrollState = scrollState,
                    navController = navController,
                    alpha = centerCardAlpha,
                    cityHide = cityTextHide,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

/** 天气页面顶部栏 */
@Composable
fun WeatherTopBar(
    type: CityType,
    location: Location?,
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
        location?.let {
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
                        text =
                            "${it.adm1} " +
                                    (if (it.adm2.equals(it.name)) "" else it.adm2 + " ") +
                                    "${it.name}",
                        modifier = Modifier.widthIn(max = 300.dp),
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false
                    )
                    if (type == CityType.Position) {
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
        }

        IconButton(
            onClick = {
                navController.navigate(
                    "WeatherSearch",
                    NavOptions.Builder().setLaunchSingleTop(true).build()
                )
            },
            modifier = Modifier
                .padding(8.dp)
                .clickable {}
                .align(Alignment.CenterEnd)
        ) { Icon(imageVector = Icons.Default.Search, null, modifier = Modifier.size(26.dp)) }
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
            onCityClick = { navController?.navigate("CityManage") },
            onClick = { navToWeatherDetail(navController, pageName = "实况天气") }
        )

        Spacer(modifier = Modifier.height(50.dp))

        // 通知栏天气信息
        weatherModel.indicesDailies?.let {
            WeatherStatusInfoCard(it, onIndicesClick = { name ->
                navToWeatherDetail(
                    navController, pageName = name.replace("指数", ""), pageInfo = name
                )
            })
        }

        // 预警信息卡片
        weatherModel.warnings?.let {
            WarningInfoCard(it, onClick = {
                navToWeatherDetail(navController, pageName = "预警信息")
            })
        }

        // 每小时天气列表
        weatherModel.weatherHourlies?.let {
            HourlyWeatherCard(it, onClick = {
                navToWeatherDetail(navController, pageName = "每日天气")
            }, onItemClick = {
                navToWeatherDetail(navController, pageName = "每日天气")
            })
        }

        // 未来7天天气列表
        weatherModel.weatherDailies?.let {
            DailyWeatherCard(it, onClick = {
                navToWeatherDetail(navController, pageName = "多日天气")
            }, onItemClick = {
                navToWeatherDetail(navController, pageName = "多日天气")
            })
        }

        // 更多天气按钮
        MoreWeatherButton(onClick = {
            navToWeatherDetail(navController, pageName = "多日天气")
        })

        // 空气质量卡片
        weatherModel.airCurrent?.let {
            AirCurrentCard(it, onClick = {
                navToWeatherDetail(navController, pageName = "空气质量")
            })
        }

        // 天气指数卡片
        weatherModel.weatherNow?.let {
            WeatherIndexCard(it, weatherDaily?.uvIndex, onClick = {
                navToWeatherDetail(navController, pageName = "实况天气")
            })
        }

        // 生活指数卡片
        weatherModel.indicesDailies?.let {
            LifeIndexCard(it, onClick = {
                navToWeatherDetail(
                    navController, pageName = it.firstOrNull()?.name?.replace("指数", "")
                )
            }, onItemClick = { item ->
                navToWeatherDetail(navController, pageName = item.name.replace("指数", ""))
            })
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

private fun navToWeatherDetail(
    navController: NavController?,
    pageName: String? = null,
    pageInfo: String? = null
) {
    navController?.navigate("WeatherDetail?pageName=$pageName&pageInfo=$pageInfo")
}
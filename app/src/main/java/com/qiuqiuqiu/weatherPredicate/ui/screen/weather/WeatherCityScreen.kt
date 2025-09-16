package com.qiuqiuqiu.weatherPredicate.ui.screen.weather

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.qiuqiuqiu.weatherPredicate.LocalAppViewModel
import com.qiuqiuqiu.weatherPredicate.model.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.CityType
import com.qiuqiuqiu.weatherPredicate.ui.normal.LoadingContainer
import com.qiuqiuqiu.weatherPredicate.ui.normal.rememberScrollAlpha
import com.qiuqiuqiu.weatherPredicate.ui.normal.rememberScrollThreshold
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background.WeatherBackground
import com.qiuqiuqiu.weatherPredicate.viewModel.AppViewModel
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherViewModel
import com.qweather.sdk.response.geo.Location
import java.time.LocalDateTime

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherCityScreen(navController: NavController, location: Pair<Double, Double>) {
    val appViewModel: AppViewModel = LocalAppViewModel.current
    val viewModel: WeatherViewModel = hiltViewModel()
    val weatherModel by viewModel.locationWeather.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initLocation(
            CityLocationModel(CityType.Normal, location),
            isMain = false,
            refresh = false
        )
    }

    val scrollState: ScrollState = rememberScrollState()
    val centerCardAlpha = rememberScrollAlpha(scrollState, 70, 300)
    val cityTextHide = rememberScrollThreshold(scrollState, 70)

    if (weatherModel.weatherNow != null)
        WeatherBackground(
            weatherModel.weatherNow!!.icon,
            modifier = Modifier.fillMaxSize(),
            isDay = LocalDateTime.now().hour in 6..17
        )
    else
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )

    PullToRefreshBox(
        isRefreshing = viewModel.isRefreshing.value,
        onRefresh = { viewModel.refreshing() },
    ) {
        Scaffold(
            topBar = {
                WeatherCityTopBar(
                    weatherModel.location,
                    navController,
                    centerCardAlpha,
                    cityTextHide
                )
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) { innerPadding ->
            LoadingContainer(
                isInit = viewModel.isInit.value,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(modifier = Modifier.padding(innerPadding)) {
                    WeatherCenterPage(
                        weatherModel = weatherModel,
                        scrollState = scrollState,
                        alpha = centerCardAlpha,
                        cityHide = cityTextHide,
                        centerScreen = false,
                        navController = navController
                    )

                    WeatherCityBottomBar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        onClick = {
                            appViewModel.addCity(location)
                            navController.navigate("Main") {
                                popUpTo("Main") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherCityBottomBar(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    color: Color = MaterialTheme.colorScheme.background
) {
    val boxHeight = 150.dp
    val density = LocalDensity.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier =
            modifier
                .fillMaxWidth()
                .height(boxHeight)
                .background(
                    Brush.verticalGradient(
                        colors =
                            listOf(
                                MaterialTheme.colorScheme.background
                                    .copy(alpha = 0f), // 顶部透明
                                MaterialTheme.colorScheme.background
                                    .copy(alpha = 0.4f), // 中间半透明
                                MaterialTheme.colorScheme.background
                                    .copy(alpha = 0.6f)// 底部不透明
                            ),
                        startY = 0f,
                        endY = with(density) { boxHeight.toPx() }
                    )
                )
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
                .padding(top = 60.dp)
        ) {
            Text(
                text = "添加城市",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

/** 天气页面顶部栏 */
@Composable
fun WeatherCityTopBar(
    location: Location?,
    navController: NavController,
    alpha: State<Float>,
    cityHide: State<Boolean>
) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(50.dp)
    ) {
        location?.let {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = ((1 - alpha.value) * 26).dp)
                    .widthIn(max = 300.dp)
                    .alpha((if (cityHide.value) 1f else 0f)),
                fontWeight = FontWeight.SemiBold,
                fontSize = (15 + 2 * (1 - alpha.value)).sp,
                text =
                    "${it.adm1} " +
                            (if (it.adm2.equals(it.name)) "" else it.adm2 + " ") +
                            "${it.name}",
                overflow = TextOverflow.Ellipsis,
                softWrap = false
            )
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(8.dp)
                .clickable {}
                .align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                null,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

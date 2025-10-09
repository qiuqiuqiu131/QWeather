package com.qiuqiuqiu.weatherPredicate.ui.screen.weather

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.qiuqiuqiu.weatherPredicate.SwitchStatusBarColor
import com.qiuqiuqiu.weatherPredicate.model.weather.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.weather.CityType
import com.qiuqiuqiu.weatherPredicate.tools.toTimeWithPeriod
import com.qiuqiuqiu.weatherPredicate.ui.normal.LoadingContainer
import com.qiuqiuqiu.weatherPredicate.ui.normal.ScrollableCenterNormalRowList
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage.WeatherAirDetailPage
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage.WeatherCurrentDetailPage
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage.WeatherDailyDetailPage
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage.WeatherHourlyDetailPage
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.detailPage.WeatherIndicesPage
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherDetailViewModel
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.defaultPageNames
import kotlinx.coroutines.launch

@Composable
fun WeatherDetailScreen(
    navController: NavController,
    location: Pair<Double, Double>,
    pageName: String? = null,
    pageInfo: String? = null
) {
    val viewModel: WeatherDetailViewModel = hiltViewModel()
    val weatherModel by viewModel.locationWeather.collectAsState()

    val defaultColor = MaterialTheme.colorScheme.background
    var topBarColor by remember { mutableStateOf(defaultColor) }
    val defaultContentColor = MaterialTheme.colorScheme.onSecondaryContainer
    var contentColor by remember { mutableStateOf(defaultContentColor) }

    viewModel.initWeatherData(CityLocationModel(CityType.Normal, location), pageName)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    )

    LoadingContainer(isInit = viewModel.isInit.value) {
        val pageState = rememberPagerState(
            viewModel.pageIndex.intValue,
            pageCount = { viewModel.pageItems.value.size }
        )
        val coroutineScope = rememberCoroutineScope()

        val switchPage: ((Int) -> Unit) = { index ->
            if (viewModel.pageIndex.intValue != index) {
                coroutineScope.launch {
                    pageState.animateScrollToPage(
                        index, animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow,
                        )
                    )
                }
            }
        }


        var darkIcon by remember { mutableStateOf(true) }
        val themeChanged: (Color?, Color?, Boolean?) -> Unit = { color, ctColor, darkicon ->
            topBarColor = color ?: defaultColor
            contentColor = ctColor ?: defaultContentColor
            darkIcon = darkicon ?: true
        }

        LaunchedEffect(pageState.targetPage) {
            if (viewModel.pageIndex.intValue != pageState.targetPage)
                viewModel.pageIndex.intValue = pageState.targetPage
        }
        SwitchStatusBarColor(darkIcon)
        Scaffold(
            topBar = {
                weatherModel.location?.let {
                    WeatherDetailTopBar(
                        cityName =
                            "${it.adm1} " +
                                    (if (it.adm2.equals(it.name)) "" else it.adm2 + " ") +
                                    "${it.name}",
                        pageItems = viewModel.pageItems.value,
                        pageIndex = viewModel.pageIndex.intValue,
                        bgColor = topBarColor,
                        contentColor = contentColor,
                        navBack = { navController.popBackStack() },
                        onSelectionChanged = switchPage
                    )
                }
            }
        ) { innerPadding ->
            LoadingContainer(isInit = viewModel.isInit.value) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    HorizontalPager(
                        pageState,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    ) { it ->
                        val pageName = viewModel.pageItems.value[it]
                        when (pageName) {
                            in defaultPageNames -> {
                                when (pageName) {
                                    "每日天气" -> WeatherHourlyDetailPage(
                                        viewModel,
                                        currentPageIndex = pageState.targetPage,
                                        pageIndex = it,
                                        onThemeChanged = themeChanged, onSwitchPage = { pageName ->
                                            val index = viewModel.pageItems.value.indexOf(pageName)
                                            switchPage(index)
                                        })

                                    "多日天气" -> WeatherDailyDetailPage(
                                        weatherModel.weatherDailiesMore,
                                        currentPageIndex = pageState.targetPage,
                                        pageIndex = it,
                                        onThemeChanged = themeChanged
                                    )

                                    "实况天气" -> WeatherCurrentDetailPage(
                                        weatherNow = weatherModel.weatherNow,
                                        weatherDaily = weatherModel.weatherDailies?.firstOrNull(),
                                        lastUpdateTime = weatherModel.lastUpdateTime.toString()
                                            .toTimeWithPeriod(),
                                        currentPageIndex = pageState.targetPage,
                                        pageIndex = it,
                                        onThemeChanged = themeChanged, onSwitchPage = switchPage
                                    )

                                    "空气质量" -> WeatherAirDetailPage(
                                        model = weatherModel,
                                        currentPageIndex = pageState.targetPage,
                                        pageIndex = it,
                                        onThemeChanged = themeChanged
                                    )

                                    else -> Text(pageName)
                                }
                            }

                            else -> {
                                WeatherIndicesPage(
                                    weatherIndices = weatherModel.indicesDailiesMore?.firstOrNull {
                                        it.first.replace("指数", "") == pageName
                                    }?.second, weatherModel.weatherDailies?.take(3),
                                    currentPageIndex = pageState.targetPage,
                                    pageIndex = it,
                                    viewModel = viewModel,
                                    key = "$it$pageInfo",
                                    onThemeChanged = themeChanged,
                                    onSwitchPage = switchPage
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun WeatherDetailTopBar(
    cityName: String,
    pageItems: List<String>,
    bgColor: Color,
    contentColor: Color,
    pageIndex: Int,
    navBack: () -> Unit,
    onSelectionChanged: (index: Int) -> Unit,
) {

    Column(
        modifier = Modifier
            .background(bgColor)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .height(50.dp)
        ) {
            IconButton(
                onClick = navBack,
                modifier =
                    Modifier
                        .padding(horizontal = 4.dp)
                        .size(36.dp)
                        .align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = cityName,
                    color = contentColor,
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                    modifier = Modifier.widthIn(max = 300.dp),
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }
        }
        ScrollableCenterNormalRowList(
            itemCount = pageItems.size,
            itemIndex = pageIndex,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .height(25.dp),
            selectedItemChanged = onSelectionChanged
        ) { index, isSelected ->
            val style =
                if (isSelected) MaterialTheme.typography.titleMedium.copy(fontSize = 21.sp)
                else MaterialTheme.typography.titleMedium
            Text(
                text = pageItems[index],
                style = style,
                color = contentColor,
                modifier = Modifier.alpha(if (isSelected) 1f else 0.6f)
            )
        }
    }
}
package com.qiuqiuqiu.weatherPredicate.ui.screen.weather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
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
import com.qiuqiuqiu.weatherPredicate.ui.normal.LoadingContainer
import com.qiuqiuqiu.weatherPredicate.ui.normal.ScrollableCenterRowList
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.WeatherDetailViewModel

@Composable
fun WeatherDetailScreen(navController: NavController) {
    val appViewModel = LocalAppViewModel.current
    val currentCity by appViewModel.currentCity.collectAsState()

    val viewModel: WeatherDetailViewModel = hiltViewModel()
    val weatherModel by viewModel.locationWeather.collectAsState()

    LaunchedEffect(currentCity) {
        currentCity?.let { viewModel.initWeatherData(it) }
    }

    Scaffold(
        topBar = {
            weatherModel.location?.let {
                WeatherDetailTopBar(
                    cityName = "${it.adm1} " +
                            (if (it.adm2.equals(it.name)) "" else it.adm2 + " ") +
                            "${it.name}",
                    pageItems = weatherModel.indicesDailies?.map { it.name.replace("指数", "") }
                        ?: emptyList(),
                    onSelectedPageChanged = {

                    },
                    navBack = { navController.popBackStack() })
            }
        }
    ) { innerPadding ->
        LoadingContainer(isInit = viewModel.isInit.value) {
            Column(modifier = Modifier.padding(innerPadding)) {

            }
        }
    }
}

@Composable
fun WeatherDetailTopBar(
    cityName: String,
    pageItems: List<String>,
    onSelectedPageChanged: (Int) -> Unit,
    navBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .height(50.dp)
        ) {
            IconButton(
                onClick = navBack, modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(36.dp)
                    .align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = cityName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.widthIn(max = 300.dp),
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }

        }
        ScrollableCenterRowList(
            itemCount = pageItems.size,
            itemIndex = 0,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .height(30.dp),
            selectedItemChanged = onSelectedPageChanged
        ) { index, isSelected ->
            val style = if (isSelected)
                MaterialTheme.typography.titleMedium.copy(fontSize = 21.sp)
            else
                MaterialTheme.typography.titleMedium
            Text(
                text = pageItems[index], style = style,
                modifier = Modifier.alpha(if (isSelected) 1f else 0.6f)
            )
        }
    }
}
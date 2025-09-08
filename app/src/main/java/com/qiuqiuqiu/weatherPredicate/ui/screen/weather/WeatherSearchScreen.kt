package com.qiuqiuqiu.weatherPredicate.ui.screen.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.qiuqiuqiu.weatherPredicate.LocalAppViewModel
import com.qiuqiuqiu.weatherPredicate.ui.normal.BaseCard
import com.qiuqiuqiu.weatherPredicate.ui.normal.LoadingContainer
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.SearchCityCard
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.card.TopCityCard
import com.qiuqiuqiu.weatherPredicate.viewModel.AppViewModel
import com.qiuqiuqiu.weatherPredicate.viewModel.WeatherSearchViewModel

@Composable
fun WeatherSearchScreen(navController: NavController) {
    var input by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val viewModel: WeatherSearchViewModel = hiltViewModel()
    val searchCityModel by viewModel.searchCityModel.collectAsState()

    val appViewModel: AppViewModel = LocalAppViewModel.current

    val focusManager = LocalFocusManager.current

    Scaffold(topBar = {
        WeatherSearchTopBar(
            input, {
                input = it
                viewModel.onSearchInputChange(it)
            },
            { navController.popBackStack() })
    }) { innerPadding ->
        LoadingContainer(isInit = viewModel.isInit.value) {
            if (viewModel.searchCities.value != null) {
                Column(
                    modifier =
                        Modifier
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
                            viewModel.initCityWeather(it, {
                                navController.navigate(
                                    "CityWeather?longitude=${it.lon}&latitude=${it.lat}",
                                    NavOptions.Builder().setLaunchSingleTop(true).build()
                                )
                            })
                        })
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
                    searchCityModel.topCities?.let { cities ->
                        TopCityCard(cities, onClick = {
                            viewModel.initCityWeather(it, {
                                navController.navigate(
                                    "CityWeather?longitude=${it.lon}&latitude=${it.lat}",
                                    NavOptions.Builder().setLaunchSingleTop(true).build()
                                )
                            })
                        })
                    }

                    if (viewModel.requirePosition()) {
                        BaseCard(onClick = {
                            appViewModel.addPositionCity()
                            navController.popBackStack()
                        }) {
                            Text("添加定位", modifier = Modifier.fillMaxSize())
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun WeatherSearchTopBar(
    input: String, inputChanged: (String) -> Unit,
    navBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .height(60.dp)
            .padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
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
        SearchTextBox(
            "搜索城市(中文/拼音)",
            input, inputChanged, Modifier
                .padding(vertical = 2.dp)
                .weight(1f)
                .fillMaxSize()
        )
    }
}

@Composable
fun SearchTextBox(
    label: String,
    input: String,
    inputChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
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
                BasicTextField(
                    value = input,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    onValueChange = inputChanged,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (input.isEmpty()) {
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
}
package com.qiuqiuqiu.weatherPredicate.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.qiuqiuqiu.weatherPredicate.LocalAppViewModel
import com.qiuqiuqiu.weatherPredicate.SwitchStatusBarColor
import com.qiuqiuqiu.weatherPredicate.ui.screen.map.MapScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.WeatherScreen
import com.qiuqiuqiu.weatherPredicate.viewModel.AppViewModel

enum class MainNaviBar(val label: String, val icon: ImageVector, val contentDescription: String) {
    Weather("Weather", Icons.Default.CloudQueue, "Weather"),
    Map("Map", Icons.Default.Map, "Map")
}

@Composable
fun MainHost(
    selectedBar: MainNaviBar,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val appViewModel: AppViewModel = LocalAppViewModel.current
    Box(modifier = modifier) {
        when (selectedBar) {
            MainNaviBar.Weather -> {
                appViewModel.naviBarIconColor.value = MaterialTheme.colorScheme.onSecondary
                appViewModel.naviBarContainerColor.value = Color.Transparent
                appViewModel.naviBarIndicatorColor.value =
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                WeatherScreen(navController)
            }

            MainNaviBar.Map -> {
                appViewModel.clearNaviBarColor()
                SwitchStatusBarColor(true)
                MapScreen(navController = navController)
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController, modifier: Modifier = Modifier) {
    val startDestination = MainNaviBar.Weather
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination) }

    val appViewModel: AppViewModel = LocalAppViewModel.current

    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            MainHost(
                selectedDestination,
                navController, modifier.fillMaxSize()
            )

            NavigationBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(50.dp),
                containerColor = appViewModel.naviBarContainerColor.value
                    ?: MaterialTheme.colorScheme.background
            ) {
                val iconColor = appViewModel.naviBarIconColor.value
                    ?: MaterialTheme.colorScheme.onTertiary
                val indicatorColor = appViewModel.naviBarIndicatorColor.value
                    ?: MaterialTheme.colorScheme.tertiaryContainer
                MainNaviBar.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == destination,
                        onClick = { selectedDestination = destination },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        colors = NavigationBarItemDefaults.colors().copy(
                            selectedIconColor = iconColor,
                            unselectedIconColor = iconColor.copy(alpha = 0.7f),
                            selectedIndicatorColor = indicatorColor,
                        ),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }

    }
}

package com.qiuqiuqiu.weatherPredicate.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.qiuqiuqiu.weatherPredicate.ui.screen.map.MapScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.time.TimeScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.WeatherScreen

enum class MainNaviBar(val label: String, val icon: ImageVector, val contentDescription: String) {
    Weather("Weather", Icons.Default.CloudQueue, "Weather"),
    Map("Map", Icons.Default.Map, "Map"),
    Time("Time", Icons.Default.AccessTime, "Time")
}

@Composable
fun MainHost(
    selectedBar: MainNaviBar,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (selectedBar) {
            MainNaviBar.Weather -> {
                WeatherScreen(navController)
            }

            MainNaviBar.Map -> {
                MapScreen()
            }

            MainNaviBar.Time -> {
                TimeScreen(navController)
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController, modifier: Modifier = Modifier) {
    val startDestination = MainNaviBar.Weather
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                windowInsets = NavigationBarDefaults.windowInsets,
                modifier = Modifier.height(50.dp)
            ) {
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
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    ) { innerPadding ->
        MainHost(
            selectedDestination,
            navController,
            Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )
    }
}

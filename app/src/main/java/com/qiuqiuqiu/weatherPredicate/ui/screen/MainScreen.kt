package com.qiuqiuqiu.weatherPredicate.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
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
import com.qiuqiuqiu.weatherPredicate.SwitchStatusBarColor
import com.qiuqiuqiu.weatherPredicate.ui.screen.map.MapScreen
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
                SwitchStatusBarColor(false)
                WeatherScreen(navController)
            }

            MainNaviBar.Map -> {
                SwitchStatusBarColor(true)
                MapScreen()
            }

            MainNaviBar.Time -> {
                SwitchStatusBarColor(true)
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

        }
    ) { innerPadding ->
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
                windowInsets = NavigationBarDefaults.windowInsets,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.975f)
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
                        colors = NavigationBarItemColors(
                            selectedIconColor = MaterialTheme.colorScheme.onTertiary,
                            unselectedIconColor = MaterialTheme.colorScheme.onTertiary.copy(
                                alpha = 0.7f
                            ),
                            selectedTextColor = MaterialTheme.colorScheme.onTertiary,
                            unselectedTextColor = MaterialTheme.colorScheme.onTertiary.copy(
                                alpha = 0.7f
                            ),
                            selectedIndicatorColor = MaterialTheme.colorScheme.tertiaryContainer,
                            disabledIconColor = MaterialTheme.colorScheme.onTertiary.copy(
                                alpha = 0.4f
                            ),
                            disabledTextColor = MaterialTheme.colorScheme.onTertiary.copy(
                                alpha = 0.4f
                            ),
                        ),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }

    }
}

package com.qiuqiuqiu.weatherPredicate.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.WeatherScreen

@Composable
fun MainScreen(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            WeatherScreen(navController)
        }
    }
}
package com.qiuqiuqiu.weatherPredicate

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qiuqiuqiu.weatherPredicate.ui.screen.MainScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.time.GlobalTimeScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.time.SolarTermScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.time.TourScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.CityEditScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.CityManageScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.WeatherCityScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.WeatherDetailScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.weather.WeatherSearchScreen

@Composable
fun MainApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = "Main",
        modifier = modifier
    )
    {
        animatedNavComposable("Main", slideSizeExcept = 3) {
            MainScreen(navController)
        }

        animatedNavComposable(
            "WeatherDetail?pageName={pageName}&pageInfo={pageInfo}&longitude={longitude}&latitude={latitude}",
            arguments = listOf()
        ) {
            val pageName = it.arguments?.getString("pageName")
            val pageInfo = it.arguments?.getString("pageInfo")
            val longitude = it.arguments?.getString("longitude")?.toDoubleOrNull() ?: 116.4074
            val latitude = it.arguments?.getString("latitude")?.toDoubleOrNull() ?: 39.9042
            WeatherDetailScreen(navController, Pair(longitude, latitude), pageName, pageInfo)
        }

        animatedNavComposable("WeatherSearch") {
            WeatherSearchScreen(navController)
        }

        animatedNavComposable(
            "CityWeather?longitude={longitude}&latitude={latitude}",
            arguments = listOf()
        ) {
            val longitude = it.arguments?.getString("longitude")?.toDoubleOrNull() ?: 116.4074
            val latitude = it.arguments?.getString("latitude")?.toDoubleOrNull() ?: 39.9042
            WeatherCityScreen(navController, Pair(longitude, latitude))
        }

        animatedNavComposable("CityManage") {
            CityManageScreen(navController)
        }

        animatedNavComposable("CityEdit") {
            CityEditScreen(navController)
        }

        animatedNavComposable("time/global") {
            GlobalTimeScreen(onBack = { navController.popBackStack() })
        }

        animatedNavComposable("time/solar") {
            SolarTermScreen(onBack = { navController.popBackStack() })
        }

        animatedNavComposable("time/city") {
            TourScreen(onBack = { navController.popBackStack() })
        }
    }
}

fun NavGraphBuilder.animatedNavComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    slideSizeExcept: Int = 1,
    direction: AnimatedContentTransitionScope.SlideDirection = AnimatedContentTransitionScope.SlideDirection.Left,
    popDirection: AnimatedContentTransitionScope.SlideDirection = AnimatedContentTransitionScope.SlideDirection.Right,
    content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit)
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = {
            slideIntoContainer(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                towards = direction,
                initialOffset = { fullSize -> fullSize / slideSizeExcept }
            )
        },
        exitTransition = {
            slideOutOfContainer(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                towards = direction,
                targetOffset = { fullSize -> fullSize / slideSizeExcept }
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                towards = popDirection,
                initialOffset = { fullSize -> fullSize / slideSizeExcept }
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                towards = popDirection,
                targetOffset = { fullSize -> fullSize / slideSizeExcept }
            )
        },
        content = content
    )
}
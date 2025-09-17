package com.qiuqiuqiu.weatherPredicate

import android.app.Activity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qiuqiuqiu.weatherPredicate.ui.screen.MainScreen
import com.qiuqiuqiu.weatherPredicate.ui.screen.map.MapSideScreen
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
            SwitchStatusBarColor(true)
            val pageName = it.arguments?.getString("pageName")
            val pageInfo = it.arguments?.getString("pageInfo")
            val longitude = it.arguments?.getString("longitude")?.toDoubleOrNull() ?: 116.4074
            val latitude = it.arguments?.getString("latitude")?.toDoubleOrNull() ?: 39.9042
            WeatherDetailScreen(navController, Pair(longitude, latitude), pageName, pageInfo)
        }

        animatedNavComposable("WeatherSearch") {
            SwitchStatusBarColor(false)
            WeatherSearchScreen(navController)
        }

        animatedNavComposable(
            "CityWeather?longitude={longitude}&latitude={latitude}",
            arguments = listOf()
        ) {
            SwitchStatusBarColor(false)
            val longitude = it.arguments?.getString("longitude")?.toDoubleOrNull() ?: 116.4074
            val latitude = it.arguments?.getString("latitude")?.toDoubleOrNull() ?: 39.9042
            WeatherCityScreen(navController, Pair(longitude, latitude))
        }

        animatedNavComposable("CityManage") {
            SwitchStatusBarColor(true)
            CityManageScreen(navController)
        }

        animatedNavComposable("CityEdit") {
            SwitchStatusBarColor(true)
            CityEditScreen(navController)
        }

        animatedNavComposable("time/global") {
            SwitchStatusBarColor(true)
            GlobalTimeScreen(onBack = { navController.popBackStack() })
        }

        animatedNavComposable("time/solar") {
            SwitchStatusBarColor(true)
            SolarTermScreen(onBack = { navController.popBackStack() })
        }

        animatedNavComposable("time/city") {
            SwitchStatusBarColor(true)
            TourScreen(onBack = { navController.popBackStack() })
        }

        animatedNavComposable(
            "SideMap?title={title}&longitude={longitude}&latitude={latitude}",
            arguments = listOf()
        ) {
            SwitchStatusBarColor(true)
            val title = it.arguments?.getString("title") ?: "位置"
            val longitude = it.arguments?.getString("longitude")?.toDoubleOrNull() ?: 116.4074
            val latitude = it.arguments?.getString("latitude")?.toDoubleOrNull() ?: 39.9042
            MapSideScreen(title, longitude, latitude, navController)
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

@Composable
fun SwitchStatusBarColor(
    darkIcons: Boolean,
) {
    val context = LocalContext.current
    val view = LocalView.current

    SideEffect {
        val window = (context as? Activity)?.window
        window?.let {
            WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = darkIcons
        }
    }
}
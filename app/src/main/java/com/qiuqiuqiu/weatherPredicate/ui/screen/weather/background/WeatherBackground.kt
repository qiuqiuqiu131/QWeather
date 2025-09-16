package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun getWeatherBackgroundType(id: String): String {
    val cloudCodes = setOf(
        "101",
        "102",
        "103",
        "104",
        "151",
        "152",
        "153",
        "500",
        "501",
        "509",
        "510",
        "514",
        "515"
    )
    val sunnyCodes = setOf("100", "150")
    val drizzleCodes = setOf("309")
    val lightRainCodes = setOf("305", "314")
    val moderateRainCodes = setOf("306", "315", "399")
    val heavyRainCodes = setOf("307", "310", "316")
    val stormRainCodes = setOf("308", "311", "317")
    val severeRainCodes = setOf("312", "318")
    val rainCodes =
        drizzleCodes + lightRainCodes + moderateRainCodes + heavyRainCodes + stormRainCodes + severeRainCodes +
                setOf("300", "301", "302", "303", "304", "313", "350", "351")
    val snowCodes = setOf(
        "400",
        "401",
        "402",
        "403",
        "404",
        "405",
        "406",
        "407",
        "408",
        "409",
        "410",
        "456",
        "457",
        "499"
    )

    return when {
        sunnyCodes.contains(id) -> "sunny"
        cloudCodes.contains(id) -> "cloudy"
        drizzleCodes.contains(id) -> "rain_drizzle"
        lightRainCodes.contains(id) -> "rain_light"
        moderateRainCodes.contains(id) -> "rain_moderate"
        heavyRainCodes.contains(id) -> "rain_heavy"
        stormRainCodes.contains(id) -> "rain_storm"
        severeRainCodes.contains(id) -> "rain_severe"
        rainCodes.contains(id) -> "rain_moderate"
        snowCodes.contains(id) -> "snow"
        else -> "cloudy"
    }
}

@Composable
fun WeatherBackground(id: String, modifier: Modifier = Modifier) {
    when (getWeatherBackgroundType(id)) {
        "sunny" -> SunnyLensFlareBackground(modifier = modifier)
        "cloudy" -> CloudyAnimationBackground(modifier = modifier)
        "rain_drizzle" -> RainyWindowBackground(type = RainType.DRIZZLE, modifier = modifier)
        "rain_light" -> RainyWindowBackground(type = RainType.LIGHT, modifier = modifier)
        "rain_moderate" -> RainyWindowBackground(type = RainType.MODERATE, modifier = modifier)
        "rain_heavy" -> RainyWindowBackground(type = RainType.HEAVY, modifier = modifier)
        "rain_storm" -> RainyWindowBackground(type = RainType.STORM, modifier = modifier)
        "rain_severe" -> RainyWindowBackground(type = RainType.SEVERE, modifier = modifier)
        "snow" -> CloudyAnimationBackground(modifier = modifier) // 可自定义雪背景
        else -> CloudyAnimationBackground(modifier = modifier)
    }
}
package com.qiuqiuqiu.weatherPredicate.ui.screen.weather.background

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.qiuqiuqiu.weatherPredicate.R

fun getWeatherBackgroundType(id: String): String {
    val cloudCodes = setOf(
        "104",
        "500",
        "501",
        "502",
        "509",
        "510",
        "511",
        "512",
        "513",
        "514",
        "515"
    )
    val sunnyCodes = setOf("100", "150")
    val sunnyCloudyCodes = setOf("101", "102", "103", "151", "152", "153")
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
        sunnyCloudyCodes.contains(id) -> "sunnyCloudy"
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
fun WeatherBackground(id: String, modifier: Modifier = Modifier, isDay: Boolean = true) {
    when (getWeatherBackgroundType(id)) {
        "sunny" -> SunnyLensFlareBackground(modifier = modifier, isCloudy = false, isDay = isDay)
        "sunnyCloudy" -> SunnyLensFlareBackground(
            modifier = modifier,
            isCloudy = true,
            isDay = isDay
        )

        "cloudy" -> CloudyAnimationBackground(modifier = modifier, isDay = isDay)
        "rain_drizzle" -> RainyWindowBackground(
            type = RainType.DRIZZLE,
            modifier = modifier,
            isDay = isDay
        )

        "rain_light" -> RainyWindowBackground(
            type = RainType.LIGHT,
            modifier = modifier,
            isDay = isDay
        )

        "rain_moderate" -> RainyWindowBackground(
            type = RainType.MODERATE,
            modifier = modifier,
            isDay = isDay
        )

        "rain_heavy" -> RainyWindowBackground(
            type = RainType.HEAVY,
            modifier = modifier,
            isDay = isDay
        )

        "rain_storm" -> RainyWindowBackground(
            type = RainType.STORM,
            modifier = modifier,
            isDay = isDay
        )

        "rain_severe" -> RainyWindowBackground(
            type = RainType.SEVERE,
            modifier = modifier,
            isDay = isDay
        )

        "snow" -> CloudyAnimationBackground(modifier = modifier, isDay = isDay) // 可自定义雪背景
        else -> CloudyAnimationBackground(modifier = modifier, isDay = isDay)
    }
}

@Composable
fun WeatherBackgroundCard(id: String, modifier: Modifier = Modifier, isDay: Boolean = true) {
    val id = when (getWeatherBackgroundType(id)) {
        "sunny" -> if (isDay) R.drawable.sunnyday else R.drawable.sunnynight
        "sunnyCloudy" -> if (isDay) R.drawable.sunnycloudyday else R.drawable.sunnycloudynight
        "cloudy" -> if (isDay) R.drawable.cloudyday else R.drawable.cloudynight
        "rain_drizzle", "rain_light", "rain_moderate", "rain_heavy", "rain_severe", "rain_storm" -> if (isDay) R.drawable.rainyday else R.drawable.rainynight
        else -> if (isDay) R.drawable.cloudyday else R.drawable.cloudynight
    }
    Image(
        painterResource(id), null, modifier = modifier,
        contentScale = androidx.compose.ui.layout.ContentScale.Crop
    )
}
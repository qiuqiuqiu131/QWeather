package com.qiuqiuqiu.weatherPredicate.viewModel.weather

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.model.StarModel
import com.qiuqiuqiu.weatherPredicate.model.weather.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.weather.DetailModel
import com.qiuqiuqiu.weatherPredicate.model.weather.LocationWeatherModel
import com.qiuqiuqiu.weatherPredicate.model.weather.TimelyChartModel
import com.qiuqiuqiu.weatherPredicate.repository.TianRepository
import com.qiuqiuqiu.weatherPredicate.service.IQWeatherService
import com.qiuqiuqiu.weatherPredicate.ui.normal.ChartPoint
import com.qiuqiuqiu.weatherPredicate.ui.screen.time.StarType
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime

val defaultPageNames = listOf("实况天气", "每日天气", "多日天气", "空气质量")

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    val locationWeatherManager: ILocationWeatherManager,
    val localDataManager: ILocalDataManager,
    val weatherService: IQWeatherService,
    val context: Context,
    val tianRepository: TianRepository
) : ViewModel() {
    var isInit = mutableStateOf(true)
        private set

    private val _locationWeather = MutableStateFlow(LocationWeatherModel())
    val locationWeather: StateFlow<LocationWeatherModel> = _locationWeather.asStateFlow()

    private val _detailModel = MutableStateFlow(DetailModel())
    val detailModel: StateFlow<DetailModel> = _detailModel.asStateFlow()


    var pageItems: MutableState<List<String>> = mutableStateOf(emptyList())
        private set

    // 页面索引
    var pageIndex: MutableIntState = mutableIntStateOf(0)
        private set

    private lateinit var cityList: List<CityLocationModel>

    @SuppressLint("NewApi")
    fun initWeatherData(city: CityLocationModel, pageName: String? = null) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            Log.e("Weather", "获取天气失败: ${e.stackTrace}")
        } + Dispatchers.IO) {
            cityList = localDataManager.getCityList()

            val result = locationWeatherManager.getCacheLocationWeather(city)

            result.first.apply {
                // 获取拓展天气数据
                val weatherDailiesTask =
                    async { weatherService.getWeatherMoreDay(location!!.id) }
                val weatherHourliesTask =
                    async { weatherService.getWeather168Hour(location!!.id) }
                val indicesDailiesTask = async {
                    weatherService.getWeatherIndices3Day(location!!.id).groupBy { it.name }
                        .map { Pair(it.key, it.value.sortedBy { va -> va.date }) }
                }
                val airHourliesTask = async {
                    weatherService.getAirHourly(location!!.lon.toDouble(), location.lat.toDouble())
                }

                weatherDailiesMore = weatherDailiesTask.await()
                weatherHourliesMore = weatherHourliesTask.await()
                indicesDailiesMore = indicesDailiesTask.await()
                airHourlies = airHourliesTask.await()
            }

            _locationWeather.update { result.first }

            // 更新日期列表
            dates.value =
                result.first.weatherHourliesMore?.groupBy {
                    OffsetDateTime.parse(it.fxTime).toLocalDate()
                }
                    ?.map { it.key }

            // 更新页列表
            pageItems.value =
                defaultPageNames + (result.first.indicesDailies?.map { it.name.replace("指数", "") }
                    ?: emptyList())
            if (pageName != null && isInit.value)
                pageIndex.intValue = pageItems.value.indexOf(pageName).let { if (it < 0) 0 else it }

            chartModelCache.clear()
            switchChartType(selectedHourlyType.value)

            // 获取星座运势
            SwitchStar(StarType.Aries)

            isInit.value = false
        }
    }

    fun SwitchStar(type: StarType) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            Log.e("Weather", "获取星座运势失败: ${e.stackTrace}")
        }) {
            val result = tianRepository.getDailyFortune(type.label).result
            _detailModel.update { it ->
                it.copy(star = StarModel(type.text, result))
            }
        }
    }

    val hourlyTypes =
        HourlyDetailType.entries.filterNot { detailType -> detailType == HourlyDetailType.Air }

    var selectedHourlyType = mutableStateOf(hourlyTypes[0])
        private set

    // 每日天气页选中日期索引
    var selectedDate = mutableIntStateOf(0)
        private set

    // 每日天气页日期列表
    var dates: MutableState<List<LocalDate>?> = mutableStateOf(null)
        private set

    // 每日天气页图表数据
    var chartModel: MutableStateFlow<TimelyChartModel?> = MutableStateFlow(null)
        private set

    // 每日天气页选中时间点索引
    var selectedEntry = mutableIntStateOf(0)
        private set

    private val chartModelCache: MutableMap<HourlyDetailType, TimelyChartModel?> = mutableMapOf()

    fun moveToHourlyPage(type: HourlyDetailType, date: LocalDate) {
        onSwitchChartType(type.intValue)
        onDateChanged(dates.value?.indexOfFirst { it == date } ?: 0)
    }

    fun onSwitchChartType(index: Int) {
        val type = hourlyTypes.firstOrNull { it.intValue == index } ?: return
        selectedHourlyType.value = type
        switchChartType(type)
    }

    @SuppressLint("NewApi")
    private fun switchChartType(type: HourlyDetailType) {
        viewModelScope.launch(Dispatchers.Default) {
            chartModel.update { getTimelyChartModel(type) }
        }
    }

    // 图标焦点变化时调用
    @SuppressLint("NewApi")
    fun onEntryChanged(entry: Int) {
        if (entry == selectedEntry.intValue) return
        selectedEntry.intValue = entry
        chartModel.value?.data1[entry]?.time?.toLocalDate()?.let { localDate ->
            val index = dates.value?.indexOfFirst { date -> date == localDate }
            if (index != null && index >= 0)
                selectedDate.intValue = index
        }
    }

    // 点击日期时调用
    @SuppressLint("NewApi")
    fun onDateChanged(index: Int) {
        selectedDate.intValue = index
        val localDate = dates.value?.get(index)
        val entryIndex =
            chartModel.value?.data1?.let { dt ->
                dt.indexOfFirst { it.time.toLocalDate() == localDate && it.time.hour == 0 }
                    .let { res ->
                        if (res >= 0) res else dt.indexOfFirst { it.time.toLocalDate() == localDate } + 1
                    }
            }
        if (entryIndex != null && entryIndex >= 0)
            selectedEntry.intValue = entryIndex
    }

    @SuppressLint("NewApi")
    fun getTimelyChartModel(type: HourlyDetailType): TimelyChartModel {
        if (chartModelCache[type] == null) {
            val data = when (type) {
                HourlyDetailType.Pressure -> {
                    TimelyChartModel(
                        _locationWeather.value.weatherHourliesMore?.map {
                            ChartPoint(
                                OffsetDateTime.parse(it.fxTime),
                                getWeatherIconBitmap(
                                    it.icon,
                                    context
                                ),
                                it.pressure.toFloat()
                            )
                        } ?: emptyList(),
                        "气压",
                        type = type
                    )
                }

                HourlyDetailType.Wind -> {
                    TimelyChartModel(
                        _locationWeather.value.weatherHourliesMore?.map {
                            ChartPoint(
                                OffsetDateTime.parse(it.fxTime),
                                getWeatherIconBitmap(
                                    it.icon,
                                    context
                                ),
                                if (it.windScale.contains("-"))
                                    it.windScale.split("-").mapNotNull { s -> s.toIntOrNull() }
                                        .average()
                                        .toFloat()
                                else
                                    it.windScale.toFloatOrNull() ?: 0f
                            )
                        } ?: emptyList(),
                        "平均风力",
                        _locationWeather.value.weatherHourliesMore?.map {
                            ChartPoint(
                                OffsetDateTime.parse(it.fxTime),
                                getWeatherIconBitmap(
                                    it.icon,
                                    context
                                ),
                                if (it.windScale.contains("-"))
                                    it.windScale.split("-").last().toFloat()
                                else
                                    it.windScale.toFloatOrNull() ?: 0f
                            )
                        } ?: emptyList(),
                        "阵风",
                        type = type
                    )
                }

                HourlyDetailType.Temp -> {
                    TimelyChartModel(
                        _locationWeather.value.weatherHourliesMore?.map {
                            ChartPoint(
                                OffsetDateTime.parse(it.fxTime),
                                getWeatherIconBitmap(
                                    it.icon,
                                    context
                                ),
                                it.temp.toFloat()
                            )
                        } ?: emptyList(),
                        "温度",
                        type = type
                    )
                }

                HourlyDetailType.Hum -> {
                    TimelyChartModel(
                        _locationWeather.value.weatherHourliesMore?.map {
                            ChartPoint(
                                OffsetDateTime.parse(it.fxTime),
                                getWeatherIconBitmap(
                                    it.icon,
                                    context
                                ),
                                it.humidity.toFloat()
                            )
                        } ?: emptyList(),
                        "湿度",
                        type = type
                    )
                }

                HourlyDetailType.Pop -> {
                    TimelyChartModel(
                        _locationWeather.value.weatherHourliesMore?.map {
                            ChartPoint(
                                OffsetDateTime.parse(it.fxTime),
                                getWeatherIconBitmap(
                                    it.icon,
                                    context
                                ),
                                it.pop.toFloat()
                            )
                        } ?: emptyList(),
                        "降水概率",
                        type = type
                    )
                }

                HourlyDetailType.Cloud -> {
                    TimelyChartModel(
                        _locationWeather.value.weatherHourliesMore?.map {
                            ChartPoint(
                                OffsetDateTime.parse(it.fxTime),
                                getWeatherIconBitmap(
                                    it.icon,
                                    context
                                ),
                                it.cloud.toFloat()
                            )
                        } ?: emptyList(),
                        "云量",
                        type = type
                    )
                }

                else -> {
                    TimelyChartModel(emptyList(), "", type = type)
                }
            }
            chartModelCache[type] = data
        }
        return chartModelCache[type]!!
    }
}

fun getWeatherIconBitmap(icon: String, context: Context): Bitmap {
    return Glide.with(context)
        .asBitmap()
        .load("https://a.hecdn.net/img/common/icon/202106d/$icon.png")
        .submit(48, 48)
        .get()
}

enum class HourlyDetailType(
    val intValue: Int,
    val text: String,
    val primaryColor: Int,
    val secondaryColor: Int,
    val endColor: Int,
    val yAxisUnit: String = "",
    val icon: ImageVector,
) {
    Temp(
        0,
        "综合指数",
        0xFFFF9800.toInt(),
        0xAAFF9800.toInt(),
        0x20FF9800.toInt(),
        "℃",
        Icons.Default.LibraryBooks
    ),
    Pop(
        1,
        "降水概率",
        0xFF61D4FA.toInt(),
        0xAA61D4FA.toInt(),
        0x2061D4FA.toInt(),
        "%",
        Icons.Default.WaterDrop
    ),
    Wind(
        2,
        "风力风向",
        0xFF9C27B0.toInt(),
        0xAA9C50B0.toInt(),
        0x209C50B0.toInt(),
        "级",
        Icons.Default.Air
    ),
    Hum(
        3,
        "相对湿度",
        0xFF1976D2.toInt(),
        0xAA1976D2.toInt(),
        0x201976D2.toInt(),
        "%",
        Icons.Default.DeviceThermostat
    ),
    Cloud(
        4,
        "云量",
        0xFF4CAF50.toInt(),
        0xAA4CAF50.toInt(),
        0x204CAF50.toInt(),
        "%",
        Icons.Default.WbCloudy
    ),
    Pressure(
        5,
        "大气压强",
        0xFF2196F3.toInt(),
        0xAA2196F3.toInt(),
        0x202196F3.toInt(), "",
        Icons.Default.Speed
    ),
    Air(
        6,
        "空气质量",
        0xFF4CAF50.toInt(),
        0xAA4CAF50.toInt(),
        0x204CAF50.toInt(), "",
        Icons.Default.Air
    )
}

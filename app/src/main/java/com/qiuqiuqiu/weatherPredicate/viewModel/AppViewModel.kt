package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.model.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.CityType
import com.qiuqiuqiu.weatherPredicate.service.ILocationService
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.defaultLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

@HiltViewModel
class AppViewModel @Inject constructor(
    val localDataManager: ILocalDataManager,
    val locationService: ILocationService
) : ViewModel() {
    var naviBarIconColor: MutableState<Color?> = mutableStateOf(null)
    var naviBarIndicatorColor: MutableState<Color?> = mutableStateOf(null)
    var naviBarContainerColor: MutableState<Color?> = mutableStateOf(null)

    fun clearNaviBarColor() {
        naviBarIconColor.value = null
        naviBarContainerColor.value = null
        naviBarIndicatorColor.value = null
    }

    var currentCity: MutableStateFlow<CityLocationModel> = MutableStateFlow(defaultLocation)
        private set

    init {
        runBlocking {
            val cityList = localDataManager.getCityList()
            currentCity.update {
                cityList.firstOrNull { it.type == CityType.Host } ?: cityList.firstOrNull()
                ?: defaultLocation
            }
        }
    }

    var currentBg: MutableState<String?> = mutableStateOf(null)

    fun setCurrentCity(location: CityLocationModel) {
        currentCity.value = location
    }

    fun addCity(location: Pair<Double, Double>) {
        runBlocking {
            localDataManager.addCity(location)
            currentCity.value = CityLocationModel(CityType.Normal, location)
        }
    }
}
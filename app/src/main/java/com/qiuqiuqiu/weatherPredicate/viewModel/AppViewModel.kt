package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.model.JieQiResult
import com.qiuqiuqiu.weatherPredicate.model.LunarResult
import com.qiuqiuqiu.weatherPredicate.model.weather.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.weather.CityType
import com.qiuqiuqiu.weatherPredicate.repository.TianRepository
import com.qiuqiuqiu.weatherPredicate.service.ILocationService
import com.qiuqiuqiu.weatherPredicate.viewModel.weather.defaultLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class AppViewModel @Inject constructor(
    val localDataManager: ILocalDataManager,
    val repository: TianRepository,
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

    // 黄历
    var lunar: MutableState<LunarResult?> = mutableStateOf(null)
    var jieqi: MutableState<JieQiResult?> = mutableStateOf(null)

    var currentCity: MutableStateFlow<CityLocationModel> = MutableStateFlow(defaultLocation)
        private set

    init {
        runBlocking {
            val cityList = localDataManager.getCityList()
            currentCity.update {
                cityList.firstOrNull { it.type == CityType.Host } ?: cityList.firstOrNull()
                ?: defaultLocation
            }
            updateLunar()
        }
    }

    fun updateLunar() {
        viewModelScope.launch(Dispatchers.IO) {
            lunar.value = repository.getLunar().result
            lunar.value?.let {
                if (!it.jieqi.isNullOrEmpty()) {
                    jieqi.value = repository.getJieQi(it.jieqi, it.lunardate.substring(0, 3)).result
                } else {
                    jieqi.value = repository.getJieQi("寒露", it.lunardate.substring(0, 3)).result
                }
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
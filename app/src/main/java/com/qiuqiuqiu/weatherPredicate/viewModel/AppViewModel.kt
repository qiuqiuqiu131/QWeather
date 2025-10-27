package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.model.JieQiResult
import com.qiuqiuqiu.weatherPredicate.model.LunarResult
import com.qiuqiuqiu.weatherPredicate.model.weather.CityType
import com.qiuqiuqiu.weatherPredicate.repository.TianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class AppViewModel @Inject constructor(
    val localDataManager: ILocalDataManager,
    val repository: TianRepository
) : ViewModel() {
    // 黄历
    var lunar: MutableState<LunarResult?> = mutableStateOf(null)
    var jieqi: MutableState<JieQiResult?> = mutableStateOf(null)

    var currentIndex: MutableIntState = mutableIntStateOf(0)
        private set

    init {
        updateLunar()
        runBlocking {
            val cityList = localDataManager.getCityList()
            var index = cityList.indexOfFirst { it.type == CityType.Host }
            if (index == -1) index = 0
            currentIndex.intValue = index
        }

    }

    fun updateLunar() {
        viewModelScope.launch(Dispatchers.IO) {
            lunar.value = repository.getLunar().result
            lunar.value?.let {
                if (!it.jieqi.isNullOrEmpty()) {
                    jieqi.value = repository.getJieQi(it.jieqi, it.lunardate.substring(0, 3)).result
                } else {
                    // jieqi.value = repository.getJieQi("寒露", it.lunardate.substring(0, 3)).result
                }
            }
            if (jieqi.value == null) {
                // jieqi.value = repository.getJieQi("寒露", "2025").result
            }
        }
    }

    var currentBg: MutableState<String?> = mutableStateOf(null)

    fun setCurrentCity(index: Int) {
        currentIndex.intValue = index
    }

    fun addCity(location: Pair<Double, Double>) {
        runBlocking {
            localDataManager.addCity(location)
            val cityList = localDataManager.getCityList()
            currentIndex.intValue = if (cityList.isEmpty()) 0 else cityList.size - 1
        }
    }
}
package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.model.LocationWeatherModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@HiltViewModel
class CityManageViewModel @Inject constructor(
    val localDataManager: ILocalDataManager,
    val locationWeatherManager: ILocationWeatherManager
) : ViewModel() {
    var isInit: MutableState<Boolean> = mutableStateOf(true)
        private set

    var cityList: MutableState<List<LocationWeatherModel>> = mutableStateOf(emptyList())
        private set

    fun refreshCities() {
        isInit.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val list = localDataManager.getCityList()
            val weatherTaskList = list.map {
                async {
                    locationWeatherManager.getCacheLocationWeather(it).first
                }
            }
            cityList.value = weatherTaskList.map { it.await() }
            isInit.value = false
        }
    }
}
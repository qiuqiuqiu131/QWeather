package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.manager.ILocationWeatherManager
import com.qiuqiuqiu.weatherPredicate.model.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.CityType
import com.qiuqiuqiu.weatherPredicate.model.LocationWeatherModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@HiltViewModel
class CityEditViewModel
@Inject
constructor(
    val localDataManager: ILocalDataManager,
    val locationWeatherManager: ILocationWeatherManager
) : ViewModel() {
    var isInit: MutableState<Boolean> = mutableStateOf(true)
        private set

    var cityList = mutableStateListOf<LocationWeatherModel>()
        private set

    fun refreshCities() {
        cityList.clear()
        viewModelScope.launch(Dispatchers.IO) {
            val list = localDataManager.getCityList()
            val weatherTaskList =
                list.map { async { locationWeatherManager.getCacheLocationWeather(it).first } }
            cityList.addAll(weatherTaskList.map { it.await() })
            isInit.value = false
        }
    }

    fun removeCity(city: LocationWeatherModel) {
        cityList.remove(city)
    }

    fun setHomeCity(city: LocationWeatherModel) {
        if (city.type == CityType.Host) {
            val newHostIndex = cityList.indexOf(city)
            if (newHostIndex != -1)
                cityList[newHostIndex] = cityList[newHostIndex].copy(type = CityType.Normal)
            return
        }

        val oldHostIndex = cityList.indexOfFirst { it.type == CityType.Host }
        if (oldHostIndex != -1)
            cityList[oldHostIndex] = cityList[oldHostIndex].copy(type = CityType.Normal)

        val newHostIndex = cityList.indexOf(city)
        if (newHostIndex != -1)
            cityList[newHostIndex] = cityList[newHostIndex].copy(type = CityType.Host)
    }

    fun saveEdit(callBack: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            localDataManager.saveCityList(
                cityList.map {
                    CityLocationModel(
                        it.type,
                        Pair(it.location!!.lon.toDouble(), it.location.lat.toDouble())
                    )
                }
            )
            viewModelScope.launch(Dispatchers.Main) { callBack?.invoke() }
        }
    }
}

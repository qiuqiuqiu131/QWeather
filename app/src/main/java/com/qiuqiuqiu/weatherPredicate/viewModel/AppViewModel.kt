package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.lifecycle.ViewModel
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import com.qiuqiuqiu.weatherPredicate.model.CityLocationModel
import com.qiuqiuqiu.weatherPredicate.model.CityType
import com.qiuqiuqiu.weatherPredicate.service.ILocationService
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
    var currentCity: MutableStateFlow<CityLocationModel?> = MutableStateFlow(null)
        private set

    init {
        runBlocking {
            val cityList = localDataManager.getCityList()
            currentCity.update {
                cityList.firstOrNull { it.type == CityType.Host } ?: cityList.firstOrNull()
            }
        }
    }

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
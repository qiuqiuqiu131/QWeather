package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.manager.ILocalDataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    val localDataManager: ILocalDataManager
) : ViewModel() {
    var currentCity: MutableState<Pair<Double, Double>?> = mutableStateOf(null)
        private set

    fun setCurrentCity(location: Pair<Double, Double>) {
        currentCity.value = location
    }

    fun addCity(location: Pair<Double, Double>) {
        currentCity.value = location
        viewModelScope.launch {
            localDataManager.addCity(location)
        }
    }
}
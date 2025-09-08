package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {
    var currentCity: MutableState<Pair<Double, Double>?> = mutableStateOf(null)
        private set

    fun setCurrentCity(location: Pair<Double, Double>) {
        currentCity.value = location
    }
}
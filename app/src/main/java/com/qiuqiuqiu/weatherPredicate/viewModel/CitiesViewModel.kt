package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.model.TimeResponse
import com.qiuqiuqiu.weatherPredicate.repository.TianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject




// ------- ViewModel ---------
@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val repository: TianRepository
) : ViewModel() {

    private val _cityResult = MutableStateFlow<TimeResponse?>(null)
    val cityResult: StateFlow<TimeResponse?> = _cityResult

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchCityTime(city: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getWorldTime(city)
                if (response.code == 200) {
                    _cityResult.value = response
                } else {
                    _error.value = response.msg
                    _cityResult.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
                _cityResult.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}


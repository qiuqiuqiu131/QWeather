package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.model.JieQiResponse
import com.qiuqiuqiu.weatherPredicate.service.ApiKeyProvider
import com.qiuqiuqiu.weatherPredicate.network.TianApiCities
import com.qiuqiuqiu.weatherPredicate.repository.JieQiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JieQiViewModel @Inject constructor(
    private val repository: JieQiRepository
) : ViewModel() {

    private val _jieQiResult = MutableStateFlow<JieQiResponse?>(null)
    val jieQiResult: StateFlow<JieQiResponse?> = _jieQiResult

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchJieQi(word: String, year: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getJieQi(word, year)
                if (response != null && response.code == 200) {
                    _jieQiResult.value = response
                } else {
                    _error.value = response?.msg ?: "未知错误"
                    _jieQiResult.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
                _jieQiResult.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}

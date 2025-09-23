package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.model.ShiJuResponse
import com.qiuqiuqiu.weatherPredicate.repository.TianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShiJuViewModel @Inject constructor(
    private val repository: TianRepository
) : ViewModel() {

    private val _shiJuResult = MutableStateFlow<ShiJuResponse?>(null)
    val shiJuResult: StateFlow<ShiJuResponse?> = _shiJuResult

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * 查询诗句
     * @param tqtype 类型：1=风、2=云、3=雨、4=雪、5=霜、6=露 、7=雾、8=雷、9=晴、10=阴
     */
    fun fetchShiJu(tqtype: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getShiJu(tqtype) // key 已在 Repository 中处理
                if (response.code == 200) {
                    _shiJuResult.value = response
                } else {
                    _error.value = response?.msg ?: "未知错误"
                    _shiJuResult.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
                _shiJuResult.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}
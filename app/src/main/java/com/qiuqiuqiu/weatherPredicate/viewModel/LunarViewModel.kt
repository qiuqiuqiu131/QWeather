package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.model.LunarResponse
import com.qiuqiuqiu.weatherPredicate.repository.LunarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LunarViewModel @Inject constructor(
    private val repository: LunarRepository
) : ViewModel() {

    private val _lunarResult = MutableStateFlow<LunarResponse?>(null)
    val lunarResult: StateFlow<LunarResponse?> = _lunarResult

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    /**
     * 查询农历信息
     *
     * @param date 可选，查询日期，格式为 "YYYY-MM-DD"，为空则默认查询当天
     * @param type 可选，查询类型：
     *             0 = 按公历查询
     *             1 = 按农历查询（此时 date 不能有前导零）
     */
    fun fetchLunar(date: String? = null, type: Int? = 0) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getLunar(date, type)
                if (response != null && response.code == 200) {
                    _lunarResult.value = response
                } else {
                    _error.value = response?.msg ?: "未知错误"
                    _lunarResult.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
                _lunarResult.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}

package com.qiuqiuqiu.weatherPredicate.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.model.DailyFortuneResponse
import com.qiuqiuqiu.weatherPredicate.repository.TianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StarViewModel @Inject constructor(
    private val repository: TianRepository
) : ViewModel() {

    private val _fortuneResult = MutableStateFlow<DailyFortuneResponse?>(null)
    val fortuneResult: StateFlow<DailyFortuneResponse?> = _fortuneResult
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * 查询星座每日运势
     * @param astro 星座中文或英文
     * @param date 日期
     */
    fun fetchDailyFortune(astro: String, date: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getDailyFortune(astro,date)
                if (response != null && response.code == 200) {
                    _fortuneResult.value = response
                } else {
                    _error.value = response?.msg ?: "未知错误"
                    _fortuneResult.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
                _fortuneResult.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}

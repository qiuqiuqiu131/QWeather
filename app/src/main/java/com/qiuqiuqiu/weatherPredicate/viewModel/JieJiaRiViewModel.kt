package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.model.JieJiaRiResponse
import com.qiuqiuqiu.weatherPredicate.repository.TianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class JieJiaRiViewModel @Inject constructor(
    private val repository: TianRepository
) : ViewModel() {

    private val _holidayResult = MutableStateFlow<JieJiaRiResponse?>(null)
    val holidayResult: StateFlow<JieJiaRiResponse?> = _holidayResult

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * 节假日查询
     * @param date 按年查询（type=1&date=2020）、按月查询（type=2&date=2020-10）、按日期范围查询（type=3&date=2020-11-1~2020-11-10）、按多个日期批量查询（date=2020-10-1,2020-11-12）
     * @param type 类型：0批量、1按年、2按月、3范围
     * @return daycode表示日期类型，0表示工作日、1节假日、2双休日、3调休日（需上班）。判断是否需要上班建议用isnotwork字段，其中值为0表示上班，为1表示休息。wage表示薪资倍数，周末为两倍，法定节假日当天为三倍其他两倍（按年查询时返回三倍薪资的具体日期）。
     *
     */
    fun fetchHoliday(date: String, type: Int = 1) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getJieJiaRi(date, type)
                if (response.code == 200) {
                    _holidayResult.value = response
                } else {
                    _error.value = response?.msg ?: "未知错误"
                    _holidayResult.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
                _holidayResult.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}

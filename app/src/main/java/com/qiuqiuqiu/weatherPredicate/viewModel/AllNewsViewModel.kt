package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.model.AllNewsResponse
import com.qiuqiuqiu.weatherPredicate.repository.TianRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllNewsViewModel @Inject constructor(
    private val repository: TianRepository
) : ViewModel() {

    private val _newsResult = MutableStateFlow<AllNewsResponse?>(null)
    val newsResult: StateFlow<AllNewsResponse?> = _newsResult

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * 获取新闻大全
     *
     * @param num 返回数量，1-50，默认10
     * @param col 新闻频道 ID（必填）
     * @param page 翻页，可选
     * @param rand 随机获取，0=不随机，1=随机
     * @param word 检索关键词，可选
     */
    fun fetchAllNews(num: Int = 10, col: Int, page: Int? = 1, rand: Int? = 1, word: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getAllNews(num, col, page, rand, word)
                if (response.code == 200) {
                    _newsResult.value = response
                } else {
                    _error.value = response.msg
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}

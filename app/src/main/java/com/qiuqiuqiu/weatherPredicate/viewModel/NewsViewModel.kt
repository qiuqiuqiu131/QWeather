package com.qiuqiuqiu.weatherPredicate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiuqiuqiu.weatherPredicate.model.NewsResponse
import com.qiuqiuqiu.weatherPredicate.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 新闻 ViewModel
 */
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _newsResult = MutableStateFlow<NewsResponse?>(null)
    val newsResult: StateFlow<NewsResponse?> = _newsResult

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * 获取新闻
     *
     * @param num 返回数量，1~50，默认10
     * @param page 翻页，默认0
     * @param rand 是否随机获取，0=不随机，1=随机
     * @param word 搜索关键词，可选
     * @param source 指定来源，可选
     */
    fun fetchNews(
        num: Int? = 10,
        page: Int? = 0,
        rand: Int? = 0,
        word: String? = null,
        source: String? = null
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getNews(num, page, rand, word, source)
                if (response.code == 200) {
                    _newsResult.value = response
                } else {
                    _error.value = response.msg
                    _newsResult.value = null
                }
            } catch (e: Exception) {
                _error.value = e.message
                _newsResult.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}

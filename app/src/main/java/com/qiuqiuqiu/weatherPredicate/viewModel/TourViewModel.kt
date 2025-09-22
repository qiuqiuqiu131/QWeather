//package com.qiuqiuqiu.weatherPredicate.viewModel
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.qiuqiuqiu.weatherPredicate.model.TouristSpotItem
//import com.qiuqiuqiu.weatherPredicate.repository.TourRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//enum class TourSearchType {
//    NAME, PROVINCE, CITY
//}
//
//@HiltViewModel
//class TourViewModel @Inject constructor(
//    private val repository: TourRepository
//) : ViewModel() {
//
//    private val TAG = "TourViewModel"
//
//    private val _spots = MutableStateFlow<List<TouristSpotItem>>(emptyList())
//    val spots: StateFlow<List<TouristSpotItem>> = _spots
//
//    private val _loading = MutableStateFlow(false)
//    val loading: StateFlow<Boolean> = _loading
//
//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error
//
//    private var currentPage = 1
//    private var currentType: TourSearchType = TourSearchType.NAME
//    private var currentQuery: String = ""
//
//    /**
//     * 按类型发起请求（只传对应参数，Repository 会把其它参数设为 null）
//     */
//    fun fetchTourSpotsByType(type: TourSearchType, query: String, num: Int = 10, resetPage: Boolean = true) {
//        val q = query.trim()
//        if (resetPage) currentPage = 1
//        currentType = type
//        currentQuery = q
//
//        Log.d(TAG, "fetchTourSpotsByType -> type=$type, query='$q', page=$currentPage")
//
//        viewModelScope.launch {
//            _loading.value = true
//            _error.value = null
//            try {
//                val resp = repository.getTourSpotByType(type, q, num, page = currentPage.toString())
//                if (resp == null) {
//                    _error.value = "请求失败或参数不正确"
//                    _spots.value = emptyList()
//                } else if (resp.code == 200 && resp.result.list.isNotEmpty()) {
//                    _spots.value = resp.result.list
//                } else if (resp.code == 200) {
//                    _error.value = "没有查询到数据"
//                    _spots.value = emptyList()
//                } else {
//                    _error.value = resp.msg
//                    _spots.value = emptyList()
//                }
//            } catch (e: Exception) {
//                _error.value = e.message ?: "未知异常"
//                _spots.value = emptyList()
//            } finally {
//                _loading.value = false
//            }
//        }
//    }
//
//    fun loadNextPageUsingCurrent(num: Int = 10) {
//        currentPage++
//       // Log.d(TAG, "loadNextPageUsingCurrent -> page=$currentPage, type=$currentType, query='$currentQuery'")
//        fetchTourSpotsByType(currentType, currentQuery, num, resetPage = false)
//    }
//
//    fun loadPreviousPageUsingCurrent(num: Int = 10) {
//        if (currentPage > 1) {
//            currentPage--
//          //  Log.d(TAG, "loadPreviousPageUsingCurrent -> page=$currentPage, type=$currentType, query='$currentQuery'")
//            fetchTourSpotsByType(currentType, currentQuery, num, resetPage = false)
//        }
//    }
//
//    fun currentPageNumber() = currentPage
//    fun currentSearchType() = currentType
//    fun currentSearchQuery() = currentQuery
//}
//
//
//
//

package com.kt.apps.xembongda.ui.highlight

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.model.highlights.HighLightDetail
import com.kt.apps.xembongda.usecase.highlights.GetAllHighLights
import com.kt.apps.xembongda.usecase.highlights.GetHighLightsDetail
import javax.inject.Inject
import kotlin.random.Random

data class HighlightInteractors @Inject constructor(
    val getALlHighlight: GetAllHighLights,
    val getHighLightDetail: GetHighLightsDetail
)

class FragmentHighlightViewModel @Inject constructor(
    private val interactors: HighlightInteractors
) : BaseViewModel() {

    var selectedHighLight: HighLightDTO? = null

    private var _currentPage: Int = 1
    private val _cacheList by lazy {
        mutableListOf<ItemHighLights>()
    }

    private val _highLightLiveData by lazy {
        MutableLiveData<DataState<List<ItemHighLights>>>()
    }
    val currentPage: Int
        get() = _currentPage

    val highlightLiveData: LiveData<DataState<List<ItemHighLights>>>
        get() = _highLightLiveData

    fun getHighlight(page: Int = ++_currentPage) {
        Log.e("TAG", "$currentPage")
        if (_highLightLiveData.value is DataState.Loading) return
        _highLightLiveData.postValue(DataState.Loading())
        add(
            interactors.getALlHighlight(page)
                .map { list ->
                    list.map {
                        ItemHighLights.DTO(it)
                    }
                }
                .map { oldList ->
                    val newList = mutableListOf<ItemHighLights>().apply {
                        this.addAll(oldList)
                    }
                    val itemsAds = oldList.size % 5
                    var currentIndex = 0
                    for (i in 1..itemsAds) {
                        try {
                            val nextIndex = Random.nextInt(currentIndex, (i) * 5)
                            newList.add(nextIndex, ItemHighLights.Ads())
                            currentIndex += nextIndex
                        } catch (_: Exception) {
                        }
                    }
                    newList
                }
                .subscribe({
                    _highLightLiveData.postValue(DataState.Success(it))
                    _cacheList.addAll(it)
                }, {
                    _currentPage--
                    _highLightLiveData.postValue(DataState.Error(it))
                })
        )
    }

    private val _highlightDetail by lazy {
        MutableLiveData<DataState<HighLightDetail>>()
    }
    val highLightDetail: LiveData<DataState<HighLightDetail>>
        get() = _highlightDetail

    fun getHighLightDetail(highLightDTO: HighLightDTO) {
        selectedHighLight = highLightDTO
        _highlightDetail.postValue(DataState.Loading())
        add(
            interactors.getHighLightDetail(highLightDTO)
                .subscribe({
                    _highlightDetail.postValue(DataState.Success(it))
                }, {
                    _highlightDetail.postValue(DataState.Error(it))
                })
        )
    }
}
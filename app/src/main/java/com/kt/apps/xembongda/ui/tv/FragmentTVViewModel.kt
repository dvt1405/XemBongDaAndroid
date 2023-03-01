package com.kt.apps.xembongda.ui.tv

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.LinkStreamWithReferer
import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.model.tv.ChannelTvWithM3u8
import com.kt.apps.xembongda.model.tv.KenhTvDetail
import com.kt.apps.xembongda.ui.highlight.HighlightInteractors
import com.kt.apps.xembongda.usecase.tv.GetListTVDetail
import com.kt.apps.xembongda.usecase.tv.GetTVDetail
import javax.inject.Inject


class TVInteractors @Inject constructor(
    val getTVDetail: GetTVDetail,
    val getListTVDetail: GetListTVDetail
)

class FragmentTVViewModel @Inject constructor(
    private val interactors: TVInteractors
) : BaseViewModel() {

    private val _listChannel by lazy {
        MutableLiveData<DataState<List<KenhTvDetail>>>()
    }
    val listChannel: LiveData<DataState<List<KenhTvDetail>>>
        get() = _listChannel
    private val _selectedChannel by lazy {
        MutableLiveData<DataState<List<LinkStreamWithReferer>>>()
    }
    val selectedChannel: LiveData<DataState<List<LinkStreamWithReferer>>>
        get() = _selectedChannel

    fun getAll() {
        _listChannel.postValue(DataState.Loading())
        add(
            interactors.getListTVDetail()
                .subscribe({
                    _listChannel.postValue(DataState.Success(it))
                }, {
                    _listChannel.postValue(DataState.Error(it))
                })
        )
    }

    var selectedTV: KenhTvDetail? = null

    fun getDetail(kenhTvDetail: KenhTvDetail) {
        this.selectedTV = kenhTvDetail
        _selectedChannel.postValue(DataState.Loading())
        add(
            interactors.getTVDetail(kenhTvDetail)
                .subscribe({
                    _selectedChannel.postValue(DataState.Success(it))
                }, {
                    _selectedChannel.postValue(DataState.Error(it))
                })
        )
    }
}
package com.kt.apps.xembongda.ui.listmatch

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.usecase.GetListFootballMatch
import javax.inject.Inject

data class ListMatchInteractors @Inject constructor(
    val getListFootballMatch: GetListFootballMatch
)

class FragmentListMatchViewModel @Inject constructor(
    private val interactors: ListMatchInteractors
) : BaseViewModel() {
    private val _listMatch by lazy { MutableLiveData<DataState<List<FootballMatch>>>() }
    val listMatch: LiveData<DataState<List<FootballMatch>>>
        get() = _listMatch

    fun getListFootBallMatch(sourceFrom: FootballRepoSourceFrom) {
        _listMatch.postValue(DataState.Loading())
        add(
            interactors.getListFootballMatch(sourceFrom)
                .subscribe({
                    _listMatch.postValue(DataState.Success(it))
                }, {
                    _listMatch.postValue(DataState.Error(it))
                })
        )
    }

    fun getListFootballMatchFromHtml(sourceFrom: FootballRepoSourceFrom, html: String) {
        _listMatch.postValue(DataState.Loading())
        add(
            interactors.getListFootballMatch(sourceFrom, html)
                .subscribe({
                    _listMatch.postValue(DataState.Success(it))
                }, {
                    _listMatch.postValue(DataState.Error(it))
                })
        )
    }
}
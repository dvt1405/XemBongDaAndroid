package com.kt.apps.xembongda.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.usecase.GetLinkStreamForMatch
import com.kt.apps.xembongda.usecase.GetListFootballMatch
import javax.inject.Inject
import javax.inject.Provider


data class MainInteractors @Inject constructor(
    val getListFootballMatch: GetListFootballMatch,
    val getLinkStreamForMatch: GetLinkStreamForMatch
)

class MainViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }

        if (creator == null) {
            throw IllegalStateException("No view model factory found: $modelClass")
        }

        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (_: Exception) {
            throw RuntimeException()
        }
    }
}

class MainViewModel @Inject constructor(private val interactors: MainInteractors) :
    BaseViewModel() {

    private val _listMatch by lazy { MutableLiveData<DataState<List<FootballMatch>>>() }
    val listMatch: LiveData<DataState<List<FootballMatch>>>
        get() = _listMatch
    val isLoading: MediatorLiveData<Boolean> by lazy { MediatorLiveData() }

    init {
        Log.e("TAG", "Init")
        isLoading.addSource(listMatch) {
            isLoading.postValue(it is DataState.Loading)
        }

    }

    fun getListFootBallMatch(sourceFrom: FootballRepoSourceFrom) {
        _listMatch.postValue(DataState.Loading())
        add(
            interactors.getListFootballMatch(sourceFrom)
                .subscribe({
                    Log.e("TAG", Gson().toJson(it))
                    _listMatch.postValue(DataState.Success(it))
                }, {
                    _listMatch.postValue(DataState.Error(it))
                })
        )
    }

    private val _matchWithStreamLink by lazy { MutableLiveData<DataState<FootballMatchWithStreamLink>>() }
    val matchDetail: LiveData<DataState<FootballMatchWithStreamLink>>
        get() = _matchWithStreamLink

    fun getFootballMatchDetail(match: FootballMatch) {
        _matchWithStreamLink.postValue(DataState.Loading())
        add(
            interactors.getLinkStreamForMatch(match)
                .subscribe({
                    _matchWithStreamLink.postValue(DataState.Success(it))
                }, {
                    _matchWithStreamLink.postValue(DataState.Error(it))
                })
        )
    }

    fun loadEuroData() {

    }
}
package com.kt.apps.xembongda.ui

import androidx.lifecycle.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.repository.ICommentRepository
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.usecase.AsyncTransformer
import com.kt.apps.xembongda.usecase.GetLinkStreamForMatch
import com.kt.apps.xembongda.usecase.GetListFootballMatch
import javax.inject.Inject
import javax.inject.Provider


data class MainInteractors @Inject constructor(
    val getListFootballMatch: GetListFootballMatch,
    val getLinkStreamForMatch: GetLinkStreamForMatch,
    val commentRepository: ICommentRepository,
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
        isLoading.addSource(listMatch) {
            isLoading.postValue(it is DataState.Loading)
        }

    }

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

    private val _matchWithStreamLink by lazy { MutableLiveData<DataState<FootballMatchWithStreamLink>>() }
    val matchDetail: LiveData<DataState<FootballMatchWithStreamLink>>
        get() = _matchWithStreamLink
    private var _currentMatch: FootballMatch? = null
    val currentMatch: FootballMatch?
        get() = _currentMatch

    fun getFootballMatchDetail(match: FootballMatch) {
        _currentMatch = match
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

    fun getFootballMatchDetail(match: FootballMatch, html: String) {
        _currentMatch = match
        _matchWithStreamLink.postValue(DataState.Loading())
        add(
            interactors.getLinkStreamForMatch(match, html)
                .subscribe({
                    _matchWithStreamLink.postValue(DataState.Success(it))
                }, {
                    _matchWithStreamLink.postValue(DataState.Error(it))
                })
        )
    }

    fun loadEuroData() {

    }

    private val _commentCount by lazy {
        MutableLiveData<Int>()
    }
    val commentCount: LiveData<Int>
        get() = _commentCount

    fun loadCommentNum() {
        add(
            interactors.commentRepository
                .loadTotalCommentCount()
                .compose(AsyncTransformer())
                .subscribe({
                    _commentCount.postValue(it)
                }, {
                })
        )
    }

    fun increaseComment(ads: RewardItem) {
        interactors.commentRepository.increaseComment(ads.amount)
    }
}
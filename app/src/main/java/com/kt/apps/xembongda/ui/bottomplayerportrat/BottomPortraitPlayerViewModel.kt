package com.kt.apps.xembongda.ui.bottomplayerportrat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.rewarded.RewardItem
import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.model.comments.CommentSpace
import com.kt.apps.xembongda.repository.ICommentRepository
import com.kt.apps.xembongda.ui.comment.BaseCommentFootballMatch
import com.kt.apps.xembongda.usecase.authenticate.GetUserInfo
import com.kt.apps.xembongda.usecase.comments.AddComment
import javax.inject.Inject


class BottomPortraitPlayerViewModel @Inject constructor(
    private val userInfo: GetUserInfo,
    private val addComment: AddComment,
    private val repository: ICommentRepository
) : BaseViewModel() {
    private val _sendCommentLiveData by lazy {
        MutableLiveData<DataState<CommentDTO>>()
    }
    val sendComment: LiveData<DataState<CommentDTO>>
        get() = _sendCommentLiveData

    fun sendComment(comment: CommentDTO, match: FootballMatch?) {
        _sendCommentLiveData.postValue(DataState.Loading())
        add(
            addComment(comment, match!!).subscribe({
                _sendCommentLiveData.postValue(DataState.Success(it))
            }, {
                _sendCommentLiveData.postValue(DataState.Error(it))
                Log.e("TAG", it.message, it)
            })
        )
    }

    private val _totalCommentLiveData by lazy {
        MutableLiveData<DataState<List<BaseCommentFootballMatch>>>()
    }
    val totalComment: LiveData<DataState<List<BaseCommentFootballMatch>>>
        get() = _totalCommentLiveData

    fun loadComment(match: CommentSpace.Match) {
        _totalCommentLiveData.postValue(DataState.Loading())
        add(
            repository.loadCommentFor(match)
                .map { listComment ->
                    listComment.map { commentDTO ->
                        BaseCommentFootballMatch.fromDto(commentDTO)
                    }
                }.subscribe({
                    _totalCommentLiveData.postValue(DataState.Success(it))
                }, {
                    Log.e("TAG", it.message, it)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
    }
}
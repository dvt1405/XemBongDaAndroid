package com.kt.apps.xembongda.ui.comment

import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.usecase.GetAllCommentForMatch
import javax.inject.Inject


data class CommentInteractors @Inject constructor(
    val getAllCommentForMatch: GetAllCommentForMatch
)

class CommentViewModel @Inject constructor(
    private val interactors: CommentInteractors
) : BaseViewModel() {


    fun getAllCommentForMatch(match: GetAllCommentForMatch) {

    }
}
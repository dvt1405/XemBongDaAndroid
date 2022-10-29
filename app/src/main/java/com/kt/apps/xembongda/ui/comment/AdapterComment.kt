package com.kt.apps.xembongda.ui.comment

import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.adapter.BaseAdapter
import com.kt.apps.xembongda.databinding.ItemCommentBinding
import com.kt.apps.xembongda.model.comments.CommentDTO

class AdapterComment : BaseAdapter<BaseCommentFootballMatch, ItemCommentBinding>() {
    override val itemLayoutRes: Int
        get() = R.layout.item_comment

    override fun bindItem(
        item: BaseCommentFootballMatch,
        binding: ItemCommentBinding,
        position: Int
    ) {
        binding.item = item
    }

    override fun onAdd(item: BaseCommentFootballMatch) {
        super.onAdd(item)
    }

    fun onAddNewComment(comment: CommentDTO) {
        val newItem = BaseCommentFootballMatch(
            comment.titleName,
            comment.avatarUrl ?: "",
            comment.commentDetail,
            comment.uID,
            comment.systemTime
        )
        onAdd(0, newItem)
    }
}
package com.kt.apps.xembongda.ui.comment

import android.util.Log
import com.google.gson.Gson
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.adapter.BaseAdapter
import com.kt.apps.xembongda.databinding.ItemCommentBinding

class AdapterComment : BaseAdapter<IBaseItemComment, ItemCommentBinding>() {
    override val itemLayoutRes: Int
        get() = R.layout.item_comment

    override fun bindItem(item: IBaseItemComment, binding: ItemCommentBinding, position: Int) {
        binding.item = item
    }

    override fun onAdd(item: IBaseItemComment) {
        super.onAdd(item)
        Log.e("TAG", "onAdd: ${item.commentDetail}}" )
    }
}
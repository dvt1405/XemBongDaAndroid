package com.kt.apps.xembongda.ui.bottomplayerportrat

import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.adapter.BaseAdapter
import com.kt.apps.xembongda.databinding.ItemLinkStreamBinding
import com.kt.apps.xembongda.model.LinkStreamWithReferer

class AdapterListM3u8Link : BaseAdapter<LinkStreamWithReferer, ItemLinkStreamBinding>() {
    override val itemLayoutRes: Int
        get() = R.layout.item_link_stream

    override fun bindItem(
        item: LinkStreamWithReferer,
        binding: ItemLinkStreamBinding,
        position: Int
    ) {
        binding.item = item
        binding.position  = position
    }
}
package com.kt.apps.xembongda.ui.bottomplayerportrat

import android.graphics.Color
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.adapter.BaseAdapter
import com.kt.apps.xembongda.databinding.ItemLinkStreamBinding
import com.kt.apps.xembongda.model.LinkStreamWithReferer

class AdapterListM3u8Link(private val opacityBackground: Float = 1f) : BaseAdapter<LinkStreamWithReferer, ItemLinkStreamBinding>() {
    override val itemLayoutRes: Int
        get() = R.layout.item_link_stream

    override fun bindItem(
        item: LinkStreamWithReferer,
        binding: ItemLinkStreamBinding,
        position: Int
    ) {
        binding.item = item
        binding.position  = position
        if (opacityBackground < 1) {
            binding.btnLink.setBackgroundColor(Color.argb((opacityBackground * 255).toInt(),0, 0, 0))
        } else {
            binding.btnLink.setBackgroundColor(Color.rgb(74, 73, 73))
        }
    }
}
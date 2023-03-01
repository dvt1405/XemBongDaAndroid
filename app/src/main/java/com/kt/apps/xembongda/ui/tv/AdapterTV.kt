package com.kt.apps.xembongda.ui.tv

import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.adapter.BaseAdapter
import com.kt.apps.xembongda.databinding.ItemTvBinding
import com.kt.apps.xembongda.model.tv.KenhTvDetail

class AdapterTV : BaseAdapter<KenhTvDetail, ItemTvBinding>() {
    override val itemLayoutRes: Int
        get() = R.layout.item_tv

    override fun bindItem(item: KenhTvDetail, binding: ItemTvBinding, position: Int) {
        binding.tv = item
    }
}
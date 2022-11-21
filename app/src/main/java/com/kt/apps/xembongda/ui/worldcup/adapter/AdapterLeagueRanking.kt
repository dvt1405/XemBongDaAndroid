package com.kt.apps.xembongda.ui.worldcup.adapter

import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.adapter.BaseAdapter
import com.kt.apps.xembongda.databinding.ItemSingleChipsBinding
import com.kt.apps.xembongda.model.League

class AdapterLeagueRanking : BaseAdapter<League, ItemSingleChipsBinding>() {
    override val itemLayoutRes: Int
        get() = R.layout.item_single_chips

    override fun bindItem(item: League, binding: ItemSingleChipsBinding, position: Int) {
        binding.item = item.name
    }
}
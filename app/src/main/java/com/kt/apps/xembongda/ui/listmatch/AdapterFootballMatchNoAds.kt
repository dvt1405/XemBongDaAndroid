package com.kt.apps.xembongda.ui.listmatch

import androidx.recyclerview.widget.RecyclerView
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.adapter.BaseAdapter
import com.kt.apps.xembongda.databinding.ItemFootballMatchBinding
import com.kt.apps.xembongda.model.FootballMatch

class AdapterFootballMatchNoAds : BaseAdapter<FootballMatch, ItemFootballMatchBinding>() {
    override val itemLayoutRes: Int
        get() = R.layout.item_football_match

    override fun bindItem(item: FootballMatch, binding: ItemFootballMatchBinding, position: Int) {
        binding.item = item
        binding.footballMatchTitle.isSelected = true
        binding.leagueTitle.isSelected = true
        val layoutParams = binding.root.layoutParams as RecyclerView.LayoutParams
        if (listItem.indexOf(item) == listItem.size - 1) {
            layoutParams.bottomMargin =
                binding.root.context.resources.getDimension(com.kt.apps.xembongda.base.R.dimen.common_padding)
                    .toInt()
        } else {
            layoutParams.bottomMargin = 0
        }
        binding.root.layoutParams = layoutParams
    }
}
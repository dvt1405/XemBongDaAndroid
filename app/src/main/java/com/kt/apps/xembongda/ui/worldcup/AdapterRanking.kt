package com.kt.apps.xembongda.ui.worldcup

import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ads.AdsListener
import com.kt.apps.xembongda.base.adapter.BaseAdsAdapter
import com.kt.apps.xembongda.databinding.AdViewContainerBinding
import com.kt.apps.xembongda.databinding.ItemEuroBinding
import com.kt.apps.xembongda.ui.worldcup.adapter.AdapterTableView
import com.kt.apps.xembongda.ui.worldcup.model.EuroFootballMatchItem


class AdapterRanking : BaseAdsAdapter<EuroFootballMatchItem, AdViewContainerBinding, ItemEuroBinding>() {
    override val adsLayoutRes: Int
        get() = R.layout.ad_view_container
    override val itemLayoutRes: Int
        get() = R.layout.item_euro
    override val oneBanner: Boolean
        get() = false
    private val adsListener by lazy {
        AdsListener(AdsListener.Type.BANNER)
    }
    override fun bindItem(item: EuroFootballMatchItem, binding: ItemEuroBinding) {
        if (item.table.isEmpty()) {
//            binding.root.gone()
        } else {
            binding.titleGroup.text = item.name
            val adapterTableView = AdapterTableView()
            binding.tableView.setAdapter(adapterTableView)
            adapterTableView.setData(item)
        }
    }

    override fun onRefresh(items: List<EuroFootballMatchItem>, notifyDataChange: Boolean) {
        super.onRefresh(items.filter {
            it.table.isNotEmpty()
        }, notifyDataChange)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun bindAds(adsBinding: AdViewContainerBinding, position: Int) {
        for (view in (adsBinding.root as ViewGroup).children.iterator()) {
            if (view is AdView) {
                view.loadAd(AdRequest.Builder()
                    .build())
                view.adListener = adsListener
            }
        }
    }
}
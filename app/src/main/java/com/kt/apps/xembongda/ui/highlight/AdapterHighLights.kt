package com.kt.apps.xembongda.ui.highlight

import android.util.Log
import com.google.android.gms.ads.nativead.NativeAd
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ads.AdsNativeManager
import com.kt.apps.xembongda.base.adapter.BaseAdsAdapter
import com.kt.apps.xembongda.databinding.ItemHighLightAdsBinding
import com.kt.apps.xembongda.databinding.ItemHighLightBinding

class AdapterHighLights :
    BaseAdsAdapter<ItemHighLights, ItemHighLightAdsBinding, ItemHighLightBinding>(),
    AdsNativeManager.Observer {
    override val adsLayoutRes: Int
        get() = R.layout.item_high_light_ads
    override val itemLayoutRes: Int
        get() = R.layout.item_high_light
    override val oneBanner: Boolean
        get() = true

    override fun getItemCount(): Int {
        return _listItem.size
    }

    override fun getItem(position: Int): ItemHighLights {
        return _listItem[position]
    }

    override fun getItemViewType(position: Int): Int {
        return if (_listItem[position] is ItemHighLights.Ads) return adsLayoutRes
        else itemLayoutRes
    }

    override fun bindItem(item: ItemHighLights, binding: ItemHighLightBinding) {
        try {
            val dto = (item as ItemHighLights.DTO).dto

            binding.item = dto
        } catch (_: Exception) {
        }
    }

    override fun bindAds(adsBinding: ItemHighLightAdsBinding, position: Int) {
        Log.e("TAG", listItem[position]::class.java.name)
        val item = _listItem[position] as ItemHighLights.Ads
        if (item.ad == null) {
            App.get().adsLoaderManager.addObserver(this)
            App.get().adsLoaderManager.preloadNativeAds()
        } else {
            adsBinding.nativeAdsView.setNativeAd(item.ad)
        }
    }

    override fun onRefresh(items: Array<out ItemHighLights>, notifyDataChange: Boolean) {
        super.onRefresh(items, notifyDataChange)
    }

    override fun onAdd(item: ItemHighLights) {
        super.onAdd(item)
    }

    var isLoading = false
    fun onLoading() {
        isLoading = true
//        notifyItemInserted(listItem.size)
    }

    override fun onReceiveAds(nativeAd: NativeAd) {
        val item = _listItem.first {
            it is ItemHighLights.Ads && it.ad == null
        }
        val index = _listItem.indexOf(item)
        _listItem.removeAt(index)
        (item as ItemHighLights.Ads).ad = nativeAd
        _listItem.add(index, item)
        notifyItemChanged(index)
    }

}
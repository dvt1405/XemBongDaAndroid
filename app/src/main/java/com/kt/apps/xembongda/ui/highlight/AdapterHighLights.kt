package com.kt.apps.xembongda.ui.highlight

import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ads.AdsNativeManager
import com.kt.apps.xembongda.base.adapter.BaseAdsAdapter
import com.kt.apps.xembongda.base.adapter.BaseAdsViewHolder
import com.kt.apps.xembongda.databinding.ItemAdsBinding
import com.kt.apps.xembongda.databinding.ItemHighLightAdsBinding
import com.kt.apps.xembongda.databinding.ItemHighLightBinding
import com.kt.skeleton.runAnimationChangeBackground

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
        return if (_listItem.isEmpty()) 0 else _listItem.size + 1
    }

    override fun getItem(position: Int): ItemHighLights {
        return _listItem[position]
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == listItem.size -> R.layout.item_loading
            _listItem[position] is ItemHighLights.Ads -> return adsLayoutRes
            else -> itemLayoutRes
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.item_loading) {
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            object : RecyclerView.ViewHolder(view) {

            }
        } else {
            super.onCreateViewHolder(parent, viewType)
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == listItem.size) {
            (holder.itemView as ViewGroup).forEach {
                if (it.background is AnimationDrawable) {
                    it.runAnimationChangeBackground()
                }
            }
        } else {
            Log.e("TAG", "Bind item")
            super.onBindViewHolder(holder, position)
        }
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
        listHiden.remove(position)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is BaseAdsViewHolder<*>) {
            val hidenPosition = holder.adapterPosition
            listHiden.add(hidenPosition)
        }
    }

    private val listHiden = mutableListOf<Int>()

    override fun onRefresh(items: Array<out ItemHighLights>, notifyDataChange: Boolean) {
        super.onRefresh(items, notifyDataChange)
    }

    override fun onAdd(item: ItemHighLights) {
        val oldProgressPosition = _listItem.size
        _listItem.add(item)
        notifyItemChanged(oldProgressPosition)
        notifyItemInserted(_listItem.size)
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
        if (index !in listHiden) {
            notifyItemChanged(index)
        }
    }

}
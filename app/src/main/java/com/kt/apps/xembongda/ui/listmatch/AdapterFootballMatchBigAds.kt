package com.kt.apps.xembongda.ui.listmatch

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.adapter.BaseAdsAdapter
import com.kt.apps.xembongda.databinding.ItemBigNativeAdsBinding
import com.kt.apps.xembongda.databinding.ItemFootballMatchBinding
import com.kt.apps.xembongda.model.FootballMatch
import kotlin.random.Random

class AdapterFootballMatchBigAds :
    BaseAdsAdapter<FootballMatch, ItemBigNativeAdsBinding, ItemFootballMatchBinding>() {

    private val _firstAdPosition = 0

    private val _secondPosition = Random.nextInt(_firstAdPosition + 5, 7)

    override val itemLayoutRes: Int
        get() = R.layout.item_football_match

    override val adsLayoutRes: Int
        get() = R.layout.item_big_native_ads

    override fun getItemCount(): Int {
        return if (_listItem.isEmpty()) {
            0
        } else if (_listItem.size < 7) {
            _listItem.size
        } else if (App.get().adsLoaderManager.listAds.isEmpty()) {
            _listItem.size + 1
        } else {
            _listItem.size + 2
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount == _listItem.size) {
            itemLayoutRes
        } else if (itemCount == (_listItem.size + 1)) {
            if (position == _firstAdPosition) {
                adsLayoutRes
            } else {
                itemLayoutRes
            }
        } else {
            if (position == _firstAdPosition || position == _secondPosition) {
                adsLayoutRes
            } else {
                itemLayoutRes
            }
        }
    }

    override fun getItem(position: Int): FootballMatch {
        return when (itemCount) {
            _listItem.size -> {
                listItem[position]
            }
            _listItem.size + 1 -> {
                listItem[if (position - _firstAdPosition > 0) {
                    position - 1
                } else {
                    position
                }]
            }
            else -> {
                val realPosition = when {
                    position < _firstAdPosition -> position
                    position < _secondPosition -> position - 1
                    else -> position - 2
                }
                listItem[realPosition]
            }
        }
    }


    override fun onRefresh(items: Array<out FootballMatch>, notifyDataChange: Boolean) {
        super.onRefresh(items, notifyDataChange)
    }

    override fun onRefresh(items: List<FootballMatch>, notifyDataChange: Boolean) {
        if (listAds.size > 2) {
            Log.e("TAG", "onRefresh: ${listAds.size}")
            for (i in 0..1) {
                listAds.removeAt(i)
            }
        } else {
            listAds.clear()
        }
        Log.e("TAG", "onRefresh: ${listAds.size}")
        super.onRefresh(items, notifyDataChange)
    }

    override fun bindItem(item: FootballMatch, binding: ItemFootballMatchBinding) {
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

    private val listAds by lazy {
        mutableListOf<NativeAd>()
    }


    override fun bindAds(adsBinding: ItemBigNativeAdsBinding, position: Int) {
        val adsPosition = if (position == _firstAdPosition) {
            0
        } else {
            1
        }
        try {
            val nativeAd = if (listAds.size < 2) {
                App.get().adsLoaderManager.getLastItem()?.also {
                    listAds.add(it)
                } ?: throw Exception()
            } else {
                listAds[adsPosition]
            }
            adsBinding.nativeAdsView.setNativeAd(nativeAd)

        } catch (e: Exception) {
            loadAds(adsPosition)
        }
    }

    fun loadAds(position: Int) {
        var isLoadingThisAds = true
        App.get().adsLoaderManager
            .preloadNativeAds {
                synchronized(listAds) {
                    listAds.add(it)
                }
                if (listAds.size > position && isLoadingThisAds) {
                    isLoadingThisAds = false
                    notifyItemChanged(position)
                }
            }
    }

    override val oneBanner: Boolean
        get() = true
}
package com.kt.apps.xembongda.ui.listmatch

import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ads.AdsNativeManager
import com.kt.apps.xembongda.base.adapter.BaseAdsAdapter
import com.kt.apps.xembongda.databinding.ItemFootballMatchBinding
import com.kt.apps.xembongda.databinding.ItemNativeAdsBinding
import com.kt.apps.xembongda.model.FootballMatch
import kotlin.random.Random

class AdapterFootballMatch :
    BaseAdsAdapter<FootballMatch, ItemNativeAdsBinding, ItemFootballMatchBinding>(),
    AdsNativeManager.Observer {

    private val _firstAdPosition = Random.nextInt(0, 4)

    private val _secondPosition = Random.nextInt(_firstAdPosition + 3, 7)

    override val itemLayoutRes: Int
        get() = R.layout.item_football_match

    override val adsLayoutRes: Int
        get() = R.layout.item_native_ads


    private val listAds by lazy {
        mutableListOf<NativeAd>()
    }

    init {
        App.get().adsLoaderManager.addObserver(this)
        if (App.get().adsLoaderManager.listAds.size > 2) {
            for (i in 0..2) {
                App.get().adsLoaderManager.listAds.poll()?.let {
                    listAds.add(it)
                }
            }
        }
        if (listAds.size > 2) {
            App.get().adsLoaderManager.unregister(this)
        }
    }

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


    override fun bindAds(adsBinding: ItemNativeAdsBinding, position: Int) {
        try {
            val adsPosition = if (position == _firstAdPosition) {
                0
            } else {
                1
            }
            if (listAds.size < 2) {
                App.get().adsLoaderManager.addObserver(this)
                App.get().adsLoaderManager.preloadNativeAds()
            } else {
                adsBinding.nativeAdsView.setNativeAd(listAds[adsPosition])
            }
        } catch (_: Exception) {

        }
    }

    override val oneBanner: Boolean
        get() = true

    override fun clearAds() {
        super.clearAds()
        listAds.forEach {
            it.destroy()
        }
        notifyItemInserted(_firstAdPosition)
        notifyItemInserted(_secondPosition)
        listAds.clear()
    }

    override fun onReceiveAds(nativeAd: NativeAd) {
        listAds.add(nativeAd)
        if (listAds.size > 1) {
            notifyItemChanged(_firstAdPosition)
            notifyItemChanged(_secondPosition)
        } else if (listAds.size > 0) {
            notifyItemChanged(_firstAdPosition)
        }

        if (listAds.size > 2) {
            App.get().adsLoaderManager.unregister(this)
        }

    }

}
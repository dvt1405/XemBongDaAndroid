package com.kt.apps.xembongda.ui.listmatch

import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.adapter.BaseAdsAdapter
import com.kt.apps.xembongda.databinding.ItemFootballMatchBinding
import com.kt.apps.xembongda.databinding.ItemNativeAdsBinding
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.skeleton.KunSkeleton
import kotlin.random.Random
import kotlin.random.nextInt

class AdapterFootballMatch :
    BaseAdsAdapter<FootballMatch, ItemNativeAdsBinding, ItemFootballMatchBinding>() {

    private val _firstAdPosition = Random.nextInt(0, 4)

    private val _secondPosition = Random.nextInt(_firstAdPosition + 3, 7)

    override val itemLayoutRes: Int
        get() = R.layout.item_football_match

    override val adsLayoutRes: Int
        get() = R.layout.item_native_ads

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

    private val listAds by lazy {
        mutableListOf<NativeAd>()
    }


    override fun bindAds(adsBinding: ItemNativeAdsBinding, position: Int) {
        try {
            val adsPosition = if (position == _firstAdPosition) {
                0
            } else {
                1
            }
            if (listAds.size < 2) {
                val nativeAd = App.get().adsLoaderManager.listAds.removeFirst()
                listAds.add(nativeAd)
                adsBinding.nativeAdsView.setNativeAd(nativeAd)
            } else {
                adsBinding.nativeAdsView.setNativeAd(listAds[adsPosition])
            }
        } catch (e: Exception) {
            loadAds(adsBinding)
        }
    }

    fun loadAds(adsBinding: ItemNativeAdsBinding) {
        val skeleton =
            KunSkeleton.bind(adsBinding.root.findViewById(com.google.android.ads.nativetemplates.R.id.icon) as View)
                .build()

        val headlineskeleton =
            KunSkeleton.bind(adsBinding.root.findViewById(com.google.android.ads.nativetemplates.R.id.primary) as View)
                .layout(R.layout.skeleton_title)
                .build()

        val row_twoskeleton =
            KunSkeleton.bind(adsBinding.root.findViewById(com.google.android.ads.nativetemplates.R.id.secondary) as View)
                .layout(com.kt.skeleton.R.layout.skeleton_image)
                .build()

        val ctaskeleton =
            KunSkeleton.bind(adsBinding.root.findViewById(com.google.android.ads.nativetemplates.R.id.cta) as View)
                .build()

        val adLoad = AdLoader.Builder(App.get(), App.get().getString(R.string.ad_mod_native_id))
            .forNativeAd {
                listAds.add(it)
                skeleton.hide()
                headlineskeleton.hide()
                row_twoskeleton.hide()
                ctaskeleton.hide()
                adsBinding.nativeAdsView.setStyles(
                    NativeTemplateStyle.Builder()
                        .withCallToActionBackgroundColor(
                            ContextCompat.getColor(
                                App.get(),
                                com.kt.apps.xembongda.base.R.color.white
                            )
                        )
                        .withPrimaryTextTypefaceColor(
                            ContextCompat.getColor(
                                App.get(),
                                com.kt.apps.xembongda.base.R.color.white
                            )
                        )
                        .withSecondaryTextTypefaceColor(
                            ContextCompat.getColor(
                                App.get(),
                                com.kt.apps.xembongda.base.R.color.white
                            )
                        )
                        .withTertiaryTextTypefaceColor(
                            ContextCompat.getColor(
                                App.get(),
                                com.kt.apps.xembongda.base.R.color.white
                            )
                        )
                        .withCallToActionTypefaceColor(
                            ContextCompat.getColor(
                                App.get(),
                                com.kt.apps.xembongda.base.R.color.backgroundColor
                            )
                        )
                        .withMainBackgroundColor(
                            ColorDrawable(
                                ContextCompat.getColor(
                                    App.get(),
                                    com.kt.apps.xembongda.base.R.color.backgroundColor

                                )
                            )
                        )

                        .build()
                )

                adsBinding.nativeAdsView.setNativeAd(it)
            }
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setRequestCustomMuteThisAd(true)
                    .build()
            )
            .withAdListener(object : AdListener() {
                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()

                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    loadAds(adsBinding)
                }
            })
            .build()
        adLoad.loadAds(AdRequest.Builder().build(), 3)
        skeleton.run()
        headlineskeleton.run()
        row_twoskeleton.run()
        ctaskeleton.run()
    }

    override val oneBanner: Boolean
        get() = true

}
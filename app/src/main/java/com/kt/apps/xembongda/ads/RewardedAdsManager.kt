package com.kt.apps.xembongda.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BuildConfig
import com.kt.apps.xembongda.player.AudioFocusManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class RewardedAdsManager @Inject constructor(
    private val context: Context,
    private val audioFocusManager: AudioFocusManager
) {
    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    val adRequest by lazy {
        AdRequest.Builder()
            .build()
    }

    private val listAds by lazy {
        ArrayDeque<RewardedAd>()
    }

    private val adsListener by lazy {
        AdsListener(AdsListener.Type.REWARD)
    }

    fun preLoadAds(adUnits: String = context.getString(R.string.ad_mod_comment_rewarded)) {
        compositeDisposable.add(
            Observable.create {
                RewardedAd.load(
                    context,
                    context.getString(R.string.ad_mod_comment_rewarded),
                    adRequest,
                    object : RewardedAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            adError.responseInfo?.responseId?.let { it1 -> Log.e("TAG", it1) }
                            it.onError(Throwable())
                            adsListener.onAdFailedToLoad(adError)
                        }

                        override fun onAdLoaded(rewardedAd: RewardedAd) {
                            it.onNext(rewardedAd)
                            adsListener.onAdLoaded()
                        }
                    })

            }.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    listAds.add(it)
                }, {
                    adsListener.onAdFailedToLoad(
                        LoadAdError(
                            -999,
                            it.message ?: it::class.java.name,
                            "Exception",
                            null,
                            null
                        )
                    )
                    Log.e("TAG", it.message, it)
                    if (listAds.size < 2) {
                        preLoadAds()
                    }
                })
        )
    }

    private val adUnits by lazy {
        if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/5224354917"
        } else {
            context.getString(R.string.ad_mod_comment_rewarded)
        }
    }

    fun loadAds(adUnits: String = this.adUnits) =
        Observable.create { emitter ->
            loadAds(adUnits, 5, {
                if (!emitter.isDisposed) {
                    emitter.onNext(it)
                }
            }, {
                if (!emitter.isDisposed) {
                    emitter.onError(it)
                }
            })
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())

    fun loadAds(
        adUnits: String,
        maxRetry: Int = 5,
        onLoaded: (ad: RewardedAd) -> Unit,
        onError: (t: Throwable) -> Unit
    ) {
        RewardedAd.load(
            context,
            adUnits,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError.responseInfo?.responseId?.let { it1 -> Log.e("TAG", it1) }
                    if (maxRetry - 1 < 0) {
                        onError(Throwable())
                    } else {
                        loadAds(adUnits, maxRetry - 1, onLoaded, onError)
                    }
                    adsListener.onAdFailedToShow(adError)
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    adsListener.onAdLoaded()

                    rewardedAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                audioFocusManager.requestFocus()
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                adsListener.onAdClicked()
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)
                                adsListener.onAdFailedToShow(p0)
                            }

                            override fun onAdImpression() {
                                super.onAdImpression()
                                adsListener.onAdImpression()
                            }

                        }
                    onLoaded(rewardedAd)
                }
            })
    }

    fun getAds(): RewardedAd {
        val ads = listAds.last()
        listAds.remove(ads)
        return ads
    }

    fun clear() {
        compositeDisposable.clear()
    }
}

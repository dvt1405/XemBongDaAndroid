package com.kt.apps.xembongda.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.usecase.AsyncTransformer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class RewardedAdsManager @Inject constructor(
    private val context: Context
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

    fun preLoadAds(adUnits: String = context.getString(R.string.ad_mod_comment_rewarded)) {
        compositeDisposable.add(
            Observable.create {
                RewardedAd.load(
                    context,
                    context.getString(R.string.ad_mod_comment_rewarded),
                    adRequest,
                    object : RewardedAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.e("TAG", adError.message)
                            adError.responseInfo?.responseId?.let { it1 -> Log.e("TAG", it1) }
                            Log.e("TAG", adError.domain)
                            it.onError(Throwable())
                        }

                        override fun onAdLoaded(rewardedAd: RewardedAd) {
                            it.onNext(rewardedAd)
                        }
                    })

            }.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    listAds.add(it)
                }, {
                    Log.e("TAG", it.message, it)
                    if (listAds.size < 2) {
                        preLoadAds()
                    }
                })
        )
    }

    fun loadAds(adUnits: String = context.getString(R.string.ad_mod_comment_rewarded)) =
        Observable.create {
            RewardedAd.load(
                context,
                context.getString(R.string.ad_mod_comment_rewarded),
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.e("TAG", adError.message)
                        adError.responseInfo?.responseId?.let { it1 -> Log.e("TAG", it1) }
                        Log.e("TAG", adError.domain)
                        it.onError(Throwable())
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        Log.e("TAG", "onAdLoaded")

                        it.onNext(rewardedAd)
                    }
                })
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())

    fun getAds(): RewardedAd {
        val ads = listAds.last()
        listAds.remove(ads)
        return ads
    }

    fun clear() {
        compositeDisposable.clear()
    }
}

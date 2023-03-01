package com.kt.apps.xembongda.ui.livescore

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ads.AdsConfigManager
import com.kt.apps.xembongda.ads.AdsListener
import com.kt.apps.xembongda.ads.applovin.ApplovinAdsManager
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentLiveScoreBinding
import com.kt.apps.xembongda.model.LiveScoreDTO
import com.kt.apps.xembongda.repository.ILiveScoresRepository
import com.kt.skeleton.CustomItemDivider
import com.kt.skeleton.KunSkeleton
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FragmentLiveScore : BaseFragment<FragmentLiveScoreBinding>() {

    @Inject
    lateinit var liveScores: ILiveScoresRepository

    @Inject
    lateinit var applovinAdsManager: ApplovinAdsManager

    @Inject
    lateinit var adsConfigManager: AdsConfigManager

    override val layoutResId: Int
        get() = R.layout.fragment_live_score

    override val screenName: String
        get() = "FragmentLiveScore"

    private val _adapter by lazy {
        AdapterLiveScore()
    }

    private val disposable by lazy {
        CompositeDisposable()
    }

    private val liveScoreSkeleton by lazy {
        KunSkeleton.bind(binding.recyclerView)
            .adapter(_adapter)
            .layoutItem(R.layout.item_live_scores_skeleton)
            .build()
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = _adapter
        binding.recyclerView.addItemDecoration(CustomItemDivider(requireContext()))

    }

    override fun initAction(savedInstanceState: Bundle?) {
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.swipeRefreshLayout.isEnabled = false
            disposable.clear()
            getLiveScore()
        }
        getLiveScore()
    }

    override fun onStart() {
        super.onStart()
//        try {
//            adsConfigManager.init({
//                loadAds()
//            })
//        } catch (e: Exception) {
//            Firebase.crashlytics.log(e.message ?: e::class.java.simpleName)
//        }
    }
    private var timer: Timer? = null
    private fun loadAds() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    binding.adView.loadAd(
                        AdRequest.Builder()
                            .build()
                    )
                }
            }
        }, 500, 3 * 60 * 1000)

        binding.adView.adListener = object : AdsListener(Type.BANNER) {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.adView.loadAd(
                        AdRequest.Builder()
                            .build()
                    )
                }, 5000)
            }
        }
    }

    private fun getLiveScore() {
        liveScoreSkeleton.run()
        disposable.add(
            Observable.interval(0, 2, TimeUnit.MINUTES)
                .observeOn(Schedulers.io())
                .flatMap {
                    liveScores.getLiveScore()
                }.map {
                    it.map { item ->
                        if (item is LiveScoreDTO.Title) {
                            ILiveScoreModel.LiveScoreTitle(item)
                        } else {
                            ILiveScoreModel.LiveScore(item as LiveScoreDTO.Match)
                        }
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (liveScoreSkeleton.isRunning) {
                        liveScoreSkeleton.hide {
                            binding.recyclerView.addItemDecoration(PinItemDecoration(binding.recyclerView, _adapter))
                        }
                    }
                    binding.swipeRefreshLayout.isEnabled = true
                    _adapter.onRefresh(it)
                }, {
                })
        )
    }

    override fun onDestroyView() {
        disposable.clear()
        try {
            timer?.cancel()
        } catch (_: Exception) {

        }
        super.onDestroyView()
    }
}
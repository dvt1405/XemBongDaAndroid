package com.kt.apps.xembongda.ui.livescore

import android.os.Bundle
import android.util.Log
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentLiveScoreBinding
import com.kt.apps.xembongda.model.LiveScoreDTO
import com.kt.apps.xembongda.repository.ILiveScoresRepository
import com.kt.skeleton.CustomItemDivider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FragmentLiveScore : BaseFragment<FragmentLiveScoreBinding>() {

    @Inject
    lateinit var liveScores: ILiveScoresRepository

    override val layoutResId: Int
        get() = R.layout.fragment_live_score

    private val _adapter by lazy {
        AdapterLiveScore()
    }

    private val disposable by lazy {
        CompositeDisposable()
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = _adapter
        binding.recyclerView.addItemDecoration(CustomItemDivider(requireContext()))
        binding.recyclerView.addItemDecoration(PinItemDecoration(binding.recyclerView, _adapter))

    }

    override fun initAction(savedInstanceState: Bundle?) {
        disposable.add(
            Observable.interval(0,2, TimeUnit.MINUTES)
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
                    _adapter.onRefresh(it)
                }, {
                    Log.e("TAG", it.message, it)
                })
        )
    }

    override fun onDestroyView() {
        disposable.clear()
        super.onDestroyView()
    }
}
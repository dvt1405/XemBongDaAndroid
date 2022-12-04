package com.kt.apps.xembongda.ui.worldcup

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentWorldcupBinding
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.ui.MainViewModelFactory
import com.kt.apps.xembongda.ui.worldcup.adapter.AdapterLeagueRanking
import com.kt.apps.xembongda.ui.worldcup.model.EuroFootballMatchItem
import com.kt.skeleton.KunSkeleton
import javax.inject.Inject

class FragmentWorldCup : BaseFragment<FragmentWorldcupBinding>() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val worldCupViewModel: WorldCupViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[WorldCupViewModel::class.java]
    }

    private val adapter by lazy { AdapterRanking() }
    private val listLeagueRankingAdapter by lazy {
        AdapterLeagueRanking()
    }
    private val skeletonLeague by lazy {
        KunSkeleton.bind(binding.recyclerViewLeague)
            .adapter(listLeagueRankingAdapter)
            .layoutItem(R.layout.item_link_stream_skeleton)
            .build()
    }

    private val skeletonRanking by lazy {
        KunSkeleton.bind(binding.recyclerViewRanking)
            .adapter(adapter)
            .layoutItem(R.layout.item_football_match_skeleton)
            .build()
    }

    override val layoutResId: Int
        get() = R.layout.fragment_worldcup

    override val screenName: String
        get() = "FragmentRanking"


    override fun initView(savedInstanceState: Bundle?) {
        binding.recyclerViewRanking.adapter = adapter
        binding.recyclerViewLeague.adapter = listLeagueRankingAdapter
        worldCupViewModel.getAllLeague()
    }

    private fun handleRanking(dataState: DataState<List<EuroFootballMatchItem>>) {
        when (dataState) {
            is DataState.Success -> {
                binding.swipeRefreshLayout.isRefreshing = false
                adapter.onRefresh(dataState.data)
                skeletonRanking.hide {  }
            }
            is DataState.Loading -> {
                binding.swipeRefreshLayout.isRefreshing = false
                if (!skeletonRanking.isRunning) {
                    skeletonRanking.run {

                    }
                }

            }

            else -> {

            }
        }
    }

    override fun initAction(savedInstanceState: Bundle?) {
        binding.swipeRefreshLayout.setOnRefreshListener {
            worldCupViewModel.getAllLeague()
        }
        listLeagueRankingAdapter.onItemRecyclerViewCLickListener = { item, position ->
            worldCupViewModel.getRankingForLeague(item)
        }
        worldCupViewModel.worldCupRanking.observe(viewLifecycleOwner) {
            handleRanking(it)
        }
        worldCupViewModel.allLeague.observe(viewLifecycleOwner) {
            handleListLeague(it)
        }
    }

    private fun handleListLeague(dataState: DataState<List<League>>) {
        when (dataState) {
            is DataState.Success -> {
                binding.swipeRefreshLayout.isRefreshing = false
                skeletonLeague.hide {
                    listLeagueRankingAdapter.onRefresh(dataState.data)
                }
            }

            is DataState.Loading -> {
                binding.swipeRefreshLayout.isRefreshing = false
                skeletonLeague.run()
                skeletonRanking.run()
            }

            else -> {

            }
        }
    }
}
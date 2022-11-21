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
    override val layoutResId: Int
        get() = R.layout.fragment_worldcup

    override fun initView(savedInstanceState: Bundle?) {
        binding.recyclerViewRanking.adapter = adapter
        binding.recyclerViewLeague.adapter = listLeagueRankingAdapter
        worldCupViewModel.getAllLeague()
    }

    private fun handleRanking(dataState: DataState<List<EuroFootballMatchItem>>) {
        when (dataState) {
            is DataState.Success -> {
                adapter.onRefresh(dataState.data)
            }

            else -> {

            }
        }
    }

    override fun initAction(savedInstanceState: Bundle?) {
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
                listLeagueRankingAdapter.onRefresh(dataState.data)
            }

            else -> {

            }
        }
    }
}
package com.kt.apps.xembongda.ui.listmatch

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentListMatchBinding
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.ui.MainViewModel
import com.kt.skeleton.CustomItemDivider
import com.kt.skeleton.KunSkeleton
import javax.inject.Inject

class FragmentListMatch : BaseFragment<FragmentListMatchBinding>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[FragmentListMatchViewModel::class.java]
    }

    private val mainActivityViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    }

    private val adapter by lazy {
        if (sourceFrom == FootballRepoSourceFrom.MiTom) {
            AdapterFootballMatch()
        } else {
            AdapterFootballMatchBigAds()
        }
    }
    private val skeleton by lazy {
        KunSkeleton.bind(binding.recyclerView)
            .adapter(adapter)
            .layoutItem(R.layout.item_football_match_skeleton)
            .build()
    }

    private val sourceFrom: FootballRepoSourceFrom by lazy {
        FootballRepoSourceFrom.valueOf(
            requireArguments().getString(
                EXTRA_SOURCE_FROM,
                FootballRepoSourceFrom.Phut91.name
            )
        )
    }

    override val layoutResId: Int
        get() = R.layout.fragment_list_match


    override fun initView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            viewModel.getListFootBallMatch(sourceFrom)
        }
        skeleton.run()
        binding.recyclerView.addItemDecoration(
            CustomItemDivider(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
    }

    override fun initAction(savedInstanceState: Bundle?) {
        viewModel.listMatch.observe(this) {
            handleListMatch(it)
        }
        adapter.onItemRecyclerViewCLickListener = { item, position ->
            mainActivityViewModel.getFootballMatchDetail(item)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getListFootBallMatch(sourceFrom)
        }
    }

    private fun handleListMatch(listMatch: DataState<List<FootballMatch>>) {
        when (listMatch) {
            is DataState.Success -> {
                skeleton.hide {
                    adapter.onRefresh(listMatch.data)
                    binding.swipeRefreshLayout.isEnabled = true
                }
            }
            is DataState.Loading -> {
                skeleton.run()
                binding.swipeRefreshLayout.isRefreshing = false
                binding.swipeRefreshLayout.isEnabled = false

            }
            else -> {
                binding.swipeRefreshLayout.isEnabled = true
            }
        }
    }

    companion object {
        private const val EXTRA_SOURCE_FROM = "extra:source_from"
        fun newInstance(sourceFrom: FootballRepoSourceFrom) = FragmentListMatch().apply {
            this.arguments = Bundle().apply {
                this.putString(EXTRA_SOURCE_FROM, sourceFrom.name)
            }
        }
    }
}
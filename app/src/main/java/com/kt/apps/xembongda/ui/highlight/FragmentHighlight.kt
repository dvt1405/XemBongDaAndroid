package com.kt.apps.xembongda.ui.highlight

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ads.AdsInterstitialManager
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentHighlightBinding
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.player.ExoPlayerManager
import com.kt.skeleton.CustomItemDivider
import com.kt.skeleton.KunSkeleton
import javax.inject.Inject

class FragmentHighlight : BaseFragment<FragmentHighlightBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_highlight

    override val screenName: String
        get() = "FragmentHighlight"

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager

    @Inject
    lateinit var interstitialManager: AdsInterstitialManager

    private val viewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[FragmentHighlightViewModel::class.java]
    }

    private val adapterHighLights by lazy { AdapterHighLights() }
    private val skeleton by lazy {
        KunSkeleton.bind(binding.recyclerView)
            .adapter(adapterHighLights)
            .layoutItem(R.layout.item_high_light_skeleton)
            .build()
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.recyclerView.addItemDecoration(
            CustomItemDivider(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        binding.recyclerView.adapter = adapterHighLights
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getHighlight(1)
        }
        binding.recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastItem = layoutManager.findLastCompletelyVisibleItemPosition()
                if (lastItem == (adapterHighLights.itemCount - 1) && !adapterHighLights.isLoading) {
                    viewModel.getHighlight()
                }
            }
        })

    }

    override fun initAction(savedInstanceState: Bundle?) {
        skeleton.run()
        if (savedInstanceState == null) {
            viewModel.getHighlight(1)
        }
        viewModel.highlightLiveData.observe(this) {
            handleHighLight(it)
        }
        adapterHighLights.onItemRecyclerViewCLickListener = { item, position ->
            interstitialManager.loadAds(requireActivity())
            if (item is ItemHighLights.DTO) {
                viewModel.getHighLightDetail(item.dto)
            }
        }
        viewModel.highLightDetail.observe(this) {

        }
    }

    private fun handleHighLight(dataState: DataState<List<ItemHighLights>>) {
        when (dataState) {
            is DataState.Loading -> {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.swipeRefreshLayout.isEnabled = false
                if (viewModel.currentPage == 1) {
                    if (!skeleton.isRunning) {
                        skeleton.run()
                    }
                } else {
                    adapterHighLights.onLoading()
                }

            }

            is DataState.Success -> {
                adapterHighLights.isLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
                binding.swipeRefreshLayout.isEnabled = true
                if (viewModel.currentPage == 1) {
                    skeleton.hide {
                        adapterHighLights.onRefresh(dataState.data)
                    }
                } else {
                    adapterHighLights.onAdd(dataState.data)
                }
            }

            is DataState.Error -> {
                adapterHighLights.isLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
                binding.swipeRefreshLayout.isEnabled = true
                skeleton.hide()
            }
            else -> {

            }
        }
    }
}
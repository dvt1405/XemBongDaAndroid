package com.kt.apps.xembongda.ui.tv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentTvBinding
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.utils.showErrorDialog
import com.kt.skeleton.CustomItemDivider
import com.kt.skeleton.KunSkeleton
import javax.inject.Inject

class FragmentTV : BaseFragment<FragmentTvBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_tv
    override val screenName: String
        get() = "TV"

    private val adapterTV by lazy {
        AdapterTV()
    }

    private val skeleton by lazy {
        KunSkeleton.bind(binding.recyclerView)
            .adapter(adapterTV)
            .layoutItem(R.layout.item_tv_skeleton)
            .build()
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), factory)[FragmentTVViewModel::class.java]
    }

    override fun initView(savedInstanceState: Bundle?) {
        viewModel.getAll()
        binding.recyclerView.addItemDecoration(CustomItemDivider(requireContext()))
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    override fun initAction(savedInstanceState: Bundle?) {
        adapterTV.onItemRecyclerViewCLickListener = { item, position ->
            viewModel.getDetail(item)
        }
        viewModel.listChannel.observe(this) {
            when (it) {
                is DataState.Loading -> {
                    skeleton.run {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.swipeRefreshLayout.isEnabled = false
                    }

                }
                is DataState.Success -> {
                    skeleton.hide {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.swipeRefreshLayout.isEnabled = true
                        adapterTV.onRefresh(it.data)
                    }

                }
                is DataState.Error -> {
                    binding.swipeRefreshLayout.isEnabled = true
                    showErrorDialog(content = "Thử tải lại danh sách kênh TV bạn nhé!")
                }
                else -> {

                }
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getAll()
        }
    }
}
package com.kt.apps.xembongda.ui.tabs

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentListMatchTabsBinding

class FragmentListMatchTabs : BaseFragment<FragmentListMatchTabsBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_list_match_tabs

    override fun initView(savedInstanceState: Bundle?) {
        binding.viewPayer.adapter = AdapterFootballListPager(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPayer) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_default_link_source)
                else -> getString(R.string.tab_sub_link_source)
            }
        }.attach()
    }

    override fun initAction(savedInstanceState: Bundle?) {
    }
}
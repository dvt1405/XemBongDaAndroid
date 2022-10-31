package com.kt.apps.xembongda.ui.tabs

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.ui.listmatch.FragmentListMatch

class AdapterFootballListPager(private val fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val items by lazy {
        FootballRepoSourceFrom.values()
            .filter {
                it != FootballRepoSourceFrom.MiTom
            }
    }
    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        return FragmentListMatch.newInstance(items[position])
    }
}
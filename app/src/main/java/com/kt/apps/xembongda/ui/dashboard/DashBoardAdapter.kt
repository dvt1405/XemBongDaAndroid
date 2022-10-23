package com.kt.apps.xembongda.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kt.apps.xembongda.ui.highlight.FragmentHighlight
import com.kt.apps.xembongda.ui.livescore.FragmentLiveScore
import com.kt.apps.xembongda.ui.tabs.FragmentListMatchTabs

class DashBoardAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        1 -> FragmentListMatchTabs()
        0 -> FragmentLiveScore()
        else -> FragmentHighlight()
    }
}
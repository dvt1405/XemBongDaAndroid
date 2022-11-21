package com.kt.apps.xembongda.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kt.apps.xembongda.ui.highlight.FragmentHighlight
import com.kt.apps.xembongda.ui.livescore.FragmentLiveScore
import com.kt.apps.xembongda.ui.tabs.FragmentListMatchTabs
import com.kt.apps.xembongda.ui.worldcup.FragmentWorldCup

class DashBoardAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment = when (position) {
        1 -> FragmentListMatchTabs()
        2 -> FragmentLiveScore()
        0 -> FragmentWorldCup()
        else -> FragmentHighlight()
    }
}
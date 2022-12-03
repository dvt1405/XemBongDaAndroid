package com.kt.apps.xembongda.ui.tabs

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.ui.listmatch.FragmentListMatch

class AdapterFootballListPager(private val fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val items by lazy {
        FootballRepoSourceFrom.values()
            .filter {
                if (Firebase.remoteConfig.getBoolean("allow_vtv")) {
                    it != FootballRepoSourceFrom.MiTom
                } else {
                    it != FootballRepoSourceFrom.MiTom && it != FootballRepoSourceFrom.VTV
                }
            }
    }
    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment {
        return FragmentListMatch.newInstance(items[position])
    }
}
package com.kt.apps.xembongda.ui.dashboard

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentDashboardBinding
import com.kt.apps.xembongda.ui.MainViewModel
import com.kt.apps.xembongda.ui.MainViewModelFactory
import javax.inject.Inject

class FragmentDashboard : BaseFragment<FragmentDashboardBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_dashboard

    override val screenName: String
        get() = ""

    @Inject
    lateinit var factory: MainViewModelFactory

    private val mainActivityViewModel by lazy {
        ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    private val viewModel by lazy {
        ViewModelProvider(this, factory)[DashboardViewModel::class.java]
    }


    override fun initView(savedInstanceState: Bundle?) {
        viewModel.get()
        binding.dashboardViewPager.adapter = DashBoardAdapter(this)
        binding.dashboardViewPager.setCurrentItem(1, false)
        binding.dashboardViewPager.animation = AnimationUtils.loadAnimation(requireContext(), com.kt.skeleton.R.anim.fade_out)
        binding.bottomNavigation.selectedItemId = R.id.middle
        binding.dashboardViewPager.isUserInputEnabled = false
        binding.bottomNavigation.setOnItemSelectedListener {
            mapMenuId[it.itemId]?.let { it1 ->
                binding.dashboardViewPager.setCurrentItem(
                    it1,
                    true
                )
                if (it1 == 1) {
                    binding.bottomAppbar.performShow()
                }
                binding.bottomAppbar.hideOnScroll = it1 != 2
                firebaseAnalytics.logEvent(
                    "ChangeBottomNavigation", bundleOf(
                        "menuId" to it1
                    )
                )
            }
            return@setOnItemSelectedListener true
        }

        binding.btnFootballLink.setOnClickListener {
            binding.dashboardViewPager.currentItem = 1
        }

        binding.dashboardViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0 || position == 2) {
                    mainActivityViewModel.loadEuroData()
                }
            }
        })
    }

    override fun initAction(savedInstanceState: Bundle?) {

    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        private val mapMenuId by lazy {
            mapOf(
                R.id.liveScore to 2,
                R.id.euro to 0,
                R.id.middle to 1,
                R.id.highlight to 3,
//                R.id.liveScore to 4,
            )
        }
    }
}
package com.kt.apps.xembongda.di.main

import androidx.lifecycle.ViewModel
import com.kt.apps.xembongda.MainActivity
import com.kt.apps.xembongda.di.DashboardModule
import com.kt.apps.xembongda.di.viewmodels.ViewModelKeys
import com.kt.apps.xembongda.di.viewmodels.ViewModelModule
import com.kt.apps.xembongda.ui.listmatch.FragmentListMatch
import com.kt.apps.xembongda.ui.MainViewModel
import com.kt.apps.xembongda.ui.bottomplayerportrat.FragmentBottomPlayerPortrait
import com.kt.apps.xembongda.ui.comment.CommentViewModel
import com.kt.apps.xembongda.ui.dashboard.DashboardViewModel
import com.kt.apps.xembongda.ui.dashboard.FragmentDashboard
import com.kt.apps.xembongda.ui.highlight.FragmentHighlight
import com.kt.apps.xembongda.ui.highlight.FragmentHighlightViewModel
import com.kt.apps.xembongda.ui.listmatch.FragmentListMatchViewModel
import com.kt.apps.xembongda.ui.livescore.FragmentLiveScore
import com.kt.apps.xembongda.ui.livescore.FragmentLiveScoreViewModel
import com.kt.apps.xembongda.ui.tabs.FragmentListMatchTabs
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun fragmentListMatch(): FragmentListMatch

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun fragmentListMatchTabs(): FragmentListMatchTabs

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun fragmentLiveScore(): FragmentLiveScore

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun fragmentBottomPlayerPortrait(): FragmentBottomPlayerPortrait

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun fragmentHighlight(): FragmentHighlight

    @ContributesAndroidInjector(modules = [ViewModelModule::class, DashboardModule::class])
    internal abstract fun fragmentDashboard(): FragmentDashboard

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun mainActivity(): MainActivity

    @Binds
    @IntoMap
    @ViewModelKeys(MainViewModel::class)
    internal abstract fun bindViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKeys(DashboardViewModel::class)
    internal abstract fun bindDashboardViewModel(viewModel: DashboardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKeys(FragmentLiveScoreViewModel::class)
    internal abstract fun bindLiveScoreViewModel(viewModel: FragmentLiveScoreViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKeys(FragmentHighlightViewModel::class)
    internal abstract fun bindHighlightViewModel(viewModel: FragmentHighlightViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKeys(FragmentListMatchViewModel::class)
    internal abstract fun bindListMatchViewModel(viewModel: FragmentListMatchViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKeys(CommentViewModel::class)
    internal abstract fun bindCommentViewModel(viewModel: CommentViewModel): ViewModel


}
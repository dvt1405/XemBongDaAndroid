package com.kt.apps.xembongda.di

import androidx.lifecycle.ViewModel
import com.kt.apps.xembongda.ui.dashboard.DashboardViewModel
import dagger.Binds
import dagger.Module

@Module
abstract class DashboardModule {

    @Binds
    abstract fun viewModel(dashboardViewModel: DashboardViewModel): ViewModel
}
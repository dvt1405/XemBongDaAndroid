package com.kt.apps.xembongda.di.viewmodels

import androidx.lifecycle.ViewModelProvider
import com.kt.apps.xembongda.ui.MainViewModelFactory
import dagger.Binds
import dagger.Module

@Module
internal abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(
        factory: MainViewModelFactory
    ): ViewModelProvider.Factory
}

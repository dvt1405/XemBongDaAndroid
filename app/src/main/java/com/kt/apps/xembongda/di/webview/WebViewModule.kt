package com.kt.apps.xembongda.di.webview

import com.kt.apps.xembongda.di.viewmodels.ViewModelModule
import com.kt.apps.xembongda.ui.webview.WebViewActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class WebViewModule  {

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun webViewActivity(): WebViewActivity
}
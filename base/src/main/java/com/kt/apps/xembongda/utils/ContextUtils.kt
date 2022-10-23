package com.kt.apps.xembongda.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import java.util.*

fun Context.updateLocale(language: String = "vi") {
    val config = Configuration()
    val locale = Locale(language)
    config.setLocale(locale)
    Locale.setDefault(locale)
    createConfigurationContext(config)
}

@SuppressLint("InternalInsetResource")
fun Context.getNavigationBarHeight(): Int {
    val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}
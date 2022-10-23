package com.kt.apps.xembongda.base.adapter

import androidx.databinding.ViewDataBinding

interface BaseItemWrapper<T : Any> {
    val dataEntity: T
    val layoutRes: Int
    val viewBinding: ViewDataBinding
}
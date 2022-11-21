package com.kt.apps.xembongda.ui.worldcup.adapter

interface IRowHeaderModel {
    val title: String
}

class BaseRowHeader(override val title: String) : IRowHeaderModel
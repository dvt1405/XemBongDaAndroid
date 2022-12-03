package com.kt.apps.xembongda.ui.worldcup.adapter

interface IRowHeaderModel {
    val title: String
    val logo: String
}

class BaseRowHeader(override val title: String, override val logo: String) : IRowHeaderModel
package com.kt.apps.xembongda.ui.worldcup.adapter

interface IColumnHeaderModel {
    val title: String
}
class BaseColumnHeader(override val title: String) : IColumnHeaderModel
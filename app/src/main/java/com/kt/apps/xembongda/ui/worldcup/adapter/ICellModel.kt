package com.kt.apps.xembongda.ui.worldcup.adapter

interface ICellModel {
    val data: String
}

class BaseCellData(override val data: String) : ICellModel
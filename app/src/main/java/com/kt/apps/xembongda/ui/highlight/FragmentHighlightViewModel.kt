package com.kt.apps.xembongda.ui.highlight

import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.usecase.GetAllHighlight
import javax.inject.Inject

data class HighlightInteractors @Inject constructor(
    val getALlHighlight: GetAllHighlight
)

class FragmentHighlightViewModel @Inject constructor(
    private val interactors: HighlightInteractors
) : BaseViewModel() {

    fun getALlHighlight() {

    }
}
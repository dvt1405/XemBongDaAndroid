package com.kt.apps.xembongda.ui.livescore

import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.usecase.GetAllLiveScore
import javax.inject.Inject


data class LiveScoreInteractors @Inject constructor(
    val getAllLiveScore: GetAllLiveScore
)

class FragmentLiveScoreViewModel @Inject constructor(
    interactors: LiveScoreInteractors
) : BaseViewModel() {

}
package com.kt.apps.xembongda.model.highlights

import com.kt.apps.xembongda.model.LinkStreamWithReferer

data class HighLightDetail(
    val highLightDTO: HighLightDTO,
    val linkStreamWithReferer: List<LinkStreamWithReferer>
) {
}
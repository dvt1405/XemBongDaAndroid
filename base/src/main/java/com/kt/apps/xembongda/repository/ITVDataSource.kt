package com.kt.apps.xembongda.repository

import com.kt.apps.xembongda.model.LinkStreamWithReferer
import com.kt.apps.xembongda.model.tv.KenhTvDetail
import com.kt.apps.xembongda.model.tv.SourceFrom
import com.kt.apps.xembongda.usecase.logging.FirebaseLogUtils
import io.reactivex.rxjava3.core.Observable

interface ITVDataSource {
    fun getTvList(): Observable<List<KenhTvDetail>>
    fun getTvLinkFromDetail(kenhTvDetail: KenhTvDetail, isBackup: Boolean = false): Observable<List<LinkStreamWithReferer>>
}

fun Observable<List<KenhTvDetail>>.onErrorReturnWithLog(source: SourceFrom): Observable<List<KenhTvDetail>> {
    return this.doOnNext {
        FirebaseLogUtils.logGetListChannel(source.name)
    }.onErrorReturn {
        FirebaseLogUtils.logGetListChannelError(source.name, it)
        listOf()
    }
}
package com.kt.apps.xembongda.usecase

import io.reactivex.rxjava3.core.Observable

abstract class BaseUseCase<T : Any>(private val transformer: AsyncTransformer<T> = AsyncTransformer()) {
    abstract fun prepareExecute(params: Map<String, Any>): Observable<T>
    fun execute(params: Map<String, Any>): Observable<T> = prepareExecute(params).compose(transformer)
}
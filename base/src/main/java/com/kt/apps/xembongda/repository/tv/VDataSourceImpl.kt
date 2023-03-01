package com.kt.apps.xembongda.repository.tv

import android.util.Log
import androidx.core.os.bundleOf
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.kt.apps.xembongda.model.LinkStreamWithReferer
import com.kt.apps.xembongda.model.tv.*
import com.kt.apps.xembongda.repository.ITVDataSource
import com.kt.apps.xembongda.storage.IKeyValueStorage
import com.kt.apps.xembongda.usecase.logging.FirebaseLogUtils
import com.kt.apps.xembongda.utils.getBaseUrl
import com.kt.apps.xembongda.utils.removeAllSpecialChars
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.json.JSONObject
import org.jsoup.Jsoup
import javax.inject.Inject

class VDataSourceImpl @Inject constructor(
    private val sharePreference: IKeyValueStorage,

    ) : ITVDataSource {
    private val compositeDisposable by lazy { CompositeDisposable() }
    private val reference by lazy {
        Firebase.database
            .reference
    }

    private val remoteConfig by lazy {
        Firebase.remoteConfig
    }

    private fun needRefresh(name: String): Boolean {
        val needRefresh = remoteConfig.getBoolean(name)
        val version = remoteConfig.getLong("${name}_refresh")
        val refreshedInVersion = sharePreference.get(name, Int::class.java)
        return needRefresh && version > refreshedInVersion
    }

    private var isOnline: Boolean = false

    override fun getTvList(): Observable<List<KenhTvDetail>> {
        val listGroup = ChannelGroup.values().map {
            it.name
        }

        return Observable.create<List<KenhTvDetail>> { emiter ->
            val totalChannel = mutableListOf<KenhTvDetail>()
            var count = 0
            listGroup.forEach { group ->
                val needRefresh = false
//                if (sharePreference.getList(group, KenhTvDetail::class.java).isNotEmpty() && !needRefresh) {
//                    isOnline = false
//                    count++
//                    if (count == listGroup.size) {
//                        emiter.onNext(totalChannel)
//                        emiter.onComplete()
//                    }
//                } else {
                    isOnline = true
                    fetchTvList(group) {
                        if (needRefresh) {
                            sharePreference.save(group, remoteConfig.getLong("${group}_refresh"), Long::class.java)
                        }
                        sharePreference.save(group, it)
                        totalChannel.addAll(it)
                        count++
                        if (count == listGroup.size) {
                            emiter.onNext(totalChannel)
                            emiter.onComplete()
                        }
                    }.addOnFailureListener {
                        Log.e("TAG", it.message, it)
                        count++
                        emiter.onError(it)
                    }
//                }
            }

        }.doOnError {
            Log.e("TAG", it.message, it)
            FirebaseLogUtils.logGetListChannelError(SourceFrom.V.name, it)
        }.doOnComplete {
            FirebaseLogUtils.logGetListChannel(
                SourceFrom.V.name,
                bundleOf("fetch_from" to if (isOnline) "online" else "offline")
            )
        }
    }


    override fun getTvLinkFromDetail(
        kenhTvDetail: KenhTvDetail,
        isBackup: Boolean
    ): Observable<List<LinkStreamWithReferer>> {
        return Observable.create { emiter ->
            val body = Jsoup.connect(kenhTvDetail.detailPage)
                .header("referer", kenhTvDetail.detailPage)
                .header("origin", kenhTvDetail.detailPage.getBaseUrl())
                .execute()
                .parse()
                .body()
            body.getElementById("__NEXT_DATA__")?.let {
                val text = it.html()
                val jsonObject = JSONObject(text)
                val linkM3u8 = jsonObject.getJSONObject("props")
                    .getJSONObject("initialState")
                    .getJSONObject("LiveTV")
                    .getJSONObject("detailChannel")
                    .optString("linkPlayHls")
                emiter.onNext(
                    listOf(
                        LinkStreamWithReferer(
                            linkM3u8,
                            kenhTvDetail.detailPage
                        )
                    )
                )
            }
            emiter.onComplete()
        }
    }

    private fun fetchTvList(
        name: String,
        onComplete: (list: List<KenhTvDetail>) -> Unit
    ): Task<DataSnapshot> {
        return reference.child(name)
            .get()
            .addOnSuccessListener {
                val value = it.getValue<List<DataFromFirebase>>() ?: return@addOnSuccessListener
                onComplete(
                    value.map {
                        KenhTvDetail(
                            name,
                            name = it.name,
                            detailPage = it.url,
                            logoChannel = it.logo,
                            sourceFrom = SourceFrom.V.name,
                            id = if (name in listOf(ChannelGroup.VOV.name, ChannelGroup.VOH.name)) {
                                it.name.removeAllSpecialChars()
                            } else {
                                it.url.trim().removeSuffix("/").split("/").last()
                            }
                        )
                    })
            }

    }
}
package com.kt.apps.xembongda.player

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.model.LinkStreamWithReferer
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager


class ExoPlayerManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private val TAG = this::class.java.simpleName
    }

    var playerView: StyledPlayerView? = null
        set(value) {
            field = value
            field?.player = exoPlayer
        }

    private var _currentTargetPlayerView: StyledPlayerView? = null

    fun switchTargetView(targetPlayerView: StyledPlayerView) {
        StyledPlayerView.switchTargetView(exoPlayer, playerView, targetPlayerView)
        playerView = targetPlayerView
        _currentTargetPlayerView = targetPlayerView
    }

    fun setupController(
        playerView: StyledPlayerView,
        minHeight: Int = LayoutParams.WRAP_CONTENT
    ) {
        val layoutParams = playerView.layoutParams
        layoutParams?.height = if (minHeight == LayoutParams.WRAP_CONTENT) LayoutParams.WRAP_CONTENT
        else (context.resources.displayMetrics.scaledDensity * minHeight).toInt()
        playerView.layoutParams = layoutParams
        playerView.findViewById<View>(R.id.exc_btn_close_player)?.setOnClickListener {
            onCloseExoPlayer?.invoke()
        }
        playerView.setFullscreenButtonClickListener {}
    }

    fun setupController(playerView: StyledPlayerView, isMinimize: Boolean) {
        setClickListenerForControllerView(playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_controller))
        if (isMinimize) {

        }
    }

    private fun setClickListenerForControllerView(playerControllerView: StyledPlayerControlView) {

    }

    var onCloseExoPlayer: (() -> Unit)? = null

    private val audioAttr by lazy {
        AudioAttributes.Builder()
            .build()
    }

    private val playerListener by lazy {
        object : Player.Listener {
            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
            }

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                when (state) {
                    ExoPlayer.STATE_IDLE -> {
                    }
                    ExoPlayer.STATE_BUFFERING -> {

                    }
                    ExoPlayer.STATE_READY -> {
                        val layoutParams = _currentTargetPlayerView?.layoutParams
                        layoutParams?.height = LayoutParams.WRAP_CONTENT
                        _currentTargetPlayerView?.layoutParams = layoutParams
                    }
                    ExoPlayer.STATE_ENDED -> {
                    }
                    else -> {
                    }
                }
            }

        }
    }


    val exoPlayer by lazy {
        ExoPlayer.Builder(context)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .setAudioAttributes(audioAttr, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    private fun trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier { name, password ->
                return@setDefaultHostnameVerifier true
            }
            val context = SSLContext.getInstance("TLS")
            context.init(
                null, arrayOf<X509TrustManager>(@SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                }), SecureRandom()
            )
            HttpsURLConnection.setDefaultSSLSocketFactory(
                context.socketFactory
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun playVideo(data: List<LinkStreamWithReferer>) {
        val dfSource: DefaultHttpDataSource.Factory = DefaultHttpDataSource.Factory()
        dfSource.setDefaultRequestProperties(
            getHeader90pLink(data.first().referer)
        )
        dfSource.setKeepPostFor302Redirects(true)
        dfSource.setUserAgent(context.getString(R.string.user_agent))
        trustEveryone()
        val adLoader = ImaAdsLoader.Builder(context).setAdPreloadTimeoutMs(3000).build()
        val adLoaderProvider = DefaultMediaSourceFactory.AdsLoaderProvider { adsConfiguration ->
            adsConfiguration.adTagUri
            adLoader
        }

        val mediaSources = data.map { it.m3u8Link }.map {
            DefaultMediaSourceFactory(dfSource)
                .setServerSideAdInsertionMediaSourceFactory(DefaultMediaSourceFactory(dfSource))
                .createMediaSource(MediaItem.fromUri(it.trim()))
        }

        playerView?.setControllerHideDuringAds(true)
        exoPlayer.setMediaSources(mediaSources)
        exoPlayer.addListener(playerListener)
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    fun pause() {
        exoPlayer.pause()
    }

    private fun getHeader90pLink(referer: String): Map<String, String> {
        val needHost = referer.contains("auth_key")
        val host = try {
            referer.trim().toHttpUrl().host
        } catch (e: Exception) {
            ""
        }

        return mutableMapOf(
            "accept-encoding" to "gzip, deflate, br",
            "accept-language" to "vi-VN,vi;q=0.9,fr-FR;q=0.8,fr;q=0.7,en-US;q=0.6,en;q=0.5,am;q=0.4,en-AU;q=0.3",
            "origin" to referer.getBaseUrl(),
            "referer" to referer.trim(),
            "sec-fetch-dest" to "empty",
            "sec-fetch-site" to "cross-site",
            "user-agent" to context.getString(R.string.user_agent),
        ).apply {
            if (needHost) {
                this["Host"] = host
            }
        }
    }

    private fun String.getBaseUrl(): String {
        val isUrl = this.trim().startsWith("http")
        val isHttps = this.trim().startsWith("https")
        if (!isUrl) return ""
        val baseUrl = this.toHttpUrl().host
        return if (isHttps) "https://${baseUrl.trim()}" else "http://${baseUrl.trim()}"
    }

}
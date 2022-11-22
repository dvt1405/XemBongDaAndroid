package com.kt.apps.xembongda.player

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.animation.AccelerateInterpolator
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.model.LinkStreamWithReferer
import com.kt.apps.xembongda.utils.gone
import com.kt.apps.xembongda.utils.trustEveryone
import com.kt.apps.xembongda.utils.visible
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Inject


class ExoPlayerManager @Inject constructor(
    private val context: Context,
    private val audioFocusManager: AudioFocusManager
) : Application.ActivityLifecycleCallbacks, AudioFocusManager.OnFocusChange {
    companion object {
        private const val FULL_SCREEN_FLAG = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private var firstStreamSuccess: Boolean = false
    private val exoFullScreenDrawableEnter: Drawable by lazy {
        ContextCompat.getDrawable(
            context,
            R.drawable.ic_round_fullscreen_24
        )!!
    }

    init {
        App.get().registerActivityLifecycleCallbacks(this)
    }

    private val exoFullScreenDrawableExit: Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_round_fullscreen_exit_24)!!
    }
    private val btnResumePortraitViewVideoId by lazy {
        R.id.exc_btn_resume_portrait
    }

    private val audioAttr by lazy {
        AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setAllowedCapturePolicy(C.ALLOW_CAPTURE_BY_NONE)
                }
            }
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
                        val layoutParams = playerView?.layoutParams
                        layoutParams?.height = if (isFullScreen) {
                            LayoutParams.MATCH_PARENT
                        } else {
                            LayoutParams.WRAP_CONTENT
                        }
                        playerView?.layoutParams = layoutParams
                        firstStreamSuccess = true
                    }
                    ExoPlayer.STATE_ENDED -> {
                    }
                    else -> {
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                if (!firstStreamSuccess) {
                    val layoutParams = _currentTargetPlayerView?.layoutParams
                    layoutParams?.height =
                        lastMinHeightSetupForController ?: LayoutParams.WRAP_CONTENT
                    _currentTargetPlayerView?.layoutParams = layoutParams
                }
            }

        }
    }


    var exoPlayer: ExoPlayer? = null
    var isFullScreen: Boolean = false
    var oldScreenFlag: Int? = null

    var playerView: StyledPlayerView? = null
        set(value) {
            field = value
            field?.player = exoPlayer ?: buildExoPlayer().also {
                exoPlayer = it
            }
        }
    private val scaleDensity by lazy {
        context.resources.displayMetrics.scaledDensity
    }
    private var lastMinHeightSetupForController: Int? = null

    private var _currentTargetPlayerView: StyledPlayerView? = null

    var onCloseExoPlayer: (() -> Unit)? = null

    fun switchTargetView(targetPlayerView: StyledPlayerView, isFullScreen: Boolean = false) {
        if (exoPlayer == null) {
            exoPlayer = buildExoPlayer()
        }
        StyledPlayerView.switchTargetView(exoPlayer!!, playerView, targetPlayerView)
        playerView = targetPlayerView
        _currentTargetPlayerView = targetPlayerView
        if (isFullScreen) {
            updateFullScreenState(isFullScreen)
        }
    }

    fun setupController(
        activity: AppCompatActivity,
        playerView: StyledPlayerView,
        minHeight: Int = LayoutParams.WRAP_CONTENT,
        isMinimize: Boolean = false
    ) {
        if (!isMinimize) {
            hideBtnGotoPortrait()
        }
        if (!firstStreamSuccess || isFullScreen) {
            val layoutParams = playerView.layoutParams
            layoutParams?.height =
                if (minHeight == LayoutParams.WRAP_CONTENT || minHeight == LayoutParams.MATCH_PARENT) {
                    minHeight
                } else {
                    lastMinHeightSetupForController = (scaleDensity * minHeight).toInt()
                    lastMinHeightSetupForController!!
                }
            playerView.layoutParams = layoutParams
        }
        setClickListenerForControllerView(activity, playerView, isMinimize)
    }

    private fun setClickListenerForControllerView(
        activity: AppCompatActivity,
        playerView: StyledPlayerView,
        isFullScreen: Boolean,
        isMinimize: Boolean = false
    ) {
        val playerControllerView: StyledPlayerControlView =
            playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_controller)
        playerControllerView.isAnimationEnabled = true
        playerControllerView.setShowNextButton(!isMinimize)
        playerControllerView.setShowPreviousButton(!isMinimize)
        playerControllerView.setProgressUpdateListener { _, _ ->

        }
        playerView.setFullscreenButtonClickListener {
            if (this.isFullScreen) {
                exitFullScreen(activity)
            } else {
                enterFullScreen(activity)
            }
        }
        playerView.findViewById<View>(R.id.exc_btn_close_player)?.setOnClickListener {
            onCloseExoPlayer?.invoke()
        }
        if (isFullScreen != this.isFullScreen) {
            updateFullScreenState(isFullScreen)
        }
    }

    fun enterFullScreen(activity: AppCompatActivity) {
        if (!isFullScreen) {
            oldScreenFlag = activity.window.attributes.flags
            activity.window.setFlags(
                FULL_SCREEN_FLAG,
                FULL_SCREEN_FLAG
            )
            activity.window.decorView.systemUiVisibility = FULL_SCREEN_FLAG
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            updateFullScreenState(true)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun exitFullScreen(activity: AppCompatActivity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT

        oldScreenFlag?.let {
            activity.window.attributes.flags = it
            activity.window.setFlags(it, it)
            activity.window.decorView.systemUiVisibility = 0
        } ?: activity.window.clearFlags(FULL_SCREEN_FLAG)
        updateFullScreenState(false)
    }

    private val alphaAnimation by lazy {
        ObjectAnimator.ofFloat(0f, 1f)
            .apply {
                this.duration = 400
                this.interpolator = AccelerateInterpolator()
            }
    }


    fun showMinimizeControl(onResumeClick: () -> Unit) {
        showBtnGotoPortrait(onResumeClick)
        updateFullScreenState(false)
        if (firstStreamSuccess) {
            val layoutParams = playerView?.layoutParams
            layoutParams?.height = LayoutParams.WRAP_CONTENT
            playerView?.layoutParams = layoutParams
        }

    }

    private fun hideBtnGotoPortrait() {
        val btnResumePortraitViewVideo =
            playerView?.findViewById<ImageButton>(btnResumePortraitViewVideoId)
        val animUpdate = ValueAnimator.AnimatorUpdateListener {
            btnResumePortraitViewVideo?.alpha = 1 - it.animatedFraction
        }
        alphaAnimation.addUpdateListener(animUpdate)
        alphaAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                btnResumePortraitViewVideo?.gone()
                alphaAnimation.removeAllUpdateListeners()
                alphaAnimation.removeListener(this)
            }
        })
    }

    private fun showBtnGotoPortrait(onResumeClick: () -> Unit) {
        val btnResumePortraitViewVideo =
            playerView?.findViewById<ImageButton>(btnResumePortraitViewVideoId)
        val animUpdate = ValueAnimator.AnimatorUpdateListener {
            btnResumePortraitViewVideo?.alpha = it.animatedFraction
        }
        btnResumePortraitViewVideo?.alpha = 0f
        btnResumePortraitViewVideo?.visible()
        alphaAnimation.addUpdateListener(animUpdate)
        alphaAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                alphaAnimation.removeAllUpdateListeners()
                alphaAnimation.removeListener(this)
            }
        })
        alphaAnimation.start()
        btnResumePortraitViewVideo?.setOnClickListener {
            onResumeClick()
        }
    }

    private fun updateFullScreenState(isFullScreen: Boolean) {
        this.isFullScreen = isFullScreen
        val fullScreenButton =
            playerView?.findViewById<ImageButton>(com.google.android.exoplayer2.ui.R.id.exo_fullscreen)

        if (isFullScreen) {
            fullScreenButton?.visible()
            fullScreenButton?.setImageDrawable(exoFullScreenDrawableExit)
        } else {
            fullScreenButton?.setImageDrawable(exoFullScreenDrawableEnter)
        }
    }

    fun playVideo(data: List<LinkStreamWithReferer>) {
        audioFocusManager.requestFocus(this)
        if (exoPlayer == null) {
            exoPlayer = buildExoPlayer()
        }
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
        exoPlayer?.setMediaSources(mediaSources)
        exoPlayer?.addListener(playerListener)
        exoPlayer?.playWhenReady = true
        exoPlayer?.prepare()
    }

    private fun buildExoPlayer() = ExoPlayer.Builder(context)
        .setWakeMode(C.WAKE_MODE_LOCAL)
        .setAudioAttributes(audioAttr, true)
        .setHandleAudioBecomingNoisy(true)
        .build()

    fun pause() {
        exoPlayer?.pause()
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

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        exoPlayer?.play()
        playerView?.onResume()
    }

    override fun onActivityPaused(activity: Activity) {
        exoPlayer?.pause()
        playerView?.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
        playerView?.onPause()

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
//        exoPlayer?.mediaItem
//        exoPlayer?.release()
//        exoPlayer = null
//        playerView = null
    }

    override fun onAudioFocus() {
        playerView?.onResume()
        exoPlayer?.play()
    }

    override fun onAudioLossFocus() {
        playerView?.onPause()
        exoPlayer?.pause()
    }

}
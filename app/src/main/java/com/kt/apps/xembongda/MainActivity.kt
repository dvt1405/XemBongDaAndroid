package com.kt.apps.xembongda

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.*
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.kt.apps.xembongda.api.BinhLuan90PhutApi
import com.kt.apps.xembongda.base.BaseActivity
import com.kt.apps.xembongda.databinding.ActivityMainBinding
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.model.highlights.HighLightDetail
import com.kt.apps.xembongda.player.ExoPlayerManager
import com.kt.apps.xembongda.ui.MainViewModel
import com.kt.apps.xembongda.ui.PlayerActivity
import com.kt.apps.xembongda.ui.bottomplayerportrat.FragmentBottomPlayerPortrait
import com.kt.apps.xembongda.ui.dashboard.FragmentDashboard
import com.kt.apps.xembongda.ui.highlight.FragmentHighlightViewModel
import com.kt.apps.xembongda.utils.gone
import com.kt.apps.xembongda.utils.visible
import javax.inject.Inject


class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager

    @Inject
    lateinit var api: BinhLuan90PhutApi

    private val viewModel by lazy {
        ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private val highLightViewModel by lazy {
        ViewModelProvider(this, factory)[FragmentHighlightViewModel::class.java]
    }

    private val sceneRoot by lazy {
        binding.sceneRoot
    }

    private val scene1 by lazy {
        Scene.getSceneForLayout(sceneRoot, R.layout.activity_main_sence_1, this)
    }

    private val scene2 by lazy {
        Scene.getSceneForLayout(sceneRoot, R.layout.activity_sence_2, this)
    }

    private val scene3 by lazy {
        Scene.getSceneForLayout(sceneRoot, R.layout.activity_sence_3, this)
    }

    private val transitionSlide by lazy {
        TransitionInflater.from(this)
            .inflateTransition(R.transition.transition_slide)
    }

    private val transition by lazy {
        TransitionInflater.from(this)
            .inflateTransition(R.transition.transition_fade_in_out)

    }
    private var currentScene: Scene? = null


    override val layoutRes: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    override fun initView(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, FragmentDashboard())
            .commit()

        sceneRoot.findViewById<StyledPlayerView>(R.id.exo_player).setOnClickListener {
            if (currentScene == scene3) {
                go(scene2)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        exoPlayerManager.playerView?.let {
            exoPlayerManager.switchTargetView(findViewById(R.id.exo_player))
        }
    }

    override fun initAction(savedInstanceState: Bundle?) {
        viewModel.matchDetail.observe(this, handelGetMatchDetail())
        highLightViewModel.highLightDetail.observe(this, handleHighLightDetail())
        viewModel.loadCommentNum()
        exoPlayerManager.onCloseExoPlayer = {
            onBackPressed()
        }
        binding.btnSwitchPlayerTest.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        binding.landScapeLayout.setOnClickListener {

        }
    }

    private fun handleHighLightDetail() = Observer<DataState<HighLightDetail>> {
        when(it) {
            is DataState.Loading -> {
                exoPlayerManager.pause()
                highLightViewModel.selectedHighLight?.let { it1 -> go(scene2, transition, it1) }
            }
            is DataState.Success -> {
                exoPlayerManager.playVideo(it.data.linkStreamWithReferer)
            }
            else -> {

            }
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                if (currentScene != null && (currentScene == scene2 || currentScene == scene3)
                    && binding.landScapeExoPlayer.visibility == View.GONE
                ) {
                    enterFullScreenLandscape()
                }
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                if (binding.landScapeExoPlayer.visibility == View.VISIBLE) {
                    exitFullScreen()
                }
            }
        }
    }

    private fun exitFullScreen() {
        binding.landScapeExoPlayer.animate()
            .alpha(0f)
            .apply {
                this.duration = 200
            }
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    currentScene?.sceneRoot?.findViewById<StyledPlayerView>(R.id.exo_player)
                        ?.let { exoPlayerManager.switchTargetView(it) }
                    binding.container.visible()
                    binding.landScapeExoPlayer.gone()
                    binding.landScapeLayout.gone()
                }
            })
            .start()
    }

    private fun enterFullScreenLandscape() {
        binding.landScapeLayout.visible()
        binding.landScapeExoPlayer.visible()
        exoPlayerManager.setupController(
            this@MainActivity,
            binding.landScapeExoPlayer,
            ViewGroup.LayoutParams.MATCH_PARENT,
            false
        )

        binding.landScapeExoPlayer.animate()
            .alpha(1f)
            .apply {
                this.duration = 200
            }
            .setUpdateListener {
                binding.landScapeExoPlayer.alpha = it.animatedFraction
            }
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    if (exoPlayerManager.playerView != binding.landScapeExoPlayer) {
                        exoPlayerManager.switchTargetView(binding.landScapeExoPlayer, true)
                        binding.container.gone()
                    }
                }
            })
            .start()
    }

    private fun handelGetMatchDetail(): (data: DataState<FootballMatchWithStreamLink>) -> Unit = {
        when (it) {
            is DataState.Loading -> {
                exoPlayerManager.pause()
                go(scene2)
            }
            is DataState.Success -> {
                if (it.data.linkStreams.isNotEmpty()) {
                    exoPlayerManager.playVideo(it.data.linkStreams)
                }
            }
            is DataState.Error -> {

            }
            else -> {

            }
        }
    }

    override fun onBackPressed() {
        if (exoPlayerManager.isFullScreen) {
            exoPlayerManager.exitFullScreen(this)
            return
        }
        when (currentScene) {
            scene2 -> {
                supportFragmentManager.apply {
                    val fragment = this.findFragmentById(R.id.container) ?: FragmentDashboard()
                    this.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit()
                }
                go(scene3)
            }
            scene1 -> {
                super.onBackPressed()
            }
            scene3 -> {
                exoPlayerManager.pause()
                go(scene1, transitionSlide)

            }
        }
    }

    fun go(scene: Scene, transition: Transition = this.transition, vararg params: Any) {
        if (currentScene == scene) return
        TransitionManager.go(scene, transition).also {
            currentScene = scene
        }

        val playerView = scene.sceneRoot.findViewById<StyledPlayerView>(R.id.exo_player)
        if (scene == scene2 || scene == scene3) {
            if (exoPlayerManager.playerView != null) {
                exoPlayerManager.switchTargetView(playerView)
            } else {
                exoPlayerManager.playerView = playerView
            }
        }
        binding.container.isEnabled = scene != scene2

        if (scene == scene2) {
            exoPlayerManager.setupController(
                this,
                playerView,
                220,
                false
            )
            showBottomFragment(params)
        } else if (scene == scene3) {
            exoPlayerManager.setupController(
                this,
                playerView,
                120,
                true
            )
        }

        transition.addListener(object : TransitionListenerAdapter() {
            override fun onTransitionStart(transition: Transition) {
                super.onTransitionStart(transition)

            }

            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                if (scene == scene3) {
                    exoPlayerManager.showMinimizeControl{
                        go(scene2)
                    }
                }
                if (scene == scene2) {
                }
                transition.removeListener(this)
            }
        })

        val targetColor = when (scene) {
            scene2 -> ContextCompat.getColor(this@MainActivity, R.color.black)
            scene3 -> ContextCompat.getColor(
                this@MainActivity,
                com.kt.apps.xembongda.base.R.color.statusBarColor
            )
            else -> null
        }

        targetColor?.let {
            animateAppAndStatusBar(window.statusBarColor, it)
        }
    }

    private fun showBottomFragment(params: Array<out Any>) {
        val f = if (params.isNotEmpty() && params.filterIsInstance<HighLightDTO>().isNotEmpty()) {
            val item = params.filterIsInstance<HighLightDTO>().last()
            FragmentBottomPlayerPortrait.newInstance(
                FragmentBottomPlayerPortrait.Type.HighLight,
                item
            )
        } else {
            FragmentBottomPlayerPortrait.newInstance()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.bottom, f)
            .commit()
    }


    private fun blendColors(from: Int, to: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val r: Float = Color.red(to) * ratio + Color.red(from) * inverseRatio
        val g: Float = Color.green(to) * ratio + Color.green(from) * inverseRatio
        val b: Float = Color.blue(to) * ratio + Color.blue(from) * inverseRatio
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

    private fun animateAppAndStatusBar(from: Int, to: Int) {
        ValueAnimator.ofFloat(0f, 1f)
            .apply {
                duration = 500
                addUpdateListener {
                    val position = it.animatedFraction
                    val blended: Int = blendColors(
                        from,
                        to,
                        position
                    )
                    window.statusBarColor = blended
                }
                start()
            }

    }

}
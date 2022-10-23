package com.kt.apps.xembongda

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.kt.apps.xembongda.base.BaseActivity
import com.kt.apps.xembongda.databinding.ActivityMainBinding
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.player.ExoPlayerManager
import com.kt.apps.xembongda.ui.MainViewModel
import com.kt.apps.xembongda.ui.PlayerActivity
import com.kt.apps.xembongda.ui.dashboard.FragmentDashboard
import com.kt.apps.xembongda.utils.gone
import com.kt.apps.xembongda.utils.visible
import javax.inject.Inject


class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager

    private val viewModel by lazy {
        ViewModelProvider(this, factory)[MainViewModel::class.java]
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
        exoPlayerManager.onCloseExoPlayer = {
            onBackPressed()
        }
        binding.btnSwitchPlayerTest.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Log.e("TAG", "ORIENTATION_LANDSCAPE")
                binding.landScapeExoPlayer.visible()
                exoPlayerManager.switchTargetView(binding.landScapeExoPlayer)
                currentScene?.sceneRoot?.gone()

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
                            exoPlayerManager.switchTargetView(binding.landScapeExoPlayer)
                        }
                    })
                    .start()
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                binding.landScapeExoPlayer.animate()
                    .alpha(0f)
                    .apply {
                        this.duration = 200
                    }
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            currentScene?.sceneRoot?.visible()
                            binding.landScapeExoPlayer.gone()
                            currentScene?.sceneRoot?.findViewById<StyledPlayerView>(R.id.exo_player)
                                ?.let { exoPlayerManager.switchTargetView(it) }
                        }
                    })
                    .start()
            }
        }
    }

    private fun handelGetMatchDetail(): (data: DataState<FootballMatchWithStreamLink>) -> Unit = {
        when (it) {
            is DataState.Loading -> {
                go(scene2)
            }
            is DataState.Success -> {
                if (it.data.linkStreams.isNotEmpty()) {
                    exoPlayerManager.playVideo(it.data.linkStreams)
                } else {

                }
            }
            is DataState.Error -> {

            }
            else -> {

            }
        }
    }

    override fun onBackPressed() {
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

    fun go(scene: Scene, transition: Transition = this.transition) {
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

        if (scene == scene2) {
            exoPlayerManager.setupController(
                playerView,
                240
            )
        } else if (scene == scene3) {
            exoPlayerManager.setupController(
                playerView,
                120
            )
        }

        transition.addListener(object : TransitionListenerAdapter() {
            override fun onTransitionStart(transition: Transition) {
                super.onTransitionStart(transition)
                Log.e("TAG", "start")

            }

            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                if (scene == scene3) {
                    exoPlayerManager.setupController(playerView)
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
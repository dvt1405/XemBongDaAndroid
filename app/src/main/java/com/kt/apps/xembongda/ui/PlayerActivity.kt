package com.kt.apps.xembongda.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseActivity
import com.kt.apps.xembongda.databinding.ActivityPlayerBinding
import com.kt.apps.xembongda.player.ExoPlayerManager
import javax.inject.Inject

class PlayerActivity : BaseActivity<ActivityPlayerBinding>() {

    @Inject
    lateinit var playerManager: ExoPlayerManager

    override val layoutRes: Int
        get() = R.layout.activity_player

    override fun initView(savedInstanceState: Bundle?) {
        playerManager.switchTargetView(binding.exoPlayer)

    }

    override fun initAction(savedInstanceState: Bundle?) {

    }
}
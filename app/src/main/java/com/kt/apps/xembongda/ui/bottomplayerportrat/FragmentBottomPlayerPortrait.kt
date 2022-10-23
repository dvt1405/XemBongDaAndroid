package com.kt.apps.xembongda.ui.bottomplayerportrat

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentBottomPlayerPortraitBinding
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.player.ExoPlayerManager
import com.kt.apps.xembongda.ui.MainViewModel
import com.kt.apps.xembongda.ui.comment.AdapterComment
import com.kt.apps.xembongda.ui.comment.IBaseItemComment
import com.kt.skeleton.CustomItemDivider
import com.kt.skeleton.KunSkeleton
import java.util.UUID
import javax.inject.Inject

class FragmentBottomPlayerPortrait : BaseFragment<FragmentBottomPlayerPortraitBinding>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager

    private val viewModel by lazy {
        ViewModelProvider(this, factory)[BottomPortraitPlayerViewModel::class.java]
    }

    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    private val adapterListM3u8Link by lazy { AdapterListM3u8Link() }
    private val adapterComment by lazy { AdapterComment() }

    private val listLinkSkeleton by lazy {
        KunSkeleton.bind(binding.recyclerViewUnderPlayer)
            .adapter(adapterListM3u8Link)
            .layoutManager(
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            .layoutItem(R.layout.item_link_stream_skeleton)
            .build()
    }

    private val titleSkeleton by lazy {
        KunSkeleton.bind(binding.title)
            .build()
    }

    override val layoutResId: Int
        get() = R.layout.fragment_bottom_player_portrait

    override fun initView(savedInstanceState: Bundle?) {
        binding.recyclerViewUnderPlayer.adapter = adapterListM3u8Link
        binding.recyclerViewUnderPlayer.addItemDecoration(
            CustomItemDivider(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                customColor = ContextCompat.getColor(requireContext(), R.color.lines_color)
            )
        )
        binding.recyclerViewComment.adapter = adapterComment
        binding.recyclerViewComment.edgeEffectFactory = RecyclerView.EdgeEffectFactory()
        binding.recyclerViewUnderPlayer.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        titleSkeleton.run()
        adapterListM3u8Link.onItemRecyclerViewCLickListener = { item, position ->
            exoPlayerManager.playVideo(listOf(item))
        }
        binding.formComment.root.setOnClickListener{

        }
    }

    override fun initAction(savedInstanceState: Bundle?) {
        mainViewModel.matchDetail.observe(this) {
            handleGetMatchDetail(it)
        }

        binding.formComment.btnSend.setOnClickListener {
            val text: String = binding.formComment.formComment.text?.toString() ?: return@setOnClickListener
            if (text.isNotEmpty()) {
                adapterComment.onAdd(0, object : IBaseItemComment {
                    override val titleName: String
                        get() = "Anonymous"
                    override val avatarUrl: String
                        get() = "https://firebasestorage.googleapis.com/v0/b/xembongda-a6695.appspot.com/o/app_icon.png?alt=media&token=668a0685-da57-4076-982c-0b49faf349f9"
                    override val commentDetail: String
                        get() = text
                    override val uID: String
                        get() = Firebase.auth.currentUser?.uid ?: UUID.randomUUID().toString()
                    override val systemTime: Long
                        get() = System.currentTimeMillis()

                })
                Log.e("TAG", binding.formComment.formComment.text.toString())
                binding.formComment.formComment.setText("")
                binding.formComment.formComment.clearFocus()
            }

        }

    }

    private fun handleGetMatchDetail(dataState: DataState<FootballMatchWithStreamLink>) {
        when (dataState) {
            is DataState.Loading -> {
                listLinkSkeleton.run()
            }
            is DataState.Success -> {
                listLinkSkeleton.hide {
                    adapterListM3u8Link.onRefresh(dataState.data.linkStreams)
                }
            }
            else -> {

            }
        }
    }

    companion object {
        fun newInstance() {

        }
    }
}
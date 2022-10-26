package com.kt.apps.xembongda.ui.bottomplayerportrat

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentBottomPlayerPortraitBinding
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.model.comments.CommentSpace
import com.kt.apps.xembongda.player.ExoPlayerManager
import com.kt.apps.xembongda.repository.ICommentRepository
import com.kt.apps.xembongda.ui.MainViewModel
import com.kt.apps.xembongda.ui.comment.AdapterComment
import com.kt.apps.xembongda.ui.comment.BaseCommentFootballMatch
import com.kt.apps.xembongda.ui.login.DialogFragmentLogin
import com.kt.apps.xembongda.ui.login.LoginViewModel
import com.kt.skeleton.CustomItemDivider
import com.kt.skeleton.KunSkeleton
import com.kt.skeleton.runLayoutAnimation
import javax.inject.Inject

class FragmentBottomPlayerPortrait : BaseFragment<FragmentBottomPlayerPortraitBinding>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager

    @Inject
    lateinit var commentRepo: ICommentRepository

    private val viewModel by lazy {
        ViewModelProvider(this, factory)[BottomPortraitPlayerViewModel::class.java]
    }

    private val loginViewModel by lazy {
        ViewModelProvider(requireActivity(), factory)[LoginViewModel::class.java]
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
        binding.recyclerViewComment.addItemDecoration(
            CustomItemDivider(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        binding.recyclerViewComment.edgeEffectFactory = RecyclerView.EdgeEffectFactory()
        binding.recyclerViewUnderPlayer.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        titleSkeleton.run()
        adapterListM3u8Link.onItemRecyclerViewCLickListener = { item, position ->
            exoPlayerManager.playVideo(listOf(item))
        }
        binding.formComment.root.setOnClickListener {

        }
    }

    private var loginDialog: DialogFragmentLogin? = null

    override fun initAction(savedInstanceState: Bundle?) {
        clickToDismissKeyboard()
        viewModel.loadComment(CommentSpace.Match(mainViewModel.currentMatch!!))
        mainViewModel.matchDetail.observe(this) {
            handleGetMatchDetail(it)
        }
        viewModel.totalComment.observe(this) {
            handleTotalComment(it)
        }
        loginViewModel.loginDataState.observe(this) {
            when (it) {
                is DataState.Success -> {
                    binding.formComment.userDTO = it.data
                }
                else -> {

                }
            }
        }
        binding.formComment.formComment.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && loginViewModel.isNeedReLogin) {
                showLogin()
                binding.formComment.formComment.clearFocus()
            }
        }

        binding.formComment.btnSend.setOnClickListener {
            if (loginViewModel.isNeedReLogin) {
                showLogin()
                return@setOnClickListener
            }
            val text: String =
                binding.formComment.formComment.text?.toString() ?: return@setOnClickListener
            if (text.isNotEmpty()) {
                val comment = CommentDTO.wrap(text, binding.formComment.userDTO!!)
                viewModel.sendComment(comment, mainViewModel.currentMatch)
                adapterComment.onAddNewComment(comment)
                binding.formComment.formComment.setText("")
                binding.formComment.formComment.clearFocus()
                binding.recyclerViewComment.scrollToPosition(0)
            }

        }
    }

    private fun handleTotalComment(it: DataState<List<BaseCommentFootballMatch>>) {
        when (it) {
            is DataState.Success -> {
                adapterComment.onRefresh(it.data, false)
                binding.recyclerViewComment.runLayoutAnimation()
            }
            else -> {
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun clickToDismissKeyboard() {
        binding.root.setOnTouchListener { v, event ->
            requireActivity().currentFocus?.let { currentFocusView ->
                if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_MOVE && currentFocusView is EditText
                    && !currentFocusView::class.java.name.startsWith("android.webkit.")
                ) {
                    val sourceCoordinator = IntArray(2)
                    currentFocusView.getLocationOnScreen(sourceCoordinator)
                    val x = event.rawX + currentFocusView.left - sourceCoordinator[0]
                    val y = event.rawY + currentFocusView.top - sourceCoordinator[1]
                    if (x < currentFocusView.left || x > currentFocusView.right || y < currentFocusView.top || y > currentFocusView.bottom) {
                        currentFocusView.clearFocus()
                        val imm =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                        imm?.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

    private fun showLogin() {
        if (loginDialog == null) {
            loginDialog = DialogFragmentLogin()
            synchronized(loginDialog!!) {
                loginDialog!!.onDismissListener = {
                    loginDialog = null
                }
            }
            loginDialog?.show(parentFragmentManager, "Login")
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

    override fun onDestroyView() {
        binding.viewModel = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        fun newInstance() {

        }
    }
}
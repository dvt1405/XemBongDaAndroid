package com.kt.apps.xembongda.ui.bottomplayerportrat

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentBottomPlayerPortraitBinding
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.FootballMatchWithStreamLink
import com.kt.apps.xembongda.model.UserDTO
import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.model.comments.CommentSpace
import com.kt.apps.xembongda.model.highlights.HighLightDTO
import com.kt.apps.xembongda.model.highlights.HighLightDetail
import com.kt.apps.xembongda.player.ExoPlayerManager
import com.kt.apps.xembongda.ui.MainViewModel
import com.kt.apps.xembongda.ui.comment.AdapterComment
import com.kt.apps.xembongda.ui.comment.BaseCommentFootballMatch
import com.kt.apps.xembongda.ui.highlight.FragmentHighlightViewModel
import com.kt.apps.xembongda.ui.login.DialogFragmentLogin
import com.kt.apps.xembongda.ui.login.LoginViewModel
import com.kt.skeleton.CustomItemDivider
import com.kt.skeleton.KunSkeleton
import com.kt.skeleton.runLayoutAnimation
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FragmentBottomPlayerPortrait : BaseFragment<FragmentBottomPlayerPortraitBinding>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager

    private val viewModel by lazy {
        ViewModelProvider(this, factory)[BottomPortraitPlayerViewModel::class.java]
    }

    private val loginViewModel by lazy {
        ViewModelProvider(requireActivity(), factory)[LoginViewModel::class.java]
    }

    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    private val highLightViewModel by lazy {
        ViewModelProvider(requireActivity(), factory)[FragmentHighlightViewModel::class.java]
    }

    private val adapterListM3u8Link by lazy { AdapterListM3u8Link() }
    private val adapterComment by lazy { AdapterComment() }

    private val listLinkSkeleton by lazy {
        KunSkeleton.bind(binding.recyclerViewUnderPlayer)
            .adapter(adapterListM3u8Link)
            .layoutManager(
                LinearLayoutManager(
                    requireContext().applicationContext,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            .layoutItem(R.layout.item_link_stream_skeleton)
            .build()
    }

    private val type by lazy {
        when(arguments?.getInt(EXTRA_TYPE)) {
            Type.HighLight.value -> Type.HighLight
            else -> Type.LiveStream
        }
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
        if (savedInstanceState == null) {
            loadComments()
        }
        registerVideoLink()
        viewModel.totalComment.observe(this) {
            handleTotalComment(it)
        }
        mainViewModel.commentCount.observe(this) {
            binding.formComment.commentCount.text = it.toString()
        }
        loginViewModel.loginDataState.observe(this) {
            handleLogin(it)
        }
        binding.formComment.formComment.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && loginViewModel.isNeedReLogin) {
                showLogin()
                binding.formComment.formComment.clearFocus()
            }
        }

        binding.formComment.btnSend.clicks().throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if (loginViewModel.isNeedReLogin) {
                    showLogin()
                    return@subscribe
                }
                val text: String =
                    binding.formComment.formComment.text?.toString() ?: return@subscribe
                if (text.isNotEmpty()) {
                    val comment = CommentDTO.wrap(text, binding.formComment.userDTO!!)
                    sendComment(comment)
                    adapterComment.onAddNewComment(comment)
                    binding.formComment.formComment.setText("")
                    binding.formComment.formComment.clearFocus()
                    binding.recyclerViewComment.scrollToPosition(0)
                }
            }, {

            })
        viewModel.sendComment.observe(this) {
            handleSendComment(it)
        }

        binding.formComment.btnReceiveComment.clicks()
            .throttleFirst(300, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                requestRewardedAds("Nhận thêm bình luận")
            }, {
                Log.e("TAG", it.message, it)
            })
    }

    private fun handleLogin(it: DataState<UserDTO>?) {
        when (it) {
            is DataState.Success -> {
                binding.formComment.userDTO = it.data
                loadComments()
            }
            else -> {

            }
        }
    }

    private fun registerVideoLink() {
        if (type == Type.HighLight) {
            highLightViewModel.highLightDetail.observe(this) {
                handleGetHighLighDetail(it)
            }
        } else {
            mainViewModel.matchDetail.observe(this) {
                handleGetMatchDetail(it)
            }
        }
    }

    private fun handleGetHighLighDetail(dataState: DataState<HighLightDetail>) {
        when(dataState) {
            is DataState.Loading -> {
                listLinkSkeleton.run()
            }

            is DataState.Success -> {
                listLinkSkeleton.hide {
                    adapterListM3u8Link.onRefresh(dataState.data.linkStreamWithReferer)
                }
            }

            is DataState.Error -> {

            }
            else -> {

            }
        }
    }

    private fun sendComment(commentDTO: CommentDTO) {
        if (type == Type.LiveStream) {
            viewModel.sendComment(commentDTO, mainViewModel.currentMatch)
        } else {
            requireArguments().getParcelable<HighLightDTO>(EXTRA_HIGH_LIGHT)?.let {
                viewModel.sendCommentHighLight(commentDTO, it)
            }
        }
    }

    private fun loadComments() {
        if (type == Type.LiveStream) {
            mainViewModel.currentMatch?.let {
                viewModel.loadComment(CommentSpace.Match(it))
            }
        } else {
            requireArguments().getParcelable<HighLightDTO>(EXTRA_HIGH_LIGHT)?.let {
                viewModel.loadCommentHighLight(it)
            }
        }
    }

    private fun handleSendComment(dataState: DataState<CommentDTO>) {
        when (dataState) {
            is DataState.Error -> {
                requestRewardedAds("Hết lượt bình luận")
            }
            else -> {}
        }
    }

    private fun requestRewardedAds(title: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage("Bạn có muốn nhận thêm lượt bình luận?")
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                App.get().rewardedAdsManager.loadAds()
                    .subscribe ({
                        activity?.let { it1 ->
                            it.show(it1) {
                                mainViewModel.increaseComment(it)
                            }
                        }
                    }, {
                        mainViewModel.increaseComment(1)
                    })
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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

    companion object {
        private const val EXTRA_TYPE = "extra:type"
        private const val EXTRA_HIGH_LIGHT = "extra:highlight"
        fun newInstance(type: Type = Type.LiveStream, vararg extra: Any): FragmentBottomPlayerPortrait {
            return FragmentBottomPlayerPortrait().apply {
                this.arguments = bundleOf().apply {
                    putInt(EXTRA_TYPE, type.value)
                    extra.forEach {
                        if (it is HighLightDTO) {
                            putParcelable(EXTRA_HIGH_LIGHT, it)
                        }
                    }
                }
            }
        }
    }

    enum class Type(val value: Int) {
        LiveStream(0), HighLight(1)
    }
}
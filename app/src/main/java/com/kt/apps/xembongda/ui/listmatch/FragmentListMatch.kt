package com.kt.apps.xembongda.ui.listmatch

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.webkit.ClientCertRequest
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kt.apps.xembongda.App
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.ads.AdsInterstitialManager
import com.kt.apps.xembongda.base.BaseFragment
import com.kt.apps.xembongda.databinding.FragmentListMatchBinding
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.repository.config.FootballRepoSourceFrom
import com.kt.apps.xembongda.ui.MainViewModel
import com.kt.apps.xembongda.ui.webview.WebView
import com.kt.apps.xembongda.utils.gone
import com.kt.apps.xembongda.utils.showErrorDialog
import com.kt.apps.xembongda.utils.visible
import com.kt.skeleton.CustomItemDivider
import com.kt.skeleton.KunSkeleton
import javax.inject.Inject

class FragmentListMatch : BaseFragment<FragmentListMatchBinding>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var adsInterstitialManager: AdsInterstitialManager

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[FragmentListMatchViewModel::class.java]
    }

    private val mainActivityViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    }

    private val adapter by lazy {
        if (sourceFrom == FootballRepoSourceFrom.BinhLuan91) {
            AdapterFootballMatch()
        } else {
            AdapterFootballMatch()
        }
    }
    private val skeleton by lazy {
        KunSkeleton.bind(binding.recyclerView)
            .adapter(adapter)
            .layoutItem(R.layout.item_football_match_skeleton)
            .build()
    }

    private val sourceFrom: FootballRepoSourceFrom by lazy {
        FootballRepoSourceFrom.valueOf(
            requireArguments().getString(
                EXTRA_SOURCE_FROM,
                FootballRepoSourceFrom.Phut91.name
            )
        )
    }

    override val layoutResId: Int
        get() = R.layout.fragment_list_match

    var retry = 0

    override fun initView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            viewModel.getListFootBallMatch(sourceFrom)
        }
        skeleton.run()
        binding.recyclerView.addItemDecoration(
            CustomItemDivider(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
    }

    private var currentMatch: FootballMatch? = null
    private var step = 1
    private var isLoadingWebView = false
    private fun backupUseWebview() {
        mainActivityViewModel.matchDetail.observe(this) {
            when (it) {
                is DataState.Error -> {
                    if (!isLoadingWebView) {
                        if (sourceFrom == FootballRepoSourceFrom.Phut91) return@observe
                        val detailUrl = if (currentMatch!!.detailPage.startsWith("http")) {
                            currentMatch?.detailPage
                        } else {
                            "https://mitom.90phut6.live${currentMatch!!.detailPage.removePrefix("/")}"
                        }
                        retry++
                        loadFromWebView(detailUrl!!, 2)
                    }
                }
                is DataState.Success -> {
                    if (retry >= 10) {
                        return@observe
                    }
                    retry++
                    if (it.data.linkStreams.isEmpty() && sourceFrom == FootballRepoSourceFrom.MiTom) {
                        val detailUrl = if (currentMatch!!.detailPage.startsWith("http")) {
                            currentMatch?.detailPage
                        } else {
                            "https://mitom.90phut6.live${currentMatch!!.detailPage.removePrefix("/")}"
                        }
                        loadFromWebView(detailUrl!!, 2)
                    }
                }
                else -> {

                }
            }
        }
        binding.webView.addJavascriptInterface(JavaScript {
            if (step == 1) {
                viewModel.getListFootballMatchFromHtml(sourceFrom, it)
            } else {
                mainActivityViewModel.getFootballMatchDetail(currentMatch!!, it)
            }
        }, "HtmlOut")
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebView()
        binding.webView.settings.allowContentAccess = true
        binding.webView.settings.allowFileAccess = true
        //        exoPlayerManager.trustEveryone()
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.settings.userAgentString = "Chrome"
        binding.webView.settings.domStorageEnabled = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: android.webkit.WebView?,
                request: WebResourceRequest?
            ): Boolean {
                Log.e("TAG", "shouldOverrideUrlLoading")
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.e("TAG", url!!)
                view?.loadUrl(
                    "javascript:window.HtmlOut.getHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
                )
            }

            override fun onLoadResource(view: android.webkit.WebView?, url: String?) {
                super.onLoadResource(view, url)
                Log.e("TAG", "onLoadResource")
            }

            override fun onPageCommitVisible(view: android.webkit.WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                Log.e("TAG", "onPageCommitVisible")
                Log.e("TAG", "Url: ${url}")

            }

            override fun onReceivedClientCertRequest(
                view: android.webkit.WebView?,
                request: ClientCertRequest?
            ) {
                super.onReceivedClientCertRequest(view, request)
                Log.e("ClientCertRequest", request?.host ?: "Host")
            }

            override fun onFormResubmission(
                view: android.webkit.WebView?,
                dontResend: Message?,
                resend: Message?
            ) {
                super.onFormResubmission(view, dontResend, resend)
                Log.e("TAG", "${resend?.what ?: 0}")
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadFromWebView(url: String = "https://mitom.90phut6.live/", step: Int = 1) {
        isLoadingWebView = true
        this.step = step
        binding.webView.visible()
        binding.webView.loadUrl(url)
    }

    class JavaScript (private val onLoadHtml: (html: String) -> Unit) {
        @JavascriptInterface
        fun getHtml(html: String) {
            logLongText("JavascriptInterface", html)
            onLoadHtml(html)
        }

        fun logLongText(tag: String, str: String) {
            if (str.length < 1000) {
                Log.e(tag, str)
            } else {
                var index = 0
                var subStr : String
                var range = 0

                while (str.length >= index + range) {
                    range = if (str.length - index > 1000) 1000 else {
                        str.length - index
                    }
                    Log.e(tag, "${index}")
                    subStr = str.substring(index, index + range)
                    index+=range
                    Log.e(tag, subStr)
                }

            }
        }
    }

    override fun initAction(savedInstanceState: Bundle?) {
        viewModel.listMatch.observe(this) {
            handleListMatch(it)
        }
        adapter.onItemRecyclerViewCLickListener = { item, position ->
            adsInterstitialManager.loadAds(requireActivity())
            currentMatch = item
            adapter.pauseAds()
            isLoadingWebView = false
            mainActivityViewModel.getFootballMatchDetail(item)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getListFootBallMatch(sourceFrom)
        }
    }

    private fun handleListMatch(listMatch: DataState<List<FootballMatch>>) {
        when (listMatch) {
            is DataState.Success -> {
                binding.webView.gone()
                skeleton.hide {
                    adapter.onRefresh(listMatch.data)
                    binding.swipeRefreshLayout.isEnabled = true
                }
            }
            is DataState.Loading -> {
                skeleton.run()
                binding.swipeRefreshLayout.isRefreshing = false
                binding.swipeRefreshLayout.isEnabled = false

            }
            is DataState.Error -> {
                showErrorDialog(content = "Thử tải lại trang để cập nhật link mới nhất")
                binding.swipeRefreshLayout.isEnabled = true
                binding.swipeRefreshLayout.isRefreshing = false
            }
            else -> {
                binding.swipeRefreshLayout.isEnabled = true
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        App.get().adsLoaderManager.unregister(adapter)
        adapter.clearAds()
        binding.recyclerView.adapter = null
    }

    override fun onDestroy() {
        retry = 0
        App.get().adsLoaderManager.unregister(adapter)
        adapter.clearAds()
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_SOURCE_FROM = "extra:source_from"
        fun newInstance(sourceFrom: FootballRepoSourceFrom) = FragmentListMatch().apply {
            this.arguments = Bundle().apply {
                this.putString(EXTRA_SOURCE_FROM, sourceFrom.name)
            }
        }
    }
}
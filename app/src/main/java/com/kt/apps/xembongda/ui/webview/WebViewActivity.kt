package com.kt.apps.xembongda.ui.webview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.webkit.ClientCertRequest
import android.webkit.WebResourceRequest
import android.webkit.WebViewClient
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.base.BaseActivity
import com.kt.apps.xembongda.databinding.ActivityWebViewBinding
import com.kt.apps.xembongda.ui.listmatch.FragmentListMatch

class WebViewActivity : BaseActivity<ActivityWebViewBinding>() {
    override val layoutRes: Int
        get() = R.layout.activity_web_view

    override fun initView(savedInstanceState: Bundle?) {
        binding.webView.addJavascriptInterface(FragmentListMatch.JavaScript {

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
        binding.webView.loadUrl("https://mitom.90phut6.live/")
    }

    override fun initAction(savedInstanceState: Bundle?) {
    }

}
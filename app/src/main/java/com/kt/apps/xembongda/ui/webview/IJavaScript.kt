package com.kt.apps.xembongda.ui.webview

import android.util.Log
import android.webkit.JavascriptInterface

class IJavaScript(private val onLoadHtml: (html: String) -> Unit) {

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
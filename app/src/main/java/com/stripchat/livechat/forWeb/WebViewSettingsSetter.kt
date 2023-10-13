@file:Suppress("DEPRECATION")

package com.stripchat.livechat.forWeb

import android.util.Log
import android.webkit.WebSettings

class WebViewSettingsSetter(private val settings: WebSettings) {

    private val getLoadDefault
        get() = WebSettings.LOAD_DEFAULT
    private val getMixedDefault
        get() = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

    private val getChangeReplaceDefault
        get() = "; wv"
    private val getChangeNewDefault
        get() = ""

    fun setAllows(value: Boolean): WebViewSettingsSetter {
        settings.allowFileAccessFromFileURLs = value
        settings.allowFileAccess = value
        settings.allowContentAccess = value
        settings.allowUniversalAccessFromFileURLs = value
        return this
    }

    fun setEnables(value: Boolean): WebViewSettingsSetter {
        settings.databaseEnabled = value
        settings.domStorageEnabled = settings.databaseEnabled
        settings.javaScriptEnabled = settings.domStorageEnabled
        return this
    }

    fun setOther(value: Boolean): WebViewSettingsSetter {
        settings.useWideViewPort = value
        settings.loadWithOverviewMode = settings.useWideViewPort
        return this
    }

    fun setNumeral(values: List<Int>): WebViewSettingsSetter {
        if(values.size > 1) {
            settings.mixedContentMode = values[0]
            settings.cacheMode = values[1]
        }
        else if (values.size == 1) {
            settings.mixedContentMode = values[0]
            settings.cacheMode = getLoadDefault
        }
        else {
            settings.mixedContentMode = getMixedDefault
            settings.cacheMode = getLoadDefault
        }
        return this
    }

    fun changeString(values: List<String>): WebViewSettingsSetter {
        if(values.size > 1) {
            settings.userAgentString = settings.userAgentString.replace(values[0], values[1])
        }
        else if (values.size == 1) {
            Log.w(getLogTag, "Be careful! String values size is 1.")
            settings.userAgentString = settings.userAgentString
                .replace(values[0], getChangeNewDefault)
        }
        else {
            Log.w(getLogTag, "Be careful! String values size is 0.")
            settings.userAgentString = settings.userAgentString
                .replace(getChangeReplaceDefault, getChangeNewDefault)
        }
        return this
    }

    private val getLogTag
        get() = "WebViewSettingsSetter"
}
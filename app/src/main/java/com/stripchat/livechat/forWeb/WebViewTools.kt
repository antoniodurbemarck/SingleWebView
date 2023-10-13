package com.stripchat.livechat.forWeb

import android.net.Uri
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher
import com.stripchat.livechat.WebActivity
import androidx.activity.OnBackPressedCallback as onBack

class WebViewTools(
    val webView: WebView,
    private val activity: WebActivity
) {

    private val webViewSettingsSetter: WebViewSettingsSetter

    private var boolValue = false
    private var intValues = listOf<Int>()
    private var stringValues = listOf<String>()
    private lateinit var launcher: ActivityResultLauncher<String>
    private lateinit var valueCallbackSetter: (ValueCallback<Array<Uri>>) -> Unit

    init {
        activity.onSaveInstanceStateCallback = {
            webView.saveState(it)
        }
        activity.onRestoreInstanceStateCallback = {
            webView.restoreState(it)
        }
        activity.onBackPressedDispatcher.addCallback(OnBackPressedCallback())
        webViewSettingsSetter = WebViewSettingsSetter(webView.settings)
    }

    fun startInitialization(
        boolValue: Boolean,
        intValues: List<Int>,
        stringValues: List<String>,
        launcher: ActivityResultLauncher<String>,
        valueCallbackSetter: (ValueCallback<Array<Uri>>) -> Unit
    ): WebViewTools {
        this.boolValue = boolValue
        this.intValues = intValues
        this.stringValues = stringValues
        this.launcher = launcher
        this.valueCallbackSetter = valueCallbackSetter
        return this
    }

    fun setSettings(): WebViewTools {
        webViewSettingsSetter
            .setAllows(boolValue)
            .setEnables(boolValue)
            .setOther(boolValue)
            .setNumeral(intValues)
            .changeString(stringValues)
        return this
    }

    fun assignClients(): WebViewTools {
        webView.webViewClient = MyWebViewClient()
        webView.webChromeClient = MyWebChromeClient(valueCallbackSetter, launcher)
        return this
    }

    fun loadUrl(url: String?) {
        webView.loadUrl(url ?: "https://google.com".apply {
            Log.e(getLogTag, "[loadUrl] Url is null. Google search page loaded.")
        })
    }

    inner class OnBackPressedCallback : onBack(!activity.isDestroyed) {
        override fun handleOnBackPressed() {
            if(webView.canGoBack()) {
                webView.goBack()
            }
            else {
                activity.finish()
            }
        }
    }

    private val getLogTag
        get() = "Web view tools"
}
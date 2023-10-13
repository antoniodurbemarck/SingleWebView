package com.stripchat.livechat.forWeb

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.JsResult
import android.webkit.SafeBrowsingResponse
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.ActivityResultLauncher

class MyWebViewClient: WebViewClient() {
    private var someInfoContainer: SomeInfoContainer? = null
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val uri = request.url.toString()
        return if (uri.contains("/")) {
            Log.e("Uri", uri)
            !uri.contains("http")
        } else true
    }

    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
        when {
            event == null -> Log.i(getLogTag, "[shouldOverrideKeyEvent] null.")
            event.keyCode == KeyEvent.KEYCODE_BACK -> {
                Log.i(getLogTag, "[shouldOverrideKeyEvent] back pressed.")
            }
            event.keyCode == KeyEvent.KEYCODE_ALT_LEFT ||
                    event.keyCode == KeyEvent.KEYCODE_ALT_RIGHT -> {
                Log.i(getLogTag, "[shouldOverrideKeyEvent] alt pressed.")
            }
            event.keyCode == KeyEvent.KEYCODE_CTRL_LEFT ||
                    event.keyCode == KeyEvent.KEYCODE_CTRL_RIGHT -> {
                Log.i(getLogTag, "[shouldOverrideKeyEvent] control pressed.")
            }
            event.keyCode == KeyEvent.KEYCODE_SHIFT_LEFT ||
                    event.keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                Log.i(getLogTag, "[shouldOverrideKeyEvent] shift pressed.")
            }
            else -> {
                Log.i(getLogTag, "[shouldOverrideKeyEvent] key with code ${event.keyCode} pressed.")
            }
        }
        someInfoContainer?.lastKeyEvent = event
        return false
    }

    inner class SomeInfoContainer {
        var lastKeyEvent: KeyEvent? = null
        var formResubmissionTimes = 0
        var lastSafeBrowsingHitThreatType = 0
        var lastSafeBrowsingHitCallbackIsNull = false
    }

    override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
        Log.i(getLogTag, "[onFormResubmission] don'tResend: $dontResend, resend: $resend.")
        someInfoContainer?.formResubmissionTimes?.inc()
        super.onFormResubmission(view, dontResend, resend)
    }

    override fun onSafeBrowsingHit(
        view: WebView?,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ) {
        someInfoContainer = (someInfoContainer ?: SomeInfoContainer()).apply {
            lastSafeBrowsingHitCallbackIsNull = callback == null
            lastSafeBrowsingHitThreatType = threatType
        }
        super.onSafeBrowsingHit(view, request, threatType, callback)
    }

    private val getLogTag
        get() = "My web view client"
}

class MyWebChromeClient(
    private val valueCallbackSetter: (ValueCallback<Array<Uri>>) -> Unit,
    private val launcher: ActivityResultLauncher<String>
): WebChromeClient() {
    private var fileChooserShownTimes = 0
    private var receivedItems = 0
        set(value) {
            field = value
            Log.i(getLogTag, "[setReceivedItems] $field")
        }
    private var lastJSAlert: Pair<String?, JsResult?> = null to null
    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        fileChooserShownTimes++
        Log.i(getLogTag, "[onShowFileChooser] times: $fileChooserShownTimes")
        valueCallbackSetter(filePathCallback)
        launcher.launch(Manifest.permission.CAMERA)
        return true
    }

    override fun getVideoLoadingProgressView(): View? {
        Log.i(getLogTag, "[getVideoLoadingProgressView] Was get.")
        return super.getVideoLoadingProgressView()
    }


    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        receivedItems ++
        super.onReceivedIcon(view, icon)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        receivedItems ++
        super.onReceivedTitle(view, title)
    }

    override fun onReceivedTouchIconUrl(view: WebView?, url: String?, precomposed: Boolean) {
        receivedItems ++
        super.onReceivedTouchIconUrl(view, url, precomposed)
    }

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        lastJSAlert = message to result
        Log.i(getLogTag, "[onJsAlert] message: $message, result: $result")
        return super.onJsAlert(view, url, message, result)
    }

    private val getLogTag
        get() = "My web chrome client"
}
package com.stripchat.livechat

import android.net.Uri
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebSettings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.stripchat.livechat.forWeb.ActivityResultHelper
import com.stripchat.livechat.forWeb.WebViewTools

class WebActivity : AppCompatActivity() {
    var valCall: ValueCallback<Array<Uri>>? = null
    var uriCall: Uri? = null
    private var yourDest: String? = null
    private lateinit var webViewTools: WebViewTools
    private val activityResultHelper = ActivityResultHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DefaultInitializations.initializeOneSignal(applicationContext)
        webViewTools = WebViewTools(findViewById(R.id.webview), this)
        yourDest = "https://hotgames.shop/DzFYg3" // TODO
        webViewTools.startInitialization(
            true,
            listOf(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW, WebSettings.LOAD_DEFAULT),
            listOf("; wv"),
            launcherFirst
        ) { valCall = it }.apply {
            CookieManager.getInstance().apply {
                if(!acceptCookie() || !acceptThirdPartyCookies(webView)) {
                    setAcceptCookie(true)
                    setAcceptThirdPartyCookies(webView, acceptCookie())
                }
            }
        }.assignClients().setSettings().loadUrl(yourDest)
    }

    private val launcherFirst = registerForActivityResult (
        ActivityResultContracts.RequestPermission()
    ) {
        activityResultHelper.doForString(this)
    }

    val launcherSecond = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        activityResultHelper.doForIntent(this, it)
    }

    var onSaveInstanceStateCallback: ((Bundle) -> Unit)? = null
    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        onSaveInstanceStateCallback?.invoke(outState)
    }

    var onRestoreInstanceStateCallback: ((Bundle) -> Unit)? = null
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        onRestoreInstanceStateCallback?.invoke(savedInstanceState)
    }
}
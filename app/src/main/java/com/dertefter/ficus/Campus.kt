package com.dertefter.ficus

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import com.google.android.material.appbar.MaterialToolbar


class Campus : AppCompatActivity() {
    var wv: WebView? = null
    var spinner: ProgressBar? = null
    var toolbar: MaterialToolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.campus_screen)
        wv = findViewById(R.id.wv_campus)
        toolbar = findViewById(R.id.toolbar_campus)
        toolbar?.addSystemWindowInsetToPadding(top = true)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        spinner = findViewById(R.id.spinner_campus)
        wv?.settings?.javaScriptEnabled = true
        wv?.settings?.blockNetworkLoads = false
        wv?.settings?.allowFileAccess = true
        wv?.settings?.loadWithOverviewMode = true
        wv?.settings?.useWideViewPort = true
        wv?.settings?.domStorageEnabled = true
        wv?.settings?.blockNetworkImage = false
        wv?.settings?.loadsImagesAutomatically = true
        wv?.settings?.forceDark = WebSettings.FORCE_DARK_OFF
        wv?.settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW


        wv?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                view.loadUrl(url!!)
                return true
            }
            override fun onPageFinished(view: WebView, url: String) {
                spinner?.visibility = View.GONE
                wv?.visibility = View.VISIBLE
                val anim = ObjectAnimator.ofFloat(wv, "alpha", 0f, 100f)
                anim.duration = 4400
                anim.start()
            }
        }
        wv?.visibility = View.INVISIBLE
        wv?.loadUrl("https://nstu.ru/campus/")

    }

    fun View.addSystemWindowInsetToPadding(
        left: Boolean = false,
        top: Boolean = false,
        right: Boolean = false,
        bottom: Boolean = false
    ) {
        val (initialLeft, initialTop, initialRight, initialBottom) =
            listOf(paddingLeft, paddingTop, paddingRight, paddingBottom)

        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            view.updatePadding(
                left = initialLeft + (if (left) insets.systemWindowInsetLeft else 0),
                top = initialTop + (if (top) insets.systemWindowInsetTop else 0),
                right = initialRight + (if (right) insets.systemWindowInsetRight else 0),
                bottom = initialBottom + (if (bottom) insets.systemWindowInsetBottom else 0)
            )

            insets
        }
    }
}
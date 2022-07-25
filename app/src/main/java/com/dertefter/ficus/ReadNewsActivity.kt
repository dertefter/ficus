package com.dertefter.ficus

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import com.google.android.material.appbar.MaterialToolbar

class ReadNewsActivity : AppCompatActivity() {
    var wv: WebView? = null
    var toolbar: MaterialToolbar? = null
    var spinner: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.read_news_layout)
        spinner = findViewById(R.id.spinner_read_news)
        toolbar = findViewById(R.id.toolbar_read_news)
        toolbar?.addSystemWindowInsetToPadding(top = true)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        wv = findViewById(R.id.newsWebView)
        wv?.settings?.javaScriptEnabled = true
        val link: String = intent.getStringExtra("link").toString()
        wv?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                wv?.visibility = View.VISIBLE
                val anim = ObjectAnimator.ofFloat(wv, "alpha", 0f, 100f)
                anim.duration = 4400
                anim.start()
                spinner?.visibility = View.GONE
            }
        }
        wv?.visibility = View.INVISIBLE
        wv?.loadUrl(link)

    }
    private fun View.addSystemWindowInsetToPadding(
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

    private fun View.addSystemWindowInsetToMargin(
        left: Boolean = false,
        top: Boolean = false,
        right: Boolean = false,
        bottom: Boolean = false
    ) {
        val (initialLeft, initialTop, initialRight, initialBottom) =
            listOf(marginLeft, marginTop, marginRight, marginBottom)

        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            view.updateLayoutParams {
                (this as? ViewGroup.MarginLayoutParams)?.let {
                    updateMargins(
                        left = initialLeft + (if (left) insets.systemWindowInsetLeft else 0),
                        top = initialTop + (if (top) insets.systemWindowInsetTop else 0),
                        right = initialRight + (if (right) insets.systemWindowInsetRight else 0),
                        bottom = initialBottom + (if (bottom) insets.systemWindowInsetBottom else 0)
                    )
                }
            }

            insets
        }
    }
}
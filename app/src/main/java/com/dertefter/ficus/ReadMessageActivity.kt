package com.dertefter.ficus

import AppPreferences
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit

class ReadMessageActivity : AppCompatActivity() {
    var toolbar: MaterialToolbar? = null
    var text: TextView? = null
    var fab: FloatingActionButton? = null
    var scrollView: ScrollView? = null

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


    private fun deleteThis(MessageID: String) {
        var tokenId = AppPreferences.token
        val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Cookie", "NstuSsoToken=$tokenId")
                    .build()
                chain.proceed(authorized)
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://ciu.nstu.ru/student_study/mess_teacher/ajax_del_mes/")
            .client(client)
            .build()
        val service = retrofit.create(APIService::class.java)
        val params = HashMap<String?, String?>()
        params["idmes"] = MessageID
        params["what"] = "1"
        params["type"] = "1"
        params["vid_sort"] = "1"
        params["year"] = "-1"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.postForm(params)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        finish()
                    }
                }
            } catch (e: Throwable) {
                Snackbar.make(
                    findViewById(R.id.read_message_layout),
                    "Ошибка! Попробуйте позже...",
                    Snackbar.LENGTH_SHORT
                ).setTextColor(getColor(R.color.md_theme_dark_inverseSurface))
                    .show()
            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.read_message_layout)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        scrollView = findViewById(R.id.read_mes_scroll_view)
        scrollView?.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            if (i2 <= i4) {
                ObjectAnimator.ofFloat(fab, "translationX", 2000f).start()
            } else {
                ObjectAnimator.ofFloat(fab, "translationX", 0f).start()
            }
        }
        var messageID: String = intent.getStringExtra("mesid")!!
        toolbar = findViewById(R.id.toolbar_read_message)
        text = findViewById(R.id.read_message_text)
        fab = findViewById(R.id.delete_this)
        fab?.addSystemWindowInsetToMargin(bottom = true)
        toolbar?.addSystemWindowInsetToPadding(top = true)
        val get_theme = intent.getStringExtra("theme")
        val get_text = intent.getStringExtra("text")
        val get_send_by = intent.getStringExtra("send_by")
        toolbar?.title = get_send_by
        toolbar?.subtitle = get_theme
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        text?.text = get_text
        fab?.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Вы уверены?")
                .setNegativeButton("Отмена") { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("Удалить") { dialog, which ->
                    deleteThis(messageID)
                }
                .show()
        }


    }
}
package com.dertefter.ficus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Retrofit


class Books : AppCompatActivity() {
    var spinner: ProgressBar? = null
    var toolbar: MaterialToolbar? = null
    var booksView: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.books_screen)

        toolbar = findViewById(R.id.toolbar_books)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        toolbar?.addSystemWindowInsetToPadding(top = true)
        spinner = findViewById(R.id.spinner_books)
        val mInflater = LayoutInflater.from(this)
        booksView = findViewById(R.id.booksView)
        val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Accept-Language", "ru,en;q=0.9")
                    .build()
                chain.proceed(authorized)
            })
            .build()
        val urlKoha = "https://koha.library.nstu.ru/"
        val retrofit = Retrofit.Builder()
            .baseUrl(urlKoha)
            .client(client)
            .build()
        val service = retrofit.create(APIService::class.java)
        val params = HashMap<String?, String?>()
        params["koha_login_context:"] = "opac"
        params["userid"] = AppPreferences.login!!
        params["password"] = AppPreferences.password!!
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.authBooks(params)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    spinner?.visibility = View.GONE
                    val pretty = response.body()?.string().toString()
                    val doc: Document = Jsoup.parse(pretty)
                    val tbody = doc.body().select("tbody")

                    for (book in tbody.select("tr")){
                        spinner?.visibility = View.GONE
                        val title = book.select("span.biblio-title").text().toString()
                        val subtitle = book.select("span.subtitle").text().toString()
                        val author = book.select("td.author").text().toString().replace(",", "")
                        val renew = book.select("td.renew").text().toString().replace(",", "")
                        val getback = book.select("td.date_due").text().toString().replace("/", ".")
                        val fines = book.select("td.fines").first().text().toString()
                        //Log.e("Жабья попка ", "$title , $subtitle - $author - $getback - $fines - $renew")

                        var item: View = mInflater.inflate(R.layout.item_book, null, false)
                        item.findViewById<TextView>(R.id.book_title).text = title
                        item.findViewById<TextView>(R.id.book_subtitle).text = subtitle
                        item.findViewById<TextView>(R.id.book_autor).text = author
                        item.findViewById<TextView>(R.id.renew).text = "Продление: $renew"
                        item.findViewById<TextView>(R.id.getback).text = getback
                        item.findViewById<TextView>(R.id.fines).text = fines
                        booksView?.addView(item)

                    }

                }
            }
        }

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


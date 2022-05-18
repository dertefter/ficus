package com.dertefter.nstumobile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.myapplication.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Retrofit

class Score : Fragment(R.layout.fragment_score) {
    var toolbar: Toolbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
                val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Cookie", "NstuSsoToken="+AppPreferences.token)
                    .build()
                chain.proceed(authorized)
            })
            .build()

        val url1 = "https://ciu.nstu.ru/student_study/student_info/progress/"
        var retrofit = Retrofit.Builder()
            .baseUrl(url1)
            .client(client)
            .build()
        val service = retrofit.create(APIService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.Study()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val pretty = response.body()?.string().toString()
                    //Log.e("pretty-score", pretty)
                    val doc: Document = Jsoup.parse(pretty)
                    val s = doc.body().select("div.sysContentWithMenu").toString()

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }


}
package com.dertefter.nstumobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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

class Messages : Fragment(R.layout.messages_fragment) {
    var messagesView: LinearLayout? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messagesView = view.findViewById(R.id.messages_view)
        val mInflater = LayoutInflater.from(activity)
        val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Cookie", "NstuSsoToken="+AppPreferences.token)
                    .build()
                chain.proceed(authorized)
            })
            .build()

        val url1 = "https://ciu.nstu.ru/student_study/"
        var retrofit = Retrofit.Builder()
            .baseUrl(url1)
            .client(client)
            .build()
        val service = retrofit.create(APIService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.messages("-1")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val pretty = response.body()?.string().toString()
                    val doc: Document = Jsoup.parse(pretty)
                    val s = doc.body().select("div.sysContentWithMenu")
                    val tbody = s.select("tbody").first()
                    val messages = tbody.select("tr")
                    for (i in messages){
                        var message_item: View = mInflater.inflate(R.layout.message, null, false)
                        message_item.findViewById<TextView>(R.id.send_by).text = i.select("span")[0].text().toString().replace("(преподаватель)", "")
                        message_item.findViewById<TextView>(R.id.message_text).text = i.select("div").text().toString()
                        message_item.findViewById<ImageView>(R.id.send_by_image).setImageResource(R.drawable.ic_avatar)
                        messagesView?.addView(message_item)

                    }

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }
}
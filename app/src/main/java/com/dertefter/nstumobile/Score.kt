package com.dertefter.nstumobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
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
    var scoreView: LinearLayout? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mInflater = LayoutInflater.from(activity)
        scoreView = view.findViewById(R.id.score_view)
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
                    val doc: Document = Jsoup.parse(pretty)
                    val s = doc.body().select("div.sysContentWithMenu")
                    var tables = s.select("table.tdall")
                    var count = 0
                    var size = tables.size
                    for (i in 1..size - 1){
                        count++
                        var table_count_textView: TextView = TextView(Work.applicationContext())
                        table_count_textView.text = "$count семестр"
                        scoreView?.addView(table_count_textView)
                        var trs = tables[i].select("tr.last_progress")
                        for (j in trs){
                            var item: View = mInflater.inflate(R.layout.score_item, null, false)
                            item.findViewById<TextView>(R.id.subject).text = j.select("td")[1].ownText().toString()
                            item.findViewById<TextView>(R.id.date_subject).text = j.select("td")[2].ownText().toString()
                            item.findViewById<TextView>(R.id.score_count).text = j.select("td")[3].select("span").first().ownText().toString()
                            item.findViewById<TextView>(R.id.score_5).text = j.select("td")[4].select("span").first().ownText().toString()
                            item.findViewById<TextView>(R.id.score_5).text = j.select("td")[5].select("span").first().ownText().toString()
                            scoreView?.addView(item)
                        }


                    }


                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }


}
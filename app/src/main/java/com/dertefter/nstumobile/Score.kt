package com.dertefter.nstumobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
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

    @ColorInt
    fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

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
                        var table_count_textView: TextView = TextView(Auth.applicationContext())
                        table_count_textView.text = " $count семестр"
                        table_count_textView.setTextColor(context?.getColorFromAttr(com.google.android.material.R.attr.colorOnSurface)!!)
                        table_count_textView.setTextSize(24f)
                        val scoreCard: View = mInflater.inflate(R.layout.score_card, null, false)
                        val scoreCardBody = scoreCard.findViewById<LinearLayout>(R.id.score_card_body)
                        scoreCardBody.addView(table_count_textView)
                        val trs = tables[i].select("tr.last_progress")
                        val item: View = mInflater.inflate(R.layout.score_item, null, false)
                        item.findViewById<TextView>(R.id.subject).text = "Дисциплина"
                        item.findViewById<TextView>(R.id.date_subject).text = "Дата"
                        item.findViewById<TextView>(R.id.score_count).text = "Балл"
                        item.findViewById<TextView>(R.id.score_5).text = "Оценка"
                        item.findViewById<TextView>(R.id.ects).text = "ECTS"
                        scoreCardBody.addView(item)

                        for (j in trs){
                            val item: View = mInflater.inflate(R.layout.score_item, null, false)
                            item.findViewById<TextView>(R.id.subject).text = j.select("td")[1].ownText().toString()
                            item.findViewById<TextView>(R.id.date_subject).text = j.select("td")[2].ownText().toString()
                            item.findViewById<TextView>(R.id.score_count).text = j.select("td")[3].select("span").first().ownText().toString()
                            item.findViewById<TextView>(R.id.score_5).text = j.select("td")[4].select("span").first().ownText().toString()
                            item.findViewById<TextView>(R.id.ects).text = j.select("td")[5].select("span").first().ownText().toString()
                            scoreCardBody.addView(item)
                        }
                        scoreView?.addView(scoreCard)


                    }


                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }


}
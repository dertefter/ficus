package com.dertefter.nstumobile

import AppPreferences
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
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
import java.util.*


class timeTable : Fragment(R.layout.timetable_fragment) {
    var toolbar: Toolbar? = null
    var lineartimeTable: LinearLayout? = null
    var timeTableScrollView: NestedScrollView? = null
    var calendar: Calendar = Calendar.getInstance()
    var isSessia: Boolean = false
    var spinnerBar: ProgressBar? = null
    var gr: String? = null
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isSessia){
            outState.putBoolean("sessia", true)
        }
    }

    @ColorInt
    fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    var link2 = "https://www.nstu.ru/studies/schedule/schedule_session/"

    fun sessia()
    {
        toolbar?.title = "Расписание сессии"
        val mInflater = LayoutInflater.from(activity)
        spinnerBar?.visibility = View.VISIBLE
        isSessia = true
        lineartimeTable?.removeAllViews()
        val retrofit = Retrofit.Builder()
            .baseUrl(link2)
            .build()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.timetable(gr)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val pretty = response.body()?.string().toString()
                    val doc: Document = Jsoup.parse(pretty)
                    val s = doc.body().select("div.schedule__session-body")
                    val rows = s.select("> *")
                    for (i in rows)
                    {
                        val aud = i.select("div.schedule__session-class").text().toString()
                        var time = i.select("div.schedule__session-time").text().toString()
                        if (time == "")
                        {
                            time = "08:30"
                        }
                        val exam = i.select("div.schedule__session-item").text().toString()
                        val date = i.select("div.schedule__session-cell")[0].text().toString()
                        val type = i.select("div.schedule__session-label").text().toString()
                        var item: View = mInflater.inflate(R.layout.item2, null, false)
                        item.findViewById<TextView>(R.id.time).text = time
                        item.findViewById<TextView>(R.id.exam).text = exam
                        item.findViewById<TextView>(R.id.date).text = date
                        item.findViewById<TextView>(R.id.aud).text = aud + ", " + type
                        lineartimeTable?.addView(item)
                        spinnerBar?.visibility = View.INVISIBLE
                    }



                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }
    fun lessons()
    {
        toolbar?.title = "Расписание занятий"
        val mInflater = LayoutInflater.from(activity)
        spinnerBar?.visibility = View.VISIBLE
        isSessia = false
        lineartimeTable?.removeAllViews()
        val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Cookie", "NstuSsoToken="+AppPreferences.token)
                    .build()
                chain.proceed(authorized)
            })
            .build()

        val url1 = "https://ciu.nstu.ru/student_study/timetable/timetable_lessons/"
        val retrofit = Retrofit.Builder()
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
                    val s = doc.body().select("div.schedule__table-body").first()
                    val rows = s.select("> *")
                    for (i in rows)
                    {
                        val day = TextView(Auth.applicationContext())

                        day.setTextColor(context?.getColorFromAttr(com.google.android.material.R.attr.colorOnBackground)!!)
                        day.textSize = 40F
                        day.text = " " + i.getElementsByAttributeValue("data-type", "day").text().toString()
                        val lessons = i.select("div.schedule__table-cell")[1].select("> *")
                        lineartimeTable?.addView(day)
                        for (j in lessons)
                        {
                            var item: View = mInflater.inflate(R.layout.item, null, false)
                            item.findViewById<TextView>(R.id.time).text = j.getElementsByAttributeValue("data-type", "time").text().toString()
                            var itemsInRow = j.select("div.schedule__table-cell")[1].select("> *")
                            if (itemsInRow.size == 1)
                            {
                                if (itemsInRow.select("span[data-week]").size > 0)
                                {
                                    if(itemsInRow.select("span[data-week]").attr("data-week") == "current"){
                                        val lesson_text = itemsInRow.select("div.schedule__table-item").first().ownText().toString().replace("·", " ").replace(",", "")
                                        if(lesson_text != "")
                                        {
                                            val aud = itemsInRow.select("div.schedule__table-class").text().toString()
                                            item.findViewById<TextView>(R.id.aud).text = aud
                                            item.findViewById<TextView>(R.id.lesson).text = lesson_text
                                            lineartimeTable?.addView(item)
                                        }
                                    }
                                }
                                else {
                                    val typeWork = itemsInRow.select("span.schedule__table-typework").text().toString()
                                    val lesson_text =
                                        itemsInRow.select("div.schedule__table-item").first().ownText()
                                            .toString().replace("·", " ").replace(",", "").replace("  ", "")
                                    if (lesson_text != "") {
                                        val aud = itemsInRow.select("div.schedule__table-class").text().toString()
                                        item.findViewById<TextView>(R.id.aud).text = aud  + ", " + typeWork
                                        item.findViewById<TextView>(R.id.lesson).text =
                                            lesson_text
                                        lineartimeTable?.addView(item)
                                    }
                                }
                                spinnerBar?.visibility = View.INVISIBLE
                            }
                            if (itemsInRow.size == 2)
                            {
                                var spans = itemsInRow.select("span[data-week]")
                                for (t in spans)
                                {
                                    if (t.attr("data-week") == "current")
                                    {

                                        val it_ = t.parent().parent().parent()
                                        val lesson_text = it_.select("div.schedule__table-item").first().ownText().toString().replace("·", " ").replace(",", "")
                                        val aud_text = it_.select("div.schedule__table-class").text().toString()
                                        item.findViewById<TextView>(R.id.lesson).text = lesson_text
                                        item.findViewById<TextView>(R.id.aud).text = aud_text
                                        lineartimeTable?.addView(item)

                                    }
                                }

                            }


                        }

                    }




                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerBar = view.findViewById(R.id.spinner)
        var today: Int = calendar.get(Calendar.DAY_OF_WEEK)
        timeTableScrollView = view.findViewById(R.id.timetable_scrollview)
        lineartimeTable = view.findViewById(R.id.linerarTimetable)

        toolbar = view.findViewById(R.id.toolbar_shedule)

        if (AppPreferences.group != null)
        {
            gr = AppPreferences.group!!
        }
        else
        {
            Log.e("ошибка", "нет группы")
        }



        var link1 =
            "https://ciu.nstu.ru/student_study/timetable/timetable_lessons/"
        var link2 = "https://www.nstu.ru/studies/schedule/schedule_session/"



        if (savedInstanceState?.getBoolean("sessia") == false || savedInstanceState?.getBoolean("sessia") == null)
        {
            lessons()

        }
        else{
            sessia()
        }




        toolbar?.setOnMenuItemClickListener {
            if (it.itemId == R.id.l1) {
                lessons()
            }
            if (it.itemId == R.id.l2) {
                sessia()


            }

            true
        }


    }

}


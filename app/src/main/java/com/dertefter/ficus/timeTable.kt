package com.dertefter.ficus

import AppPreferences
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.get
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
    var daySelection: CardView? = null
    var timeTableScrollView: NestedScrollView? = null
    var calendar: Calendar = Calendar.getInstance()
    var isSessia: Boolean = false
    var spinnerBar: ProgressBar? = null
    var gr: String? = null
    var day: Int = 1
    var arrowLeftButton: ImageButton? = null
    var arrowRightButton: ImageButton? = null
    var days: FrameLayout? = null
    var dayView: TextView? = null
    var exams: FrameLayout? = null
    var spinner: ProgressBar? = null
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isSessia) {
            outState.putBoolean("sessia", true)
        }
        outState.putInt("day", day)
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

    fun sessia() {
        spinner?.visibility = View.VISIBLE
        daySelection?.visibility = View.INVISIBLE
        days?.visibility = View.INVISIBLE
        exams?.visibility = View.VISIBLE
        toolbar?.title = "Расписание сессии"
        val mInflater = LayoutInflater.from(activity)
        isSessia = true
        (exams?.get(0) as LinearLayout).removeAllViews()
        val retrofit = Retrofit.Builder()
            .baseUrl(link2)
            .build()
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.timetable(gr)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    spinner?.visibility = View.INVISIBLE
                    val pretty = response.body()?.string().toString()
                    val doc: Document = Jsoup.parse(pretty)
                    val s = doc.body().select("div.schedule__session-body")
                    val rows = s.select("> *")
                    for (i in rows) {
                        val aud = i.select("div.schedule__session-class").text().toString()
                        var time = i.select("div.schedule__session-time").text().toString()
                        if (time == "") {
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
                        (exams?.get(0) as LinearLayout).addView(item)
                    }


                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    fun lessons() {
        spinner?.visibility = View.VISIBLE
        daySelection?.visibility = View.VISIBLE
        toolbar?.title = "Расписание занятий"
        val mInflater = LayoutInflater.from(activity)
        isSessia = false
        for (i in 0..5) {
            ((days?.get(i) as FrameLayout).get(0) as LinearLayout).removeAllViews()
        }
        days?.visibility = View.VISIBLE
        exams?.visibility = View.INVISIBLE
        val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Cookie", "NstuSsoToken=" + AppPreferences.token)
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
                    spinner?.visibility = View.INVISIBLE
                    val pretty = response.body()?.string().toString()
                    val doc: Document = Jsoup.parse(pretty)
                    val s = doc.body().select("div.schedule__table-body").first()
                    val rows = s.select("> *")
                    for (i in 0..rows.size - 1) {
                        val lessons = rows[i].select("div.schedule__table-cell")[1].select("> *")
                        for (j in lessons) {
                            var item: View = mInflater.inflate(R.layout.item, null, false)
                            item.findViewById<TextView>(R.id.time).text =
                                j.getElementsByAttributeValue("data-type", "time").text().toString()
                            var itemsInRow = j.select("div.schedule__table-cell")[1].select("> *")
                            if (itemsInRow.size == 1) {
                                if (itemsInRow.select("span[data-week]").size > 0) {
                                    if (itemsInRow.select("span[data-week]")
                                            .attr("data-week") == "current"
                                    ) {
                                        val lesson_text =
                                            itemsInRow.select("div.schedule__table-item").first()
                                                .ownText().toString().replace("·", " ")
                                                .replace(",", "")
                                        if (lesson_text != "") {
                                            val aud = itemsInRow.select("div.schedule__table-class")
                                                .text().toString()
                                            item.findViewById<TextView>(R.id.aud).text = aud
                                            item.findViewById<TextView>(R.id.lesson).text =
                                                lesson_text
                                            var this_day: FrameLayout = days?.get(i) as FrameLayout
                                            (this_day.get(0) as LinearLayout).addView(item)
                                        }
                                    }
                                } else {
                                    val typeWork =
                                        itemsInRow.select("span.schedule__table-typework").text()
                                            .toString()
                                    val lesson_text =
                                        itemsInRow.select("div.schedule__table-item").first()
                                            .ownText()
                                            .toString().replace("·", " ").replace(",", "")
                                            .replace("  ", "")
                                    if (lesson_text != "") {
                                        val aud =
                                            itemsInRow.select("div.schedule__table-class").text()
                                                .toString()
                                        item.findViewById<TextView>(R.id.aud).text =
                                            aud + ", " + typeWork
                                        item.findViewById<TextView>(R.id.lesson).text =
                                            lesson_text
                                        var this_day: FrameLayout = days?.get(i) as FrameLayout
                                        (this_day.get(0) as LinearLayout).addView(item)
                                    }
                                }
                                spinnerBar?.visibility = View.INVISIBLE
                            }
                            if (itemsInRow.size == 2) {
                                var spans = itemsInRow.select("span[data-week]")
                                for (t in spans) {
                                    if (t.attr("data-week") == "current") {

                                        val it_ = t.parent().parent().parent()
                                        val lesson_text =
                                            it_.select("div.schedule__table-item").first().ownText()
                                                .toString().replace("·", " ").replace(",", "")
                                        val aud_text =
                                            it_.select("div.schedule__table-class").text()
                                                .toString()
                                        item.findViewById<TextView>(R.id.lesson).text = lesson_text
                                        item.findViewById<TextView>(R.id.aud).text = aud_text
                                        var this_day: FrameLayout = days?.get(i) as FrameLayout
                                        (this_day.get(0) as LinearLayout).addView(item)

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

    fun arrowRight() {
        if (day < 6) {
            day++
            for (i in 0..5) {
                if (i == day - 1) {
                    days?.get(i)?.visibility = View.VISIBLE
                } else {
                    days?.get(i)?.visibility = View.INVISIBLE
                }
            }
            if (day == 1) {
                dayView?.text = "Понедельник"
            } else if (day == 2) {
                dayView?.text = "Вторник"
            } else if (day == 3) {
                dayView?.text = "Среда"
            } else if (day == 4) {
                dayView?.text = "Четверг"
            } else if (day == 5) {
                dayView?.text = "Пятница"
            } else if (day == 6) {
                dayView?.text = "Суббота"
            }
            if (day == 6) {
                arrowRightButton?.visibility = View.INVISIBLE
            } else {
                arrowRightButton?.visibility = View.VISIBLE
            }
            if (day == 1) {
                arrowLeftButton?.visibility = View.INVISIBLE
            } else {
                arrowLeftButton?.visibility = View.VISIBLE
            }
        }
    }

    fun arrowLeft() {
        if (day > 1) {
            day--
            for (i in 0..5) {
                if (i == day - 1) {
                    days?.get(i)?.visibility = View.VISIBLE
                } else {
                    days?.get(i)?.visibility = View.INVISIBLE
                }
            }
            if (day == 1) {
                dayView?.text = "Понедельник"
            } else if (day == 2) {
                dayView?.text = "Вторник"
            } else if (day == 3) {
                dayView?.text = "Среда"
            } else if (day == 4) {
                dayView?.text = "Четверг"
            } else if (day == 5) {
                dayView?.text = "Пятница"
            } else if (day == 6) {
                dayView?.text = "Суббота"
            }
            if (day == 6) {
                arrowRightButton?.visibility = View.INVISIBLE
            } else {
                arrowRightButton?.visibility = View.VISIBLE
            }
            if (day == 1) {
                arrowLeftButton?.visibility = View.INVISIBLE
            } else {
                arrowLeftButton?.visibility = View.VISIBLE
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner = view.findViewById(R.id.spinner_timetable)
        exams = view.findViewById(R.id.exams)
        daySelection = view.findViewById(R.id.daySelection)
        arrowLeftButton = view.findViewById(R.id.arrowLeft)
        arrowRightButton = view.findViewById(R.id.arrowRight)
        dayView = view.findViewById(R.id.day_state)
        arrowRightButton?.setOnClickListener {
            arrowRight()
        }
        arrowLeftButton?.setOnClickListener {
            arrowLeft()
        }
        days = view.findViewById(R.id.days)
        var today: Int = calendar.get(Calendar.DAY_OF_WEEK)
        today -= 1
        if (today < 1) {
            today = 1
        }
        day = today
        if (savedInstanceState?.get("day") != null) {
            day = savedInstanceState?.get("day") as Int
        }
        for (i in 0..5) {
            if (i == day - 1) {
                days?.get(i)?.visibility = View.VISIBLE
            } else {
                days?.get(i)?.visibility = View.INVISIBLE
            }
        }
        if (day == 6) {
            arrowRightButton?.visibility = View.INVISIBLE
        } else {
            arrowRightButton?.visibility = View.VISIBLE
        }
        if (day == 1) {
            arrowLeftButton?.visibility = View.INVISIBLE
        } else {
            arrowLeftButton?.visibility = View.VISIBLE
        }
        if (day == 1) {
            dayView?.text = "Понедельник"
        } else if (day == 2) {
            dayView?.text = "Вторник"
        } else if (day == 3) {
            dayView?.text = "Среда"
        } else if (day == 4) {
            dayView?.text = "Четверг"
        } else if (day == 5) {
            dayView?.text = "Пятница"
        } else if (day == 6) {
            dayView?.text = "Суббота"
        }

        timeTableScrollView = view.findViewById(R.id.timetable_scrollview)
        //lineartimeTable = view.findViewById(R.id.linerarTimetable)

        toolbar = view.findViewById(R.id.toolbar_shedule)

        if (AppPreferences.group != null) {
            gr = AppPreferences.group!!
        } else {
            Log.e("ошибка", "нет группы")
        }


        var link1 =
            "https://ciu.nstu.ru/student_study/timetable/timetable_lessons/"
        var link2 = "https://www.nstu.ru/studies/schedule/schedule_session/"



        if (savedInstanceState?.getBoolean("sessia") == false || savedInstanceState?.getBoolean("sessia") == null) {
            lessons()

        } else {
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


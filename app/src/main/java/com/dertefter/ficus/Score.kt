package com.dertefter.ficus

import AppPreferences
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.google.android.material.color.MaterialColors
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
    var scoreView: FrameLayout? = null
    var semSelection: CardView? = null
    var count: Int = 0
    var max_count: Int = 0
    var selectedSemestr: Int = 1
    var arrowLeft: ImageButton? = null
    var arrowRight: ImageButton? = null
    var semTextView: TextView? = null
    var scrollView: NestedScrollView? = null

    @ColorInt
    fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    fun score() {
        val mInflater = LayoutInflater.from(activity)
        val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Cookie", "NstuSsoToken=" + AppPreferences.token)
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
                    count = 0
                    var size = tables.size
                    for (i in 1..size - 1) {
                        count++
                        max_count++
                        var semestr = LinearLayout(Work.applicationContext())
                        semestr.orientation = LinearLayout.VERTICAL
                        semestr.visibility = View.INVISIBLE
                        val trs = tables[i].select("tr.last_progress")

                        for (j in trs) {
                            val item: View = mInflater.inflate(R.layout.score_item, null, false)
                            item.findViewById<TextView>(R.id.subject).text =
                                j.select("td")[1].ownText().toString()
                            item.findViewById<TextView>(R.id.count).text =
                                "Балл: " + j.select("td")[3].select("span").first().ownText()
                                    .toString()
                            item.findViewById<TextView>(R.id.ECTS).text =
                                "ECTS: " + j.select("td")[5].select("span").first().ownText()
                                    .toString()
                            item.findViewById<TextView>(R.id.finaly).text =
                                "Оценка: " + j.select("td")[4].select("span").first().ownText()
                                    .toString().replace("Зач", "Зачёт")
                            var accept = true
                            val checkString = j.select("td")[4].select("span").first().ownText()
                                .toString().replace("Зач", "Зачёт")
                            if (checkString != "Зачёт" && checkString != "5" && checkString != "4" && checkString != "3"){
                                accept = false
                            }
                            if (!accept){
                                item.findViewById<TextView>(R.id.subject).setTextColor(Color.WHITE)
                                item.findViewById<LinearLayout>(R.id.cardPrimary1).setBackgroundColor(Color.RED)
                                item.findViewById<ImageView>(R.id.cardPrimary2).setColorFilter(Color.RED)
                                item.findViewById<ImageView>(R.id.cardPrimary2).setImageResource(R.drawable.ic_baseline_error_outline_24)
                            }else{
                                item.findViewById<ImageView>(R.id.cardPrimary2).setColorFilter(Color.GREEN)
                            }
                            semestr.addView(item)
                        }
                        scoreView?.addView(semestr)
                    }
                    for (i in 0..count - 1) {
                        if (i == selectedSemestr - 1) {
                            (scoreView?.get(i) as LinearLayout).visibility = View.VISIBLE
                        } else {
                            (scoreView?.get(i) as LinearLayout).visibility = View.INVISIBLE
                        }
                    }
                    if (count == 1) {
                        arrowLeft?.visibility = View.INVISIBLE
                    } else {
                        arrowLeft?.visibility = View.VISIBLE
                    }
                    if (count == max_count) {
                        arrowRight?.visibility = View.INVISIBLE
                    } else {
                        arrowRight?.visibility = View.VISIBLE
                    }


                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    fun arrowLeft() {
        if (count > 1) {
            count--
        }
        for (i in 0..max_count - 1) {
            if (i == count - 1) {
                (scoreView?.get(i) as LinearLayout).visibility = View.VISIBLE
            } else {
                (scoreView?.get(i) as LinearLayout).visibility = View.INVISIBLE
            }
        }
        if (count == 1) {
            arrowLeft?.visibility = View.INVISIBLE
        } else {
            arrowLeft?.visibility = View.VISIBLE
        }
        if (count == max_count) {
            arrowRight?.visibility = View.INVISIBLE
        } else {
            arrowRight?.visibility = View.VISIBLE
        }
    }

    fun arrowRight() {
        if (count < max_count) {
            count++
        }
        for (i in 0..max_count - 1) {
            if (i == count - 1) {
                (scoreView?.get(i) as LinearLayout).visibility = View.VISIBLE
            } else {
                (scoreView?.get(i) as LinearLayout).visibility = View.INVISIBLE
            }
        }
        if (count == 1) {
            arrowLeft?.visibility = View.INVISIBLE
        } else {
            arrowLeft?.visibility = View.VISIBLE
        }
        if (count == max_count) {
            arrowRight?.visibility = View.INVISIBLE
        } else {
            arrowRight?.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scoreView = view.findViewById(R.id.score_view)
        semSelection = view.findViewById(R.id.scoreSelection)
        semTextView = view.findViewById(R.id.score_state)
        scrollView = view.findViewById(R.id.nestedScrollView)
        scrollView?.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            if (i2 > i4) {
                ObjectAnimator.ofFloat(semSelection, "translationY", 1000f).start()
            } else {
                ObjectAnimator.ofFloat(semSelection, "translationY", 0f).start()
            }
        }
        arrowLeft = view.findViewById(R.id.arrowLeft)
        arrowRight = view.findViewById(R.id.arrowRight)
        arrowLeft?.setOnClickListener {
            arrowLeft()
        }
        arrowRight?.setOnClickListener {
            arrowRight()
        }
        score()

    }


}
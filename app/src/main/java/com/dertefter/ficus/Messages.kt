package com.dertefter.ficus

import AppPreferences
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
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
import java.lang.StringBuilder

class Messages : Fragment(R.layout.messages_fragment) {
    var messagesView: LinearLayout? = null
    var mInflater: LayoutInflater? = null
    var toolbar: Toolbar? = null
    var spinner: ProgressBar? = null
    var animation: FrameLayout? = null
    var current_value = 0
    var no_mesTextView: TextView? = null
    override fun onStart() {
        super.onStart()
        mes(current_value)

    }

    fun mes(value: Int) {
        current_value = value
        messagesView?.removeAllViews()
        animation?.visibility = View.INVISIBLE
        spinner?.visibility = View.VISIBLE
        val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Cookie", "NstuSsoToken=" + AppPreferences.token)
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
                    val tbody = s.select("tbody")[value]
                    val messages = tbody?.select("tr")
                    spinner?.visibility = View.INVISIBLE
                    if (messages != null) {
                        if (messages.size != 0) {
                            animation?.visibility = View.INVISIBLE
                        } else {
                            animation?.visibility = View.VISIBLE
                        }
                        for (i in messages) {
                            var message_item: View =
                                mInflater!!.inflate(R.layout.message, null, false)
                            message_item.findViewById<TextView>(R.id.send_by).text =
                                i.select("span")[0].text().toString().replace("(преподаватель)", "")
                            message_item.findViewById<TextView>(R.id.message_text).text =
                                i.select("div").text().toString()
                            message_item.findViewById<ImageView>(R.id.send_by_image)
                                .setImageResource(R.drawable.ic_avatar)
                            message_item.isClickable = true
                            message_item.setOnClickListener {
                                val inta = Intent(
                                    Auth.applicationContext(),
                                    ReadMessageActivity::class.java
                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                inta.putExtra(
                                    "send_by",
                                    i.select("span")[0].text().toString()
                                        .replace("(преподаватель)", "").replace("(деканат)", "")
                                )
                                inta.putExtra(
                                    "theme",
                                    i.select("span")[1].text().toString().replace("--", "\n")
                                )
                                var text = i.select("span")[2].text().toString().replace("--", "\n")
                                text = text.replaceRange(0, 2, "")
                                inta.putExtra(
                                    "text",
                                    text
                                )
                                inta.putExtra(
                                    "mesid",
                                    i.select("td")[0].select("input")[0].attr("id").toString()
                                        .replace("id_chk_", "")
                                )
                                Work.applicationContext().startActivity(inta)
                            }

                            messagesView?.addView(message_item)

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
        messagesView = view.findViewById(R.id.messages_view)
        animation = view.findViewById(R.id.animationMessages)
        mInflater = LayoutInflater.from(activity)
        spinner = view.findViewById(R.id.spinner_mes)
        toolbar = view.findViewById(R.id.toolbar_messages)
        no_mesTextView = view.findViewById(R.id.no_mes_text)
        toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mes1 -> {
                    toolbar?.title = "От преподавателей"
                    mes(0)
                    true
                }
                R.id.mes2 -> {
                    toolbar?.title = "От деканата"
                    mes(1)
                    true
                }
                R.id.mes3 -> {
                    toolbar?.title = "От бухгалтерии"
                    mes(2)
                    true
                }
                R.id.mes4 -> {
                    toolbar?.title = "От международной службы"
                    mes(3)
                    true
                }
                R.id.mes5 -> {
                    toolbar?.title = "Прочее"
                    mes(4)
                    true
                }
                else -> false
            }
        }
        //mes(0)
    }
}
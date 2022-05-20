package com.dertefter.nstumobile

import AppPreferences
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
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


class profileMenu : Fragment(R.layout.profile_menu) {
    var nameText: TextView? = null
    var wifiButton: LinearLayout? = null
    var profileDataButton: LinearLayout? = null
    var vk: ImageButton? = null
    var tg: ImageButton? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileDataButton = view.findViewById(R.id.profile_data_button)
        wifiButton = view.findViewById(R.id.wifi_button)
        vk = view.findViewById(R.id.vk)
        tg = view.findViewById(R.id.tg)
        tg?.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/nstumobile_dev/"))
            startActivity(browserIntent)
        }
        vk?.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.me/join/GLo5/Ww6z/8vDWV4nZnNx/KTu4rx_2g07Dc="))
            startActivity(browserIntent)
        }




        profileDataButton?.setOnClickListener {
            val profiledataIntent = Intent(Auth.applicationContext(), ProfileData::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Auth.applicationContext().startActivity(profiledataIntent)

        }




        wifiButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                wifiNnstu()
            }
        })
        nameText = view.findViewById(R.id.name)
        if (AppPreferences.name == null)
        {
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
                        var s = doc.body().select("div.sysCaption").select("div")[4].select("div")[9].text().toString()
                        val sarr = s.split(" ").toTypedArray()
                        AppPreferences.fullName = sarr[0] + " " + sarr[1] + " " + sarr[2]
                        s = sarr[1]

                        nameText?.text = s
                        AppPreferences.name = s

                    } else {

                        Log.e("RETROFIT_ERROR", response.code().toString())

                    }
                }
            }
        }
        else
        {
            nameText?.text = AppPreferences.name

        }

    }

    fun wifiNnstu() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://wifi.nstu.ru/"))
        startActivity(browserIntent)
    }

}
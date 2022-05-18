package com.dertefter.nstumobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
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

class Profile : Fragment(R.layout.profile_fragment) {
    var settinsButton: ImageButton? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settinsButton = view.findViewById(R.id.settings_button)
        settinsButton?.setOnClickListener {
            var inta = Intent(Work.applicationContext(), Settings::class.java)
            startActivity(inta)
        }

        fragmentManager?.beginTransaction()?.replace(R.id.profile_mwnu, profileMenu())?.commit()


    }
}
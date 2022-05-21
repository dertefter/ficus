package com.dertefter.nstumobile

import AppPreferences
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.vosk.Model
import org.vosk.android.StorageService
import java.io.IOException
import java.util.*

class Auth : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        private var instance: Auth? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private var model: Model? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val locale = Locale("ru")
        Locale.setDefault(locale)
        val config: Configuration = baseContext.resources.configuration
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )

        AppPreferences.setup(Auth.applicationContext())
        if (AppPreferences.name != null)
        {
            val nameView: TextView = findViewById(R.id.name_auth)
            nameView?.text = "Добро пожаловать, " + AppPreferences.name

        }


        var intent_login = Intent(this, Login::class.java)
        var intent_work = Intent(this, Work::class.java)
        var saved_login: String? = AppPreferences.login
        var saved_password: String? = AppPreferences.password
        if (saved_login != "" && saved_password != "" && saved_login != null && saved_password != null)
        {
            Website().Auth(saved_login, saved_password)
        }
        else
        {
            startActivity(intent_login)
        }

    }

    private fun initModel() {
        StorageService.unpack(
            Work.applicationContext(), "model-ru", "model",
            { model: Model? ->
                this.model = model
            }
        ) { exception: IOException ->
            Log.e(
                "Failed to unpack the model", exception.message.toString()
            )
        }
    }
}
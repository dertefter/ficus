package com.dertefter.nstumobile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import java.io.IOException

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

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
}
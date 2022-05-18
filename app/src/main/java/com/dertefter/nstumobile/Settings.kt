package com.dertefter.nstumobile

import AppPreferences
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.myapplication.APIService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import retrofit2.Retrofit

class Settings : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        private var instance: Settings? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    fun logOut()
    {
        AppPreferences.name = null
        AppPreferences.group = null
        AppPreferences.fullName = null
        AppPreferences.login = null
        AppPreferences.password = null
        val inta = Intent(Work.applicationContext(), Login::class.java)
        startActivity(inta)
        finish()
    }

    var logoutButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_screen)
        logoutButton = findViewById(R.id.logout_button)
        logoutButton?.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Выход")
                .setMessage("Вы уверены, что хотите выйти из аккаунта?")
                .setNeutralButton("Отмена") { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("Выйти") { dialog, which ->
                    logOut()
                }
                .show()
        }
    }
}
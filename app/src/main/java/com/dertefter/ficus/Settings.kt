package com.dertefter.ficus

import AppPreferences
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Settings : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        private var instance: Settings? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    fun logOut() {
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
                .setNegativeButton("Отмена") { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("Выйти") { dialog, which ->
                    logOut()
                }
                .show()
        }
    }
}
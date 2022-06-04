package com.dertefter.ficus

import AppPreferences
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import kotlinx.android.synthetic.main.activity_auth.*
import kotlin.random.Random
import kotlin.random.nextInt

class Auth : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        private var instance: Auth? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    private fun View.blink(
        times: Int = Animation.INFINITE,
        duration: Long = 600L,
        offset: Long = 20L,
        minAlpha: Float = 0.0f,
        maxAlpha: Float = 1.0f,
        repeatMode: Int = Animation.REVERSE
    ) {
        startAnimation(AlphaAnimation(minAlpha, maxAlpha).also {
            it.duration = duration
            it.startOffset = offset
            it.repeatMode = repeatMode
            it.repeatCount = times
        })
    }
    var text1: TextView? = null
    private  fun rand(start: Int, end: Int): Int {
        require(start <= end) { "Illegal Argument" }
        return (Math.random() * (end - start + 1)).toInt() + start
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_auth)
        text1 = findViewById(R.id.auth_text1)
        val r = rand(1, 9)
        when (r){
            1 -> text1?.text = "Подключаемся к НГТУ"
            2 -> text1?.text = "Поливаем фикус"
            3 -> text1?.text = "Подключаемся к НГТУ"
            4 -> text1?.text = "Подключаемся к НГТУ"
            5 -> text1?.text = "Учим физику"
            6 -> text1?.text = "Пишем шпаргалки"
            7 -> text1?.text = "Считаем ваши пропуски"
            8 -> text1?.text = "Подключаемся к сети"
            9 -> text1?.text = "Подключаемся к сети"
        }
        auth_text1?.blink()
        AppPreferences.setup(Auth.applicationContext())

        var intent_login = Intent(this, Login::class.java)
        var saved_login: String? = AppPreferences.login
        var saved_password: String? = AppPreferences.password
        if (saved_login != "" && saved_password != "" && saved_login != null && saved_password != null) {
            Website().Auth(saved_login, saved_password)
        } else {
            startActivity(intent_login)
        }

    }
}
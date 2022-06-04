package com.dertefter.ficus


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged

class Login : AppCompatActivity() {
    var loading: ProgressBar? = null
    var loginTextView: TextView? = null
    var passwordTextView: TextView? = null
    var signInButton: Button? = null
    private var loginText: String = ""
    private var passwordText: String = ""
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loading = findViewById(R.id.progressBarLogin)
        loginTextView = findViewById(R.id.login)
        passwordTextView = findViewById(R.id.password)
        signInButton = findViewById(R.id.login_button)
        signInButton?.setOnClickListener { signIn() }
        loginTextView?.doOnTextChanged { text, start, count, after -> textChecker() }
        passwordTextView?.doOnTextChanged { text, start, count, after -> textChecker() }
    }

    private fun textChecker() {
        loginText = loginTextView?.text.toString()
        passwordText = passwordTextView?.text.toString()
        signInButton?.isEnabled = loginText != "" && passwordText != ""
    }

    private fun signIn() {
        loading?.visibility = View.VISIBLE
        signInButton?.isEnabled = false
        loginTextView?.isEnabled = false
        passwordTextView?.isEnabled = false
        Website().Auth(loginText, passwordText)
    }
}
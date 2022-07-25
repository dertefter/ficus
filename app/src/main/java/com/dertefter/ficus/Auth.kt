package com.dertefter.ficus

import AppPreferences
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException


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

    private fun View.blink2(
        times: Int = Animation.INFINITE,
        duration: Long = 700L,
        offset: Long = 40L,
        minAlpha: Float = 1.0f,
        maxAlpha: Float = 0.0f,
        repeatMode: Int = Animation.REVERSE
    ) {
        startAnimation(AlphaAnimation(minAlpha, maxAlpha).also {
            it.duration = duration
            it.startOffset = offset
            it.repeatMode = repeatMode
            it.repeatCount = times
        })
    }

    var tokenId: String = ""
    var gr = ""

    fun getGroup() {
        var htmlString: String = ""
        val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Cookie", "NstuSsoToken=$tokenId")
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
            val response = service.Study()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    val pretty = response.body()?.string().toString()
                    val doc: Document = Jsoup.parse(pretty)

                    var bodyyy = doc.body().select("div").first()
                    var el = bodyyy.select("div").first()
                    el = el.select("div.other_lks")[1]
                    el = el.child(0)
                    var txt: String = el.toString()
                    var group = ""
                    for (i in 32..txt.length) {
                        if (txt[i] == ' ' || txt[i] == '<')
                            break
                        group += txt[i]
                    }

                    AppPreferences.group = group
                    ViewStudy()


                } else {
                    Log.e("RETROFIT_ERROR", response.code().toString())
                }
            }
        }

    }

    fun getToken(): String {
        return tokenId
    }

    fun ViewStudy() {
        val client = OkHttpClient().newBuilder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val authorized: Request = original.newBuilder()
                    .addHeader("Cookie", "NstuSsoToken=$tokenId")
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
            val response = service.Study()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val pretty = response.body()?.string().toString()
                    val context: Context = applicationContext()
                    val inta =
                        Intent(context, Work::class.java)
                    startActivity(inta)
                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }


    fun authFun(login: String, password: String) {
        val url1 = "https://login.nstu.ru/"
        val retrofit = Retrofit.Builder().baseUrl(url1).build()
        val service = retrofit.create(APIService::class.java)

        val jsonObjectString =
            "{\"authId\":\"eyAidHlwIjogIkpXVCIsICJhbGciOiAiSFMyNTYiIH0.eyAib3RrIjogInR2ZW9yazY0dHU5aDc5dTRtb2xoZTBrb3NkIiwgInJlYWxtIjogIm89bG9naW4sb3U9c2VydmljZXMsZGM9b3BlbmFtLGRjPWNpdSxkYz1uc3R1LGRjPXJ1IiwgInNlc3Npb25JZCI6ICJBUUlDNXdNMkxZNFNmY3dIV1l6elZqbTdlbjREYXptS2ZfQktXLTA0UGR1M0lMay4qQUFKVFNRQUNNRElBQWxOTEFCTTJNamc0T0RrM05qUXpNVFE1TXpJMk56TTUqIiB9.iQ7F98fLLFrcDlSI5kYU14d9_Dg9lKN5meoGYIdXxcA\",\"template\":\"\",\"stage\":\"JDBCExt1\",\"header\":\"Авторизация\",\"callbacks\":[{\"type\":\"NameCallback\",\"output\":[{\"name\":\"prompt\",\"value\":\"Логин:\"}],\"input\":[{\"name\":\"IDToken1\",\"value\":\"$login\"}]},{\"type\":\"PasswordCallback\",\"output\":[{\"name\":\"prompt\",\"value\":\"Пароль:\"}],\"input\":[{\"name\":\"IDToken2\",\"value\":\"$password\"}]}]}"
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        CoroutineScope(Dispatchers.Main).launch {

            try {
                val response = service.authPart1(requestBody)
                withContext(Dispatchers.IO) {
                    if (response.isSuccessful) {
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val prettyJson = gson.toJson(
                            JsonParser.parseString(
                                response.body()
                                    ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                            )
                        )

                        val Jobject = JSONObject(prettyJson)
                        tokenId = Jobject.getString("tokenId").toString()
                        AppPreferences.token = tokenId
                        var CookieString = "\"NstuSsoToken\"=\"$tokenId\""
                        val requestBody2 =
                            CookieString.toRequestBody("application/json".toMediaTypeOrNull())
                        CoroutineScope(Dispatchers.IO).launch {

                            // Do the POST request and get response
                            val response2 = service.authPart2(requestBody2)

                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {

                                    // Convert raw JSON to pretty JSON using GSON library
                                    val gson = GsonBuilder().setPrettyPrinting().create()
                                    val prettyJson = gson.toJson(
                                        JsonParser.parseString(
                                            response.body()
                                                ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                                        )
                                    )
                                    retrofit.newBuilder()
                                        .baseUrl("https://login.nstu.ru/ssoservice/json/users/$login/")
                                        .build()
                                    CoroutineScope(Dispatchers.IO).launch {
                                        /*
                                         * For @Query: You need to replace the following line with val response = service.getEmployees(2)
                                         * For @Path: You need to replace the following line with val response = service.getEmployee(53)
                                         */

                                        // Do the GET request and get response
                                        val response = service.authPart3()

                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                val animLottie = ObjectAnimator.ofFloat(findViewById<LottieAnimationView>(
                                                    R.id.lottieAnimationView
                                                ), "alpha", 1f, 0f)
                                                animLottie .duration = 100
                                                animLottie .start()
                                                val anim1 = ObjectAnimator.ofFloat(icon, "alpha", 0f, 1f)
                                                anim1.duration = 460
                                                anim1.start()
                                                val anim2 = ObjectAnimator.ofFloat(icon, "scaleX", 0f, 1f)
                                                anim2.duration = 300
                                                anim2.start()
                                                val anim3 = ObjectAnimator.ofFloat(icon, "scaleY", 0f, 1f)
                                                anim3.duration = 100
                                                anim3.start()

                                                retrofit.newBuilder().baseUrl(url1).build()
                                                val requestBody4 =
                                                    CookieString.toRequestBody("application/json".toMediaTypeOrNull())

                                                CoroutineScope(Dispatchers.IO).launch {
                                                    // Do the POST request and get response
                                                    val response4 = service.authPart4(requestBody4)

                                                    withContext(Dispatchers.Main) {
                                                        val r = rand(1, 9)
                                                        when (r){
                                                            1 -> text1?.text = "Загружаем данные..."
                                                            2 -> text1?.text = "Подготовка..."
                                                            3 -> text1?.text = "Подготовка..."
                                                            4 -> text1?.text = "Загружаем..."
                                                            5 -> text1?.text = "Загружаем..."
                                                            6 -> text1?.text = "Почти готово..."
                                                            7 -> text1?.text = "Почти готово..."
                                                            8 -> text1?.text = "Подготовка..."
                                                            9 -> text1?.text = "Почти готово..."
                                                        }
                                                        if (response.isSuccessful) {
                                                            var r = response.body()?.string()
                                                                ?.toString()
                                                            if (r != null) {
                                                                AppPreferences.login = login
                                                                AppPreferences.password = password
                                                                getGroup()
                                                            }

                                                        } else {
                                                            val context: Context =
                                                                applicationContext()
                                                            var inta = Intent(
                                                                context,
                                                                Login::class.java
                                                            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            context.startActivity(inta)

                                                        }
                                                    }
                                                }


                                            } else {

                                                Log.e("RETROFIT_ERROR", response.code().toString())

                                            }
                                        }
                                    }
                                } else {

                                    Log.e("RETROFIT_ERROR_PART2", response.code().toString())

                                }
                            }
                        }
                    } else {
                        Log.e("a", "неверный логин или пароль")


                    }


                }
            } catch (e: Throwable) {
                Log.e("a", "1")
                val context: Context = Auth.applicationContext()
                var inta = Intent(
                    context,
                    NetworkErrorActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(inta)

            } catch (e: HttpException) {
                Log.e("a", "2")
            } catch (e: IOException) {
                Log.e("a", "3")
            }
        }


    }

    var icon: ImageView? = null
    var text1: TextView? = null
    private  fun rand(start: Int, end: Int): Int {
        require(start <= end) { "Illegal Argument" }
        return (Math.random() * (end - start + 1)).toInt() + start
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        text1 = findViewById(R.id.auth_text1)
        icon = findViewById(R.id.authIcon)
        val r = rand(1, 9)
        when (r){
            1 -> text1?.text = "Подключаемся к НГТУ"
            2 -> text1?.text = "Поливаем фикус"
            3 -> text1?.text = "Подключаемся к НГТУ"
            4 -> text1?.text = "Подключаемся к НГТУ"
            5 -> text1?.text = "Подключаемся к сети"
            6 -> text1?.text = "Подключаемся к НГТУ"
            7 -> text1?.text = "Подключаемся к сети"
            8 -> text1?.text = "Подключаемся к сети"
            9 -> text1?.text = "Подключаемся к сети"
        }
        auth_text1?.blink()
        AppPreferences.setup(Auth.applicationContext())

        var intent_login = Intent(this, Login::class.java)
        var saved_login: String? = AppPreferences.login
        var saved_password: String? = AppPreferences.password
        if (saved_login != "" && saved_password != "" && saved_login != null && saved_password != null) {
            authFun(saved_login, saved_password)
        } else {
            startActivity(intent_login)
        }

    }
}
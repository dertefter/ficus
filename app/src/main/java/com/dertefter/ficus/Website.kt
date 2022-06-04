package com.dertefter.ficus

import AppPreferences
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
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

class AuthException() : Exception("Auth Error")

class Website {

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

                    Log.e("group", group)
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
                    Log.i("Got :", pretty)
                    val context: Context = Auth.applicationContext()
                    val inta =
                        Intent(context, Work::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(inta)
                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }


    fun Auth(login: String, password: String) {
        val url1 = "https://login.nstu.ru/"
        val retrofit = Retrofit.Builder().baseUrl(url1).build()
        val service = retrofit.create(APIService::class.java)

        val jsonObjectString =
            "{\"authId\":\"eyAidHlwIjogIkpXVCIsICJhbGciOiAiSFMyNTYiIH0.eyAib3RrIjogInR2ZW9yazY0dHU5aDc5dTRtb2xoZTBrb3NkIiwgInJlYWxtIjogIm89bG9naW4sb3U9c2VydmljZXMsZGM9b3BlbmFtLGRjPWNpdSxkYz1uc3R1LGRjPXJ1IiwgInNlc3Npb25JZCI6ICJBUUlDNXdNMkxZNFNmY3dIV1l6elZqbTdlbjREYXptS2ZfQktXLTA0UGR1M0lMay4qQUFKVFNRQUNNRElBQWxOTEFCTTJNamc0T0RrM05qUXpNVFE1TXpJMk56TTUqIiB9.iQ7F98fLLFrcDlSI5kYU14d9_Dg9lKN5meoGYIdXxcA\",\"template\":\"\",\"stage\":\"JDBCExt1\",\"header\":\"Авторизация\",\"callbacks\":[{\"type\":\"NameCallback\",\"output\":[{\"name\":\"prompt\",\"value\":\"Логин:\"}],\"input\":[{\"name\":\"IDToken1\",\"value\":\"$login\"}]},{\"type\":\"PasswordCallback\",\"output\":[{\"name\":\"prompt\",\"value\":\"Пароль:\"}],\"input\":[{\"name\":\"IDToken2\",\"value\":\"$password\"}]}]}"
        Log.e("a", jsonObjectString)
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
                        Log.d("token^ ", tokenId)
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

                                    Log.e("Pretty Printed JSON :", "part 2 success")
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
                                                Log.e("AuthPart3 :", "Success")
                                                retrofit.newBuilder().baseUrl(url1).build()
                                                val requestBody4 =
                                                    CookieString.toRequestBody("application/json".toMediaTypeOrNull())

                                                CoroutineScope(Dispatchers.IO).launch {
                                                    // Do the POST request and get response
                                                    val response4 = service.authPart4(requestBody4)

                                                    withContext(Dispatchers.Main) {

                                                        if (response.isSuccessful) {
                                                            var r = response.body()?.string()
                                                                ?.toString()
                                                            if (r != null) {
                                                                AppPreferences.login = login
                                                                AppPreferences.password = password
                                                                getGroup()
                                                            }
                                                            Log.e("PART4 :", "Okey")

                                                        } else {
                                                            val context: Context =
                                                                Auth.applicationContext()
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
                        throw AuthException()

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
}
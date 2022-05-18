package com.example.myapplication

import okhttp3.Cookie
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface APIService {

    @POST("ssoservice/json/authenticate")
    suspend fun authPart1(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("json/users?_action=idFromSession")
    suspend fun authPart2(@Body requestBody: RequestBody): Response<ResponseBody>

    @GET(".")
    suspend fun authPart3(): Response<ResponseBody>

    @POST("json/users?_action=validateGoto")
    suspend fun authPart4(@Body requestBody: RequestBody): Response<ResponseBody>

    @GET(".")
    suspend fun Study(): Response<ResponseBody>

    @FormUrlEncoded
    @POST(".")
    suspend fun postForm(@FieldMap params: HashMap<String?, String?>): Response<ResponseBody>


    @GET("schedule")
    suspend fun timetable(@Query("group") group: String?): Response<ResponseBody>

}
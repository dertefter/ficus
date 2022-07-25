package com.dertefter.ficus

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface APIService {

    @GET(".")
    suspend fun getNews(@Query("main_events") page: String?): Response<ResponseBody>

    @FormUrlEncoded
    @POST("cgi-bin/koha/opac-user.pl")
    suspend fun authBooks(@FieldMap params: HashMap<String?, String?>): Response<ResponseBody>

    @GET("cgi-bin/koha/opac-user.pl")
    suspend fun getBooks(): Response<ResponseBody>

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

    @GET("mess_teacher")
    suspend fun messages(@Query("year") year: String?): Response<ResponseBody>

    @GET("schedule")
    suspend fun timetable(@Query("group") group: String?): Response<ResponseBody>

}
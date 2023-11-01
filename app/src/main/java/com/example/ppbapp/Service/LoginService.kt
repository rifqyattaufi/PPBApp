package com.example.ppbapp.Service

import com.example.ppbapp.Data.LoginData
import com.example.ppbapp.Respond.LoginRespond
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("auth/local")
    fun getData(@Body body: LoginData): Call<LoginRespond>
}
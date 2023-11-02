package com.example.ppbapp.Service

import com.example.ppbapp.Data.RegisterData
import com.example.ppbapp.Respond.LoginRespond
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterService {
    @POST("auth/local/register")
    fun saveData(@Body body: RegisterData): Call<LoginRespond>
}
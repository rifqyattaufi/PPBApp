package com.example.ppbapp.Service

import com.example.ppbapp.Data.UpdateData
import com.example.ppbapp.Respond.LoginRespond
import com.example.ppbapp.Respond.UserRespond
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @GET("users")
    fun getData(): Call<List<UserRespond>>

    @DELETE("users/{id}")
    fun delete(@Path("id") id: Int): Call<UserRespond>

    @PUT("users/{id}")
    fun save(@Path("id") id: String?, @Body body: UpdateData): Call<LoginRespond>
}
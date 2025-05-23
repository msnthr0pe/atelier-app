package com.example.atelierapp.ktor

import com.example.atelierapp.EmailDTO
import com.example.atelierapp.PasswordDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    fun login(@Body request: AuthModels.LoginRequest): Call<AuthModels.AuthResponse>

    @POST("register")
    fun register(@Body request: AuthModels.RegisterRequest): Call<AuthModels.AuthResponse>

    @POST("addclient")
    fun addClient(@Body request: AuthModels.ClientRequest): Call<AuthModels.AuthResponse>

    @GET("getclients")
    suspend fun getClients(): List<AuthModels.ClientRequest>

    @POST("getclientby")
    suspend fun getClientBy(@Body request: AuthModels.ClientByDTO): List<AuthModels.ClientRequest>

    @POST("getuser")
    suspend fun getUser(@Body email: EmailDTO): AuthModels.RegisterRequest

    @POST("updateuser")
    suspend fun updateUserPassword(@Body request: PasswordDTO): AuthModels.AuthResponse
}
package com.example.evcondata.data.network

import com.example.evcondata.data.network.request.LoginRequest
import retrofit2.Response
import retrofit2.http.*

interface UserServices {
    @POST("ev-data/_session")
    suspend fun login(
        @Body loginRequest: LoginRequest)
    : Response<Any>

    @POST("ev-data/_session")
    suspend fun login(
        @Header("Authorization") authHeader: String?
    ): Response<Any>
}
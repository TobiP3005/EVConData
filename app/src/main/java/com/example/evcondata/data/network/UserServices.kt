package com.example.evcondata.data.network

import retrofit2.Response
import retrofit2.http.*

interface UserServices {
    @POST("ev-data/_session")
    suspend fun login(
        @Header("Authorization") authHeader: String?
    ): Response<LoginBody>
}

data class LoginBody(
    val userCtx: Name
    )

data class Name(
    val name: String
)
package com.example.evcondata.data.auth

import com.example.evcondata.data.auth.model.LoggedInUser
import com.example.evcondata.data.network.UserServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import java.io.IOException
import javax.inject.Inject


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource @Inject constructor(private val userServices: UserServices) {

    suspend fun login(username: String, password: String): AuthResult<LoggedInUser> {
        return withContext(Dispatchers.IO){
            try {
                val credentials = Credentials.basic(username, password)
                val response = userServices.login(credentials)
                if (response.isSuccessful){
                    val userId = response.body()!!.userCtx.name
                    val header = response.headers().get("Set-Cookie")
                    val arr: List<String> = header!!.split("=")
                    var jsessionid = arr[1]
                    jsessionid = jsessionid.substring(0, jsessionid.length - 6)
                    return@withContext AuthResult.Success(LoggedInUser(username, userId, jsessionid))
                } else{
                    return@withContext AuthResult.Error(IOException("Login Failed"))
                }
            } catch (e: Throwable) {
                return@withContext AuthResult.Error(IOException("Error logging in", e))
            }
        }
    }

    suspend fun login(firstName: String, lastName: String, googleToken: String): AuthResult<LoggedInUser> {

        return withContext(Dispatchers.IO){
            try {
                val response = userServices.login("Bearer $googleToken")
                if (response.isSuccessful){
                    val body = response.body()
                    val userId = body!!.userCtx.name
                    val header = response.headers().get("Set-Cookie")
                    val arr: List<String> = header!!.split("=")
                    var sessionid = arr[1]
                    sessionid = sessionid.substring(0, sessionid.length - 6)

                    return@withContext AuthResult.Success(LoggedInUser(firstName, userId, sessionid))
                } else{
                    return@withContext AuthResult.Error(IOException("Login Failed"))
                }
            } catch (e: Throwable) {
                return@withContext AuthResult.Error(IOException("Error logging in", e))
            }
        }
    }
}
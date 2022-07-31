package com.example.evcondata.data.auth

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class AuthResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : AuthResult<T>()
    data class Error(val exception: Exception) : AuthResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}
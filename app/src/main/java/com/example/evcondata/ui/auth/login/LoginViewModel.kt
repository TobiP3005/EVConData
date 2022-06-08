package com.example.evcondata.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.evcondata.R
import com.example.evcondata.data.auth.AuthResult
import com.example.evcondata.data.auth.LoginRepository
import com.example.evcondata.data.auth.UserPreferencesRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository, private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = loginRepository.login(username, password)

            if (result is AuthResult.Success) {
                _loginResult.postValue(LoginResult(success = LoggedInUserView(displayName = result.data.username)))
                userPreferencesRepository.setSessionToken(result.data.sessionToken)
                userPreferencesRepository.setUsername(result.data.username)
                userPreferencesRepository.setUserId(result.data.username)
            } else if (result is AuthResult.Error){
                _loginResult.postValue(LoginResult(error = R.string.login_failed))
            }
        }
    }

    fun login(firstName: String?, lastName: String?, googleToken: String?) {
        CoroutineScope(Dispatchers.IO).launch {

            if (googleToken == null || firstName == null || lastName == null) {
                _loginResult.postValue(LoginResult(error = R.string.login_failed))
            }
            else {
                val result = loginRepository.login(firstName, lastName, googleToken)

                if (result is AuthResult.Success) {
                    _loginResult.postValue(LoginResult(success = LoggedInUserView(displayName = result.data.username)))
                    userPreferencesRepository.setSessionToken(result.data.sessionToken)
                    userPreferencesRepository.setUsername(result.data.username)
                    userPreferencesRepository.setUserId(result.data.userId)
                } else if (result is AuthResult.Error) {
                    _loginResult.postValue(LoginResult(error = R.string.login_failed))
                }
            }
        }
    }

    fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            userPreferencesRepository.logoutUser()
            loginRepository.logout()
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
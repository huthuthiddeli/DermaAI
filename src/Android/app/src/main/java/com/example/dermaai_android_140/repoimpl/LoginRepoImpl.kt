package com.example.dermaai_android_140.repoimpl

import androidx.lifecycle.MutableLiveData
import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.HealthCheckResponse
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.LoginRepo
import com.google.gson.Gson

class LoginRepoImpl : LoginRepo {

    val _error = MutableLiveData<String?>()

    override fun login(email: String, password: String, mfa: Boolean, url: String): User? {
        val user = User(email, password, mfa)
        val result = API.callApi(url, "POST", user)

        return if (result.isSuccess) {
            Gson().fromJson(result.getOrNull(), User::class.java)
        } else {
            _error.postValue("Login failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}")
            null
        }
    }

    override fun register(email: String, password: String, mfa: Boolean, url: String): User? {
        val user = User(email, password, mfa)
        val result = API.callApi(url, "POST", user)

        return if (result.isSuccess) {
            Gson().fromJson(result.getOrNull(), User::class.java)
        } else {
            _error.postValue("Registration failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}")
            null
        }
    }

    override fun setMFA(user: User?, url: String): User? {
        val result = API.callApi(url, "POST", user)

        return if (result.isSuccess) {
            Gson().fromJson(result.getOrNull(), User::class.java)
        } else {
            _error.postValue("MFA setup failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}")
            null
        }
    }

    override fun checkHealth(model: HealthCheckResponse, url: String): String {
        val result = API.callApi(url, "GET", model)

        return if (result.isSuccess) {
            val healthResponse = Gson().fromJson(result.getOrNull(), HealthCheckResponse::class.java)
            if (healthResponse.message == "Application is live") {
                "Ready to connect!"
            } else {
                _error.postValue("Unexpected health check response")
                "Error"
            }
        } else {
            _error.postValue("Connection failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}")
            "Error"
        }
    }
}

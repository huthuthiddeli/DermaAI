package com.example.dermaai_android_140.repoimpl

import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.HealthCheckResponse
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.LoginRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class LoginRepoImpl : LoginRepo {

    override fun login(email: String, password: String, mfa: Boolean, url: String): Result<User> {
        return try {
            val user = User(email, password, mfa)
            val result = API.callApi(url, "POST", user)

            if (result.isSuccess) {
                val userResponse = Gson().fromJson(result.getOrThrow(), User::class.java)
                Result.success(userResponse)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun register(email: String, password: String, mfa: Boolean, url: String): Result<User> {
        return try {
            val user = User(email, password, mfa)
            val result = API.callApi(url, "POST", user)

            if (result.isSuccess) {
                val userResponse = Gson().fromJson(result.getOrThrow(), User::class.java)
                Result.success(userResponse)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun setMFA(user: User?, url: String): Result<User> {
        return try {
            val result = API.callApi(url, "POST", user)

            if (result.isSuccess) {
                val userResponse = Gson().fromJson(result.getOrThrow(), User::class.java)
                Result.success(userResponse)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun checkHealth(model: HealthCheckResponse, url: String): Result<String> {
        return try {
            val result = API.callApi(url, "GET", model)

            if (result.isSuccess) {
                val healthResponse = Gson().fromJson(result.getOrThrow(), HealthCheckResponse::class.java)
                Result.success(healthResponse.message)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}

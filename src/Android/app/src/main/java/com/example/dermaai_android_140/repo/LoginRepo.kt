package com.example.dermaai_android_140.repo

import com.example.dermaai_android_140.myClasses.HealthCheckResponse
import com.example.dermaai_android_140.myClasses.User


interface LoginRepo {

                fun login(email: String, password: String, mfa: Boolean, url : String):  Result<User>

                fun register(email: String, password: String, mfa: Boolean, url : String): Result<User>

                fun setMFA(user : User?, url : String) : User?

                fun checkHealth(model : HealthCheckResponse, url : String) : String


}
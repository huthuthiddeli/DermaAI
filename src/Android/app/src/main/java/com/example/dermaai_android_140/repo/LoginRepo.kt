package com.example.dermaai_android_140.repo

import com.example.dermaai_android_140.myClasses.User


interface LoginRepo {

    fun login(email: String, password: String, mfa : Boolean, key : String) : User?
    fun register(): User?
    fun getUser(): User?

}
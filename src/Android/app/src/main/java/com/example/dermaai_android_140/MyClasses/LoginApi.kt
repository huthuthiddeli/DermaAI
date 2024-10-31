package com.example.dermaai_android_140.MyClasses

class LoginApi : Api() {

    fun login(email : String, password : String) : User?
    {
        val user = User(email,password)

        return User("T","T")
    }

    fun register()
    {

    }



}
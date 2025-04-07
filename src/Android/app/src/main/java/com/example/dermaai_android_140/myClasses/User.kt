package com.example.dermaai_android_140.myClasses

data class User(
    var email: String,
    var password: String,
    var mfa: Boolean,
    var isAdmin : Boolean = false,
    var id: String? = "",
)


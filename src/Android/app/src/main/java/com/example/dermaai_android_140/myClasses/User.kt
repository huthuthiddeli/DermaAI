package com.example.dermaai_android_140.myClasses

class User(var email : String, var password : String, var mfa : Boolean)
{
    var id : String? = ""

    constructor(email: String, password: String, id: String?, mfa : Boolean)
            : this(email, password, mfa) {
        this.id = id
    }

}
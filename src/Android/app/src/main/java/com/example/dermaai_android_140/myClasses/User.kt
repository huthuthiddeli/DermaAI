package com.example.dermaai_android_140.myClasses

class User(var email : String, var password : String, var mfa : Boolean,var key : String)
{
    var id : String? = ""

    constructor(email: String, password: String, id: String?, mfa : Boolean,key : String)
            : this(email, password, mfa, key) {
        this.id = id
    }

}


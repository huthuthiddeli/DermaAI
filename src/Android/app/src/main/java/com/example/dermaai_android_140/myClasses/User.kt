package com.example.dermaai_android_140.myClasses

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy

class User(var email : String, var password : String)
{
    var id : String? = ""

    constructor(email: String, password: String, id: String?)
            : this(email, password) {
        this.id = id
    }

}


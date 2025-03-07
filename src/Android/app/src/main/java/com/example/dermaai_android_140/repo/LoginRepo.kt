package com.example.dermaai_android_140.repo

import com.example.dermaai_android_140.myClasses.User



interface LoginRepo {

                fun login(email: String, password: String, mfa: Boolean, url : String): User? 

                fun register(email: String, password: String, mfa: Boolean, url : String): User?

                fun setMFA(user : User?, url : String) : User?


}
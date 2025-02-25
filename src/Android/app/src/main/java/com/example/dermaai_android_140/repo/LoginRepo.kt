package com.example.dermaai_android_140.repo

import android.R
import com.example.dermaai_android_140.myClasses.User



interface LoginRepo {

                fun login(email: String, password: String, mfa: Boolean, url : String): User? {
                        return User(email, password, mfa)
                }

                fun register(email: String, password: String, mfa: Boolean, url : String): User? {
                        return User("a", "a", false)
                }

                fun getUser(): User? {
                        return User("a", "a", false)
                }

                fun setMFA(user : User?, url : String) : User?

}
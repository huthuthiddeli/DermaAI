package com.example.dermaai_android_140.repoimpl

import android.content.Context
import android.content.SharedPreferences
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.UserRepo

class UserRepoImpl(context : Context) : UserRepo {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    override suspend fun getCurrentUser(): User? {
        val password = sharedPreferences.getString("password", null).toString()
        val email = sharedPreferences.getString("email", null).toString()
        val mfa = sharedPreferences.getString("mfa", null)
        val isAdmin = sharedPreferences.getBoolean("isAdmin", false)
        
        return User(email,password,mfa.toBoolean(), isAdmin)
    }

    override suspend fun saveCurrentUser(user: User) {
        sharedPreferences.edit().apply {
            putString("password", user.password)
            putString("email", user.email)
            putString("mfa", user.mfa.toString())
            putBoolean("isAdmin", user.isAdmin)
            
            apply() //  asynchronously
        }
    }

}
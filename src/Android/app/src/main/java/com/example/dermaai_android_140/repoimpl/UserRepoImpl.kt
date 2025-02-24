package com.example.dermaai_android_140.repoimpl

import android.content.Context
import android.content.SharedPreferences
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.UserRepo

class UserRepoImpl(context : Context) : UserRepo {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    override suspend fun getCurrentUser(): User? {
        val password = sharedPreferences.getString("password", null)
        val email = sharedPreferences.getString("email", null)
        val mfa = sharedPreferences.getString("mfa", null)

        return if (email != null && password != null && mfa != null) {
            User(email,password,mfa.toBoolean()) // Create the user object
        } else {
            null // No user data found
        }
    }

    override suspend fun saveCurrentUser(user: User) {
        sharedPreferences.edit().apply {
            putString("password", user.password)
            putString("email", user.email)
            putString("mfa", user.mfa.toString())
            
            apply() //  asynchronously
        }
    }

}
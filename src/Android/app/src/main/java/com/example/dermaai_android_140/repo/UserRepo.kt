package com.example.dermaai_android_140.repo

import com.example.dermaai_android_140.myClasses.User

interface UserRepo {

    fun getCurrentUser(): User?
    suspend fun saveCurrentUser(user: User)
    
}
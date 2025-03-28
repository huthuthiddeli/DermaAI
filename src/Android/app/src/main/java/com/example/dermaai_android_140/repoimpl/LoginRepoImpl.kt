package com.example.dermaai_android_140.repoimpl

import android.annotation.SuppressLint
import android.widget.Toast
import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.LoginRepo
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlin.Result


class LoginRepoImpl : LoginRepo {



    override fun login(email: String, password: String, mfa: Boolean, url: String): Result<User> {
        val user = User(email, password, mfa)

        val result = API.callApi(url, "POST", user)

        if (result.isSuccess) {
            val receivedUser = Gson().fromJson(result.getOrNull(), User::class.java)
            return Result.success(receivedUser)
        } else if (result.isFailure) {
            val errorMessage = result.exceptionOrNull()?.message
            return Result.failure(Exception(errorMessage))
        }

        return Result.failure(Exception("Unexpected result"))
    }


        @SuppressLint("SuspiciousIndentation")
        override fun register(email : String, password : String, mfa : Boolean, url : String) : User?
        {
            val user = User(email,password, false)

            var receivedUserObject : User? = null


            val result = API.callApi(url, "POST", user)

                if (result.isSuccess) {


                    val receivedData = result.getOrNull()

                    val gson = Gson()

                    try{
                        receivedUserObject = gson.fromJson(receivedData, User::class.java)

                    }
                    catch (e: Exception)
                    {

                    }


                } else if (result.isFailure) {

                }

            return receivedUserObject
        }


        override fun setMFA(user : User?, url : String) : User?
        {
            val result = API.callApi(url, "POST", user)

            if (result.isSuccess) {
                val receivedData = result.getOrNull()

                val gson = Gson()

                val receivedUserObject = gson.fromJson(receivedData, User::class.java)
                return receivedUserObject
            }
            else
            {
                return null;
            }
        }




}
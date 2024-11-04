package com.example.dermaai_android_140.ui.accountinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.LoginApi
import com.example.dermaai_android_140.myClasses.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AccountinfoViewModel : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _isLoggedIn = MutableLiveData<Boolean>(false)
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    fun login(email : String, password : String)
    {
        viewModelScope.launch(Dispatchers.IO) {

            val loginDeferred = async(Dispatchers.IO) {
                val loginApi = LoginApi()
                loginApi.login(email, password)
            }

            val receivedUser = loginDeferred.await()

            if (receivedUser != null) {
                _isLoggedIn.postValue(true)
                _user.postValue(receivedUser)
                println("successful")
            } else {
                println("failed")
            }

        }

        println()
    }
    

}
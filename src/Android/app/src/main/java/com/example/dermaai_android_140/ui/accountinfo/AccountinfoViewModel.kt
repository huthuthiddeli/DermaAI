package com.example.dermaai_android_140.ui.accountinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dermaai_android_140.myClasses.LoginApi
import com.example.dermaai_android_140.myClasses.User

class AccountinfoViewModel : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    fun login(email : String, password : String)
    {
        val loginApi = LoginApi()
        _user.value = loginApi.login(email, password)
    }
    

}
package com.example.dermaai_android_140.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _stayLoggedIn = MutableLiveData<Boolean>(false)
    val stayLoggedIn: LiveData<Boolean> get() = _stayLoggedIn

    

    fun setStayLoggedIn(stayLoggedIn: Boolean)
    {
        _stayLoggedIn.value = stayLoggedIn
    }

    fun getStayLoggedIn() : Boolean?
    {
        return stayLoggedIn.value
    }


}
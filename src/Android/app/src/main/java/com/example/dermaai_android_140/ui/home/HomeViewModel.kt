package com.example.dermaai_android_140.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class HomeViewModel : ViewModel()
{

    /*
    private val userRepo: UserRepoImpl by KoinJavaComponent.inject(UserRepoImpl::class.java)

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> get() = _isAdmin

    fun checkIfAdmin()
    {
        viewModelScope.launch {

            val user = userRepo.getCurrentUser()
            _isAdmin.postValue(user?.isAdmin)
        }
    }



    private val _verifiedUser = MutableLiveData<User?>()
    val verifiedUser : LiveData<User?> get() = _verifiedUser

    fun getUser()
    {
        val user = viewModelScope.launch {
            val user = userRepo.getCurrentUser()
            _verifiedUser.postValue(user)
        }
    }*/
}
package com.example.dermaai_android_140.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class ResultViewModel : ViewModel(){

    private val userRepo: UserRepoImpl by KoinJavaComponent.inject(UserRepoImpl::class.java)

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> get() = _currentUser



    fun getCurrentUser() : User
    {
        return userRepo.getCurrentUser()
    }

}

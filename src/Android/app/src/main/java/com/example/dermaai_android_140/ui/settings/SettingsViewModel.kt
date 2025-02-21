package com.example.dermaai_android_140.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import kotlin.getValue

class SettingsViewModel : ViewModel() {


    private val userRepo: UserRepoImpl by KoinJavaComponent.inject(UserRepoImpl::class.java)

    fun setMfa(url : String) : User?
    {
        var receivedUser : User? = null

        viewModelScope.launch(Dispatchers.IO) {

            val mfaDeferred = async(Dispatchers.IO) {
                LoginRepoImpl.setMFA(userRepo.getCurrentUser(), url)
            }

            receivedUser = mfaDeferred.await()

        }

        return receivedUser
    }

}
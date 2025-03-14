package com.example.dermaai_android_140.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.myClasses.Authentication
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
import kotlin.getValue

class SettingsViewModel : ViewModel() {


    private val userRepo: UserRepoImpl by KoinJavaComponent.inject(UserRepoImpl::class.java)
    private val loginRepo: LoginRepoImpl by KoinJavaComponent.inject(LoginRepoImpl::class.java)


    fun setMfa(url : String) : User?
    {
        var receivedUser : User? = null
        
        viewModelScope.launch {

            receivedUser = withContext(Dispatchers.IO) {
                loginRepo.setMFA(userRepo.getCurrentUser(), url)
            }
        }
        return receivedUser
    }

    fun generate2faKey(context: Context): String {
        return Authentication.generateSecret(context)
    }

    fun validate2faCode(secret: String, code: String): Boolean {
        return Authentication.validateTOTP(secret, code)
    }



}
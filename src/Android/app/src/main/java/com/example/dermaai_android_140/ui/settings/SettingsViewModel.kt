package com.example.dermaai_android_140.ui.settings

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.Authentication
import com.example.dermaai_android_140.myClasses.PredictionImageList
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class SettingsViewModel : ViewModel() {

    private val userRepo: UserRepoImpl by KoinJavaComponent.inject(UserRepoImpl::class.java)
    private val loginRepo: LoginRepoImpl by KoinJavaComponent.inject(LoginRepoImpl::class.java)
    private val imageRepo: ImageRepoImpl by KoinJavaComponent.inject(ImageRepoImpl::class.java)

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    private val _allPredictions = MutableLiveData<PredictionImageList?>(null)
    val allPredictions: LiveData<PredictionImageList?> get() = _allPredictions

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _secretKey = MutableLiveData<String>()
    val secretKey: LiveData<String> get() = _secretKey

    fun syncImages(url: String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    imageRepo.loadPredictions(getCurrentUser(), url)
                }
                result.onSuccess { predictionList ->
                    _allPredictions.postValue(predictionList)
                }.onFailure { exception ->
                    _message.postValue("Sync Images Error: ${exception.message}")
                }
            } catch (e: Exception) {
                _message.postValue("Unexpected Sync Images Error: ${e.message}")
            }
        }
    }

    fun setMfa(url: String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    loginRepo.setMFA(userRepo.getCurrentUser(), url)
                }
                result.onSuccess { updatedUser ->
                    _currentUser.postValue(updatedUser)
                }.onFailure { exception ->
                    _message.postValue("Set MFA Error: ${exception.message}")
                }
            } catch (e: Exception) {
                _message.postValue("Unexpected Set MFA Error: ${e.message}")
            }
        }
    }

    fun signInFirebase(activity: Activity) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    Authentication.signInWithBrowser(activity, getCurrentUser().email)
                }
                _message.postValue("Opening Firebase Sign-In")
            } catch (e: Exception) {
                _message.postValue("Unexpected Sign-In Error: ${e.message}")
            }
        }
    }


    fun enable2FA(activity: Activity, userId: String){

        val secret = Authentication.enable2FA(activity, userId) { secret ->
            _message.postValue("2FA Enabled successfully")
            _secretKey.postValue(secret)
        }
    }

    fun generate2faKey(context: Context): String {
        //return Authentication.generateSecret(context)
        return ""
    }

    fun validate2faCode(secret: String, code: String): Boolean {
        //return Authentication.validateTOTP(secret, code)
        return false
    }

    fun getCurrentUser(): User {
        return try {
            userRepo.getCurrentUser()
        } catch (e: Exception) {
            _message.postValue("Get Current User Error: ${e.message}")
            User("", "", false, false)
        }
    }
}

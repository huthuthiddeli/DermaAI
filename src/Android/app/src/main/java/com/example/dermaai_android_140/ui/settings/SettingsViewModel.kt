package com.example.dermaai_android_140.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.Authentication
import com.example.dermaai_android_140.myClasses.ReceivedPredictionsAndImages
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

    private val _allPredictions = MutableLiveData<List<ReceivedPredictionsAndImages>>(null)
    val allPredictions: LiveData<List<ReceivedPredictionsAndImages>> get() = _allPredictions

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

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




    fun generate2faKey(context: Context): String {
        return Authentication.generateSecret(context)
    }

    fun validate2faCode(secret: String, code: String): Boolean {
        return Authentication.validateTOTP(secret, code)
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

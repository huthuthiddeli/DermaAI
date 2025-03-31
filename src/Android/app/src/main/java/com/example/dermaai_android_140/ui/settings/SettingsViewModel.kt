package com.example.dermaai_android_140.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.myClasses.Authentication
import com.example.dermaai_android_140.myClasses.PredictionImage
import com.example.dermaai_android_140.myClasses.PredictionImageList
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
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
    private val imageRepo: ImageRepoImpl by KoinJavaComponent.inject(ImageRepoImpl::class.java)

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    private val _allPredictions = MutableLiveData<PredictionImageList?>(null)
    val allPredictions: LiveData<PredictionImageList?> get() = _allPredictions


    private val _error = MutableLiveData<String?>()
    val error : LiveData<String?> get() = _error


    fun syncImages(url: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {

                imageRepo.loadPredictions(getCurrentUser(), url)
            }
            result.onSuccess { predictionList ->
                _allPredictions.postValue(predictionList)
            }.onFailure { exception ->
                _error.postValue(exception.message)
                exception.printStackTrace()
            }
        }
    }


    fun setMfa(url: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                loginRepo.setMFA(userRepo.getCurrentUser(), url)
            }
            result.onSuccess { updatedUser ->
                _currentUser.postValue(updatedUser)
            }.onFailure { exception ->
                _error.postValue(exception.message)
                exception.printStackTrace()
            }
        }
    }


    fun generate2faKey(context: Context): String {
        return Authentication.generateSecret(context)
    }

    fun validate2faCode(secret: String, code: String): Boolean {
        return Authentication.validateTOTP(secret, code)
    }





    fun getCurrentUser() : User
    {
        return userRepo.getCurrentUser()
    }


}
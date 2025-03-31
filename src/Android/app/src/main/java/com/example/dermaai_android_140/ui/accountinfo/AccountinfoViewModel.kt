package com.example.dermaai_android_140.ui.accountinfo


import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.HealthCheckResponse
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.LoginRepo
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


//import org.koin.core.Koin


class AccountinfoViewModel() : ViewModel() {
    
    private val loginRepo: LoginRepoImpl by KoinJavaComponent.inject(LoginRepoImpl::class.java)
    private val userRepo: UserRepoImpl by KoinJavaComponent.inject(UserRepoImpl::class.java)


    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password
    
    private val _isLoggedIn = MutableLiveData<Boolean>(false)
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    private val _mfaEnabled = MutableLiveData<Boolean>(false)
    val mfaEnabled: LiveData<Boolean> get() = _mfaEnabled

    private var _key = String()
    private var _user : User? = null

    private val _verifiedUser = MutableLiveData<Boolean>(false)
    val verifiedUser : LiveData<Boolean> get() = _verifiedUser

    private val _registerCount = MutableLiveData<Int>(0)
    val registerCount: LiveData<Int> get() = _registerCount

    private val _isHealthy = MutableLiveData<String>()
    val isHealthy: LiveData<String> get() = _isHealthy

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message


    var healthCheckJob: Job? = null

    private var myJob: Job? = null

    //private val _stayLoggedIn = MutableLiveData<Boolean>(false)
    //val stayLoggedIn: LiveData<Boolean> get() = _stayLoggedIn

/*
    fun setStayLoggedIn(stayLoggedIn: Boolean)
    {
        _stayLoggedIn.value = stayLoggedIn
    }

    fun getStayLoggedIn() : Boolean?
    {
        return stayLoggedIn.value
    }*/

    fun setEmail(email: String) {
        _email.value = email
    }



    fun setPassword(password: String) {
        _password.value = password
    }

    fun setIsLoggedIn(isLoggedIn : Boolean)
    {
        _isLoggedIn.value = isLoggedIn
    }

    fun getKey() : String
    {
        return _key
    }

    fun getUser() : User?
    {
        return _user
    }



    fun register(email: String, password: String, url: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                loginRepo.register(email, password, false, url)
            }

            result.onSuccess { receivedUser ->
                _user = receivedUser
                _message.postValue("Registration successful")
                _registerCount.postValue(_registerCount.value?.plus(1) ?: 1)
            }

            result.onFailure { exception ->
                _message.postValue(exception.message)
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun login(email: String, password: String, mfa: Boolean, url: String) {
        // var receivedUser: User? = null // You don't need this line here.

        viewModelScope.launch {
            // Call the repository function
            val result = withContext(Dispatchers.IO) {
                loginRepo.login(email, password, mfa, url)
            }

            // Handle the result properly
            result.onSuccess { receivedUser ->
                Log.d("Tag", receivedUser.toString())

                // success: do something with the user
                _user = receivedUser
                receivedUser.password = password
                userRepo.saveCurrentUser(receivedUser)

                _mfaEnabled.postValue(_user!!.mfa)

                if (receivedUser.mfa) {
                    _mfaEnabled.postValue(receivedUser.mfa)
                } else {
                    _isLoggedIn.postValue(true)
                }
            }
            result.onFailure { exception ->
                _message.postValue(exception.message)
            }
        }
    }





    fun createTestUser()
    {
        val hardcodedUser = User(
            email = "string",
            password = "string",
            mfa = true,
            isAdmin = true
        )

        val gson = Gson()
        val userJson = gson.toJson(hardcodedUser)
        val receivedUserObject = gson.fromJson(userJson, User::class.java)

        viewModelScope.launch {
            userRepo.saveCurrentUser(receivedUserObject)
        }
    }


    private fun checkHealth(model : HealthCheckResponse, url : String)
    {
        viewModelScope.launch {

            val result = withContext(Dispatchers.IO) {
                loginRepo.checkHealth(model, url)
            }

            fun checkHealth(model: HealthCheckResponse, url : String) {
                viewModelScope.launch {
                    val result = withContext(Dispatchers.IO) {
                        loginRepo.checkHealth(model, url)
                    }

                    _isHealthy.postValue(result)

                }
            }


        }

    }

    fun startHealthCheck(model: HealthCheckResponse, url: String) {
        // Cancel any existing job
        healthCheckJob?.cancel()
        healthCheckJob = viewModelScope.launch {
            while (isActive) {
                checkHealth(model, url)
                delay(10_000)
            }
        }
    }

}




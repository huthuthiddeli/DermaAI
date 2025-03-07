package com.example.dermaai_android_140.ui.accountinfo


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.LoginRepo
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

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

    fun setUser(user : User?)
    {
        _user = user
    }


    fun register(email : String, password : String, url : String)
    {

        var receivedUser: User? = null

        myJob = viewModelScope.launch{

            receivedUser  = withContext(Dispatchers.IO) {
                loginRepo.register(email, password, false, url)
            }

            if (receivedUser != null) {
                _user = receivedUser

            } else {
                println("failed")
            }
        }

        myJob?.invokeOnCompletion {throwable ->

            if (throwable == null) {
                _registerCount.postValue(_registerCount.value!! + 1)
            }

        }
    }
    

    @OptIn(ExperimentalCoroutinesApi::class)
    fun login(email : String, password : String, mfa: Boolean, url : String) {
        /*
        if(stayLoggedIn.value == true)
        {

        }*/

        var receivedUser: User? = null

        fun login(email: String, password: String, mfa: Boolean, url: String) {

            var receivedUser: User? = null

            viewModelScope.launch {
                
                val receivedUser = withContext(Dispatchers.IO) {
                    loginRepo.login(email, password, mfa, url)
                }


                // succesfull
                if (receivedUser != null) {

                    _user = receivedUser
                    userRepo.saveCurrentUser(receivedUser)
                    _mfaEnabled.postValue(_user!!.mfa)


                    if (receivedUser.mfa) {

                        _mfaEnabled.postValue(receivedUser.mfa)

                    } else {
                        _isLoggedIn.postValue(true)
                    }

                } else {
                    println("failed")
                }


            }
        }


    }


}
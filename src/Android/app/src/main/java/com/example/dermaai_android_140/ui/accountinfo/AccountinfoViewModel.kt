package com.example.dermaai_android_140.ui.accountinfo


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.Authentication
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.ImageRepo
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

//import org.koin.core.Koin


class AccountinfoViewModel() : ViewModel() {


    private val loginRepo: LoginRepoImpl by KoinJavaComponent.inject(LoginRepoImpl::class.java)

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

    fun loginTest()
    {
        data class UserRequest(val name: String, val job: String)
        val requestModel = UserRequest(name = "John Doe", job = "Software Developer")

        viewModelScope.launch(Dispatchers.IO) {

            // Example URL
            val result = API.callApi("https://reqres.in/api/users","","GET",requestModel)

            withContext(Dispatchers.Main) {

                if (result.isSuccess) {
                    val receivedData = result.getOrNull()


                } else if (result.isFailure) {

                }
            }
        }
    }

    fun login(email : String, password : String)
    {
        /*
        if(stayLoggedIn.value == true)
        {

        }*/

        viewModelScope.launch(Dispatchers.IO) {

            val loginDeferred = async(Dispatchers.IO) {
                loginRepo.login(email, password, false, "")
            }

            val receivedUser = loginDeferred.await()

            // succesfull
            if (receivedUser != null) {

                //_user.postValue(receivedUser)


                if(receivedUser.mfa)
                {
                    _mfaEnabled.value = receivedUser.mfa
                    _key = receivedUser.key

                    /*
                    val auth = Authentication()
                    val enteredCode = 0

                    if(auth.validateTOTP(receivedUser.key,enteredCode.toString()))
                    {
                        _isLoggedIn.postValue(true)
                    }*/
                }
                // failed
                else{
                    _isLoggedIn.postValue(true)
                }

            } else {
                println("failed")
            }

        }

        println()
    }


    fun getUser(): User? {

        var user : User? = null

        viewModelScope.launch(Dispatchers.IO) {
            user = loginRepo.getUser()
        }

        return user
    }


}
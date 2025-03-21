package com.example.dermaai_android_140.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.ModelTrainer
import com.example.dermaai_android_140.myClasses.Retrain
import com.example.dermaai_android_140.myClasses.RetrainAll
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.AdminRepoImpl
import com.example.dermaai_android_140.repoimpl.ModelRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class AdminViewModel : ViewModel() {

    private val adminRepo: AdminRepoImpl by KoinJavaComponent.inject(AdminRepoImpl::class.java)
    private val userRepo: UserRepoImpl by KoinJavaComponent.inject(UserRepoImpl::class.java)

    private val _responseString = MutableLiveData<String?>()
    val responseString: LiveData<String?> get() = _responseString

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _models = MutableLiveData<ModelTrainer?>()
    val models: LiveData<ModelTrainer?> get() = _models

    private var myJob: Job? = null
    
    private val modelRepo: ModelRepoImpl by KoinJavaComponent.inject(ModelRepoImpl::class.java)

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    
    fun retrainAll(url : String, model : RetrainAll){

        viewModelScope.launch {
            try {

                val responseString = withContext(Dispatchers.IO) {

                    adminRepo.retrainAllModel(model,url)
                }

                _responseString.postValue(responseString)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue(e.message)
            }
        }
    }


    fun retrain(url : String, model : Retrain){

        viewModelScope.launch {
            try {

                val responseString = withContext(Dispatchers.IO) {
                    adminRepo.retrainModel(model,url)
                }

                _responseString.postValue(responseString)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue(e.message)
            }
        }

    }



    fun getModels(url: String) {

        try {
            myJob = viewModelScope.launch {
                val receivedModels = withContext(Dispatchers.IO) {
                    modelRepo.getModels(url)
                }

                if (receivedModels != null) {
                    _models.postValue(receivedModels)
                } else {
                    _models.postValue(null)
                }
            }
        } catch(e : Exception)
        {
            _error.postValue(e.message)
        }

        myJob?.invokeOnCompletion { throwable ->

        }
    }

    fun setCurrentUser(){
        viewModelScope.launch {
            _currentUser.postValue(userRepo.getCurrentUser())
        }
    }

    fun getCurrentUser() : User?
    {
        return currentUser.value
    }




}
package com.example.dermaai_android_140.ui.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.ModelTrainer
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import com.example.dermaai_android_140.repoimpl.ModelRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
import java.io.File

//import org.koin.core.Koin


class PhotoViewModel() : ViewModel() {


    private val _requestCameraPermission = MutableLiveData<Int>(0)
    val requestCameraPermission: LiveData<Int> get() = _requestCameraPermission

    private val _currentImage = MutableLiveData<File>()
    val currentImage : LiveData<File> get() = _currentImage

    private val _error = MutableLiveData<String?>()
    val error : LiveData<String?> get() = _error


    private val modelRepo: ModelRepoImpl by KoinJavaComponent.inject(ModelRepoImpl::class.java)


    private val _models = MutableLiveData<ModelTrainer?>()
    val models: LiveData<ModelTrainer?> get() = _models


    private lateinit var tmpImage : File


    fun getModels(url: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                modelRepo.getModels(url)
            }
            result.onSuccess { receivedModels ->
                _models.postValue(receivedModels)
            }.onFailure { exception ->
                exception.printStackTrace()
                _error.postValue(exception.message)
            }
        }
    }


    fun requestCameraPermission()
    {
        val value = requestCameraPermission.value!! + 1
        _requestCameraPermission.postValue(value)
    }

    fun resetCameraPermissionRequest() {
        _requestCameraPermission.value = 0
    }

    fun setCurrentImage(photoFile : File)
    {
        _currentImage.postValue(photoFile)
    }

    fun setTmpImage(tmpImage : File)
    {
        this.tmpImage = tmpImage
    }







}


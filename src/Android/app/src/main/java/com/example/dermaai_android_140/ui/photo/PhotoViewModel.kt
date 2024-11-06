package com.example.dermaai_android_140.ui.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.net.Uri
import com.example.dermaai_android_140.repo.ImageRepo
//import org.koin.core.Koin


class PhotoViewModel(private val imageRepo: ImageRepo) : ViewModel() {

    private val _requestCameraPermission = MutableLiveData<Int>(0)
    val requestCameraPermission: LiveData<Int> get() = _requestCameraPermission

    private val _currentImage = MutableLiveData<Uri>()
    val currentImage: LiveData<Uri> get() = _currentImage



    fun requestCameraPermission()
    {
        val value = requestCameraPermission.value!! + 1
        _requestCameraPermission.postValue(value)
    }

    fun resetCameraPermissionRequest() {
        _requestCameraPermission.value = 0 // Reset to 0
    }

    fun sendImage()
    {
        //imageRepo.sendImage()
    }

}


package com.example.dermaai_android_140.ui.photo

import android.media.ExifInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.net.Uri
import com.example.dermaai_android_140.myClasses.Storage
import com.example.dermaai_android_140.repo.ImageRepo
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import org.koin.java.KoinJavaComponent
import java.io.File
import java.io.IOException
import kotlin.getValue

//import org.koin.core.Koin


class PhotoViewModel() : ViewModel() {


    private val _requestCameraPermission = MutableLiveData<Int>(0)
    val requestCameraPermission: LiveData<Int> get() = _requestCameraPermission

    private val _currentImage = MutableLiveData<File>()
    val currentImage : LiveData<File> get() = _currentImage

    private lateinit var tmpImage : File

    private val imageRepo: ImageRepoImpl by KoinJavaComponent.inject(ImageRepoImpl::class.java)

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

    fun sendImage(image : File)
    {
        imageRepo.sendImage()
    }

}


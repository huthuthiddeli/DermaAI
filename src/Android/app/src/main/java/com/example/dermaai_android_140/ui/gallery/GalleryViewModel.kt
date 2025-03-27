package com.example.dermaai_android_140.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.Storage
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import java.io.File


class GalleryViewModel : ViewModel() {

    private val _images = MutableLiveData<List<File>>()
    val images: LiveData<List<File>> get() = _images

    private val userRepo: UserRepoImpl by KoinJavaComponent.inject(UserRepoImpl::class.java)

    fun loadImages(filesDir : File?) {

        viewModelScope.launch(Dispatchers.IO) {
            val retrievedImages = Storage.retrieveImagesFromStorage(filesDir, userRepo.getCurrentUser()!!.email)
            _images.postValue(retrievedImages)
        }
    }






}
package com.example.dermaai_android_140.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class GalleryViewModel : ViewModel() {

    private val _images = MutableLiveData<List<File>>()
    val images: LiveData<List<File>> get() = _images

    fun loadImages(filesDir : File?) {

        viewModelScope.launch(Dispatchers.IO) {
            val retrievedImages = Storage.retrieveImagesFromStorage(filesDir, true)
            _images.postValue(retrievedImages)
        }
    }

}
package com.example.dermaai_android_140.ui.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.Storage
import com.example.dermaai_android_140.ui.result.ResultActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class GalleryViewModel : ViewModel() {

    private val _images = MutableLiveData<List<File>>()
    val images: LiveData<List<File>> get() = _images

    fun loadImages(filesDir : File?) {

        viewModelScope.launch(Dispatchers.IO) {
            val storage = Storage()
            val retrievedImages = storage.retrieveImagesFromStorage(filesDir, true)
            _images.postValue(retrievedImages)
        }
    }
    


}
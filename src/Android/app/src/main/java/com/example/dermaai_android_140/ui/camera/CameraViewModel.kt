/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.dermaai_android_140.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.AiModel
import com.example.dermaai_android_140.myClasses.Prediction
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class CameraViewModel : ViewModel() {

    private val imageRepo: ImageRepoImpl by KoinJavaComponent.inject(ImageRepoImpl::class.java)
    private var myJob: Job? = null


    private val _prediction = MutableLiveData<Prediction?>()
    val prediction: LiveData<Prediction?> get() = _prediction


    private lateinit var lastPath : String

    fun getLastPath() : String
    {
        return lastPath
    }



    fun sendImage(url: String, base64Image: String, lastPathOfImg : String?)
    {

        var prediction : Prediction? = null

        myJob =  viewModelScope.launch {
            // Use withContext to switch to the IO dispatcher for network operations
            val receivedImage = withContext(Dispatchers.IO) {

                val model = AiModel(0,"ModelTrainerPyTorch", base64Image)

                lastPath = lastPathOfImg.toString()

                prediction = imageRepo.sendImage(model,url)
            }
        }

        myJob?.invokeOnCompletion {throwable ->

            if (throwable == null) {
                _prediction.postValue(prediction)
            }

        }
    }


}
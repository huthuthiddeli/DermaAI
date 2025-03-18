package com.example.dermaai_android_140.ui.resize

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.AiModel
import com.example.dermaai_android_140.myClasses.Image
import com.example.dermaai_android_140.myClasses.Prediction
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class ResizeViewModel : ViewModel() {

    private val imageRepo: ImageRepoImpl by KoinJavaComponent.inject(ImageRepoImpl::class.java)

    private val _prediction = MutableLiveData<Prediction?>()
    val prediction: LiveData<Prediction?> get() = _prediction

    private val _resizedImage = MutableLiveData<Image?>()
    val resizedImage: LiveData<Image?> get() = _resizedImage

    private lateinit var lastPath: String

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun getLastPath(): String {
        return lastPath
    }

    fun sendImage(url: String, modelIndex : Int, trainerString : String, base64Image: String, lastPathOfImg: String?) {

        viewModelScope.launch {
            try {

                val prediction = withContext(Dispatchers.IO) {

                    val model = AiModel(modelIndex, trainerString, base64Image)
                    lastPath = lastPathOfImg.toString()
                    imageRepo.sendImage(model, url)
                }

                _prediction.postValue(prediction)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue(e.message)
                _prediction.postValue(null)
            }
        }
    }


    fun resizeImage(url: String, base64Image: String)
    {
        viewModelScope.launch {
            try {

                val resizedImage = withContext(Dispatchers.IO) {

                    //lastPath = lastPathOfImg.toString()
                    imageRepo.resizeImage(url, base64Image)
                }

                _resizedImage.postValue(resizedImage)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue(e.message)
            }
        }

    }

}
package com.example.dermaai_android_140.ui.resize

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.AiModel
import com.example.dermaai_android_140.myClasses.Image
import com.example.dermaai_android_140.myClasses.Prediction
import com.example.dermaai_android_140.myClasses.PredictionImage
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class ResizeViewModel : ViewModel() {

    private val imageRepo: ImageRepoImpl by KoinJavaComponent.inject(ImageRepoImpl::class.java)
    private val userRepo: UserRepoImpl by KoinJavaComponent.inject(UserRepoImpl::class.java)

    private val _prediction = MutableLiveData<Prediction?>()
    val prediction: LiveData<Prediction?> get() = _prediction

    private val _resizedImage = MutableLiveData<Image?>()
    val resizedImage: LiveData<Image?> get() = _resizedImage

    private lateinit var lastPath: String

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _response = MutableLiveData<String?>(null)
    val response: LiveData<String?> get() = _response

    fun getLastPath(): String {
        return lastPath
    }

    fun sendImage(url: String, modelIndex: Int, trainerString: String, base64Image: String, lastPathOfImg: String?) {
        viewModelScope.launch {

            val result = withContext(Dispatchers.IO) {
                val model = AiModel(modelIndex, trainerString, base64Image)
                lastPath = lastPathOfImg.toString()
                imageRepo.sendImage(model, url)
            }
            result.onSuccess { predictionResult ->
                _prediction.postValue(predictionResult)
            }.onFailure { exception ->
                exception.printStackTrace()
                _error.postValue(exception.message ?: "Unknown error occurred")
            }
        }
    }

    fun savePrediction(url: String, model: PredictionImage) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                imageRepo.savePrediction(model, url)
            }
            result.onSuccess { responseString ->
                _response.postValue(responseString)
            }.onFailure { exception ->
                exception.printStackTrace()
                _error.postValue(exception.message)
                _response.postValue(null)
            }
        }
    }

    fun resizeImage(url: String, base64: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                imageRepo.resizeImage(url, base64)
            }
            result.onSuccess { imageResult ->
                _resizedImage.postValue(imageResult)
            }.onFailure { exception ->
                exception.printStackTrace()
                _error.postValue(exception.message)
            }
        }
    }

    fun getCurrentUser(): User {
        return userRepo.getCurrentUser()
    }
}

package com.example.dermaai_android_140.ui.resize

import android.util.Log
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
import kotlinx.coroutines.Job
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

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> get() = _currentUser

    private val _response = MutableLiveData<String?>(null)
    val response: LiveData<String?> get() = _response


    fun getLastPath(): String {
        return lastPath
    }

    fun sendImage(url: String, modelIndex: Int, trainerString: String, base64Image: String, lastPathOfImg: String?) {
        viewModelScope.launch {
            val prediction = try {

                withContext(Dispatchers.IO) {
                    val model = AiModel(modelIndex, trainerString, base64Image)
                    lastPath = lastPathOfImg.toString()
                    imageRepo.sendImage(model, url) // This should return the prediction
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue(e.message ?: "Unknown error occurred")
                null
            }
            
            _prediction.postValue(prediction)
        }
    }




    fun savePrediction(url: String, model : PredictionImage) {

        viewModelScope.launch {
            try {

                val response = withContext(Dispatchers.IO) {

                    imageRepo.savePrediction(model, url)
                }

                _response.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue(e.message)
                _response.postValue(null)
            }
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





    fun resizeImage(url: String, base64 : String)
    {
        viewModelScope.launch {
            try {

                val resizedImage = withContext(Dispatchers.IO) {

                    //lastPath = lastPathOfImg.toString()
                    imageRepo.resizeImage(url, base64)
                }


                _resizedImage.postValue(resizedImage)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue(e.message)
            }
        }

    }

}

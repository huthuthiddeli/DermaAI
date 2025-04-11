package com.example.dermaai_android_140.ui.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.ModelResponse
import com.example.dermaai_android_140.repoimpl.ModelRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
import kotlin.getValue

class ModelViewModel : ViewModel() {

    private val modelRepo: ModelRepoImpl by KoinJavaComponent.inject(ModelRepoImpl::class.java)

    private val _modelsInfo = MutableLiveData<ModelResponse?>()
    val modelsInfo: LiveData<ModelResponse?> get() = _modelsInfo


    private val _message = MutableLiveData<String?>()
    val message : LiveData<String?> get() = _message

    fun getModelsDescription(url : String)
    {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                modelRepo.getModelsDescription(url)
            }
            result.onSuccess { receivedModels ->
                _modelsInfo.postValue(receivedModels)
            }.onFailure { exception ->
                exception.printStackTrace()
                _message.postValue(exception.message)
            }
        }
    }


}
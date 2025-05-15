package com.example.dermaai_android_140.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.myClasses.ModelTrainer
import com.example.dermaai_android_140.myClasses.Report
import com.example.dermaai_android_140.myClasses.ReportAll
import com.example.dermaai_android_140.myClasses.Retrain
import com.example.dermaai_android_140.myClasses.RetrainAll
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.AdminRepoImpl
import com.example.dermaai_android_140.repoimpl.ModelRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

class AdminViewModel : ViewModel() {

    private val adminRepo: AdminRepoImpl by KoinJavaComponent.inject(AdminRepoImpl::class.java)
    private val userRepo: UserRepoImpl by KoinJavaComponent.inject(UserRepoImpl::class.java)
    private val modelRepo: ModelRepoImpl by KoinJavaComponent.inject(ModelRepoImpl::class.java)

    private val _responseString = MutableLiveData<String?>()
    val responseString: LiveData<String?> get() = _responseString

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _models = MutableLiveData<ModelTrainer?>()
    val models: LiveData<ModelTrainer?> get() = _models

    private var myJob: Job? = null

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> get() = _currentUser

    private val _report = MutableLiveData<String>()
    val report: LiveData<String> get() = _report

    private val _allReports = MutableLiveData<String>()
    val allReports: LiveData<String> get() = _allReports

    private val timeoutMillis = 10_000L

    fun retrainAll(url: String, model: RetrainAll) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                adminRepo.retrainAllModel(model, url)
            }
            result.onSuccess { response ->
                _responseString.postValue(response.message)
            }.onFailure { exception ->
                exception.printStackTrace()
                _message.postValue(exception.message)
            }
        }
    }

    fun retrain(url: String, model: Retrain) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                adminRepo.retrainModel(model, url)
            }
            result.onSuccess { response ->
                _responseString.postValue(response.message)
            }.onFailure { exception ->
                exception.printStackTrace()
                _message.postValue(exception.message)
            }
        }
    }

    fun getModels(url: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                modelRepo.getModels(url)
            }
            result.onSuccess { receivedModels ->
                _models.postValue(receivedModels)
            }.onFailure { exception ->
                exception.printStackTrace()
                _message.postValue(exception.message)
            }
        }
    }

    fun setCurrentUser() {
        viewModelScope.launch {
            try {
                val user = userRepo.getCurrentUser()
                _currentUser.postValue(user)
            } catch (e: Exception) {
                // Handle error (e.g., post error state)
                _message.postValue("User could not be found!")
            }
        }
    }

    fun getCurrentUser(): User? {
        return currentUser.value
    }

    fun getAllReports(url: String, model: ReportAll) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                adminRepo.getAllReports(model, url)
            }
            result.onSuccess { allReportsJson ->
                _allReports.postValue(allReportsJson)
            }.onFailure { exception ->
                exception.printStackTrace()
                _message.postValue(exception.message)
            }
        }
    }
    


    fun getOneReport(url: String, model: Report) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                adminRepo.getOneReport(model, url)
            }
            result.onSuccess { reportJson ->
                //val gson = Gson()
                //val receivedReport = gson.fromJson(reportJson, ReceivedReport::class.java)
                _report.postValue(reportJson)
            }.onFailure { exception ->
                exception.printStackTrace()
                _message.postValue(exception.message)
            }
        }
    }
}


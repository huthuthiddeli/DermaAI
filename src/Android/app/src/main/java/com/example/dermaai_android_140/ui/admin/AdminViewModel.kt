package com.example.dermaai_android_140.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermaai_android_140.R
import com.example.dermaai_android_140.myClasses.ModelTrainer
import com.example.dermaai_android_140.myClasses.ReceivedReport
import com.example.dermaai_android_140.myClasses.ReceivedReportAll
import com.example.dermaai_android_140.myClasses.Report
import com.example.dermaai_android_140.myClasses.ReportAll
import com.example.dermaai_android_140.myClasses.Retrain
import com.example.dermaai_android_140.myClasses.RetrainAll
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repoimpl.AdminRepoImpl
import com.example.dermaai_android_140.repoimpl.ModelRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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

    private val _report = MutableLiveData<ReceivedReport>()
    val report: LiveData<ReceivedReport> get() = _report

    private val _allReports = MutableLiveData<ReceivedReportAll>()
    val allReports: LiveData<ReceivedReportAll> get() = _allReports

    private val timeoutMillis = 10_000L

    fun retrainAll(url: String, model: RetrainAll) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                adminRepo.retrainAllModel(model, url)
            }
            result.onSuccess { responseString ->
                _responseString.postValue(responseString)
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
            result.onSuccess { responseString ->
                _responseString.postValue(responseString)
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

    fun setCurrentUser(){
        
        val currentUserJob = viewModelScope.launch {
            userRepo.getCurrentUser()
        }

        currentUserJob.invokeOnCompletion {
            _currentUser.postValue(currentUser.value)
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
                val gson = Gson()
                val receivedReport = gson.fromJson(allReportsJson, ReceivedReportAll::class.java)
                _allReports.postValue(receivedReport)
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
                val gson = Gson()
                val receivedReport = gson.fromJson(reportJson, ReceivedReport::class.java)
                _report.postValue(receivedReport)
            }.onFailure { exception ->
                exception.printStackTrace()
                _message.postValue(exception.message)
            }
        }
    }
}

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import org.koin.java.KoinJavaComponent

class AdminViewModel : ViewModel() {

    private val adminRepo: AdminRepoImpl by KoinJavaComponent.inject(AdminRepoImpl::class.java)
    private val userRepo: UserRepoImpl by KoinJavaComponent.inject(UserRepoImpl::class.java)

    private val _responseString = MutableLiveData<String?>()
    val responseString: LiveData<String?> get() = _responseString

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _models = MutableLiveData<ModelTrainer?>()
    val models: LiveData<ModelTrainer?> get() = _models

    private var myJob: Job? = null
    
    private val modelRepo: ModelRepoImpl by KoinJavaComponent.inject(ModelRepoImpl::class.java)

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> get() = _currentUser

    private val _report = MutableLiveData<ReceivedReport>()
    val report: LiveData<ReceivedReport> get() = _report

    private val _allReports = MutableLiveData<ReceivedReportAll>()
    val allReports: LiveData<ReceivedReportAll> get() = _allReports


    private val timeoutMillis = 10_000L


    fun retrainAll(url : String, model : RetrainAll){

        viewModelScope.launch {
            try {

                val responseString = withContext(Dispatchers.IO) {

                    adminRepo.retrainAllModel(model,url)
                }

                _responseString.postValue(responseString)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue(e.message)
            }
        }
    }


    fun retrain(url : String, model : Retrain){

        viewModelScope.launch {
            try {

                val responseString = withContext(Dispatchers.IO) {
                    adminRepo.retrainModel(model,url)
                }

                _responseString.postValue(responseString)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue(e.message)
            }
        }

    }





    fun getModels(url: String) {

        try {
            myJob = viewModelScope.launch {
                val receivedModels = withContext(Dispatchers.IO) {
                    modelRepo.getModels(url)
                }

                if (receivedModels != null) {
                    _models.postValue(receivedModels)
                } else {
                    _models.postValue(null)
                }
            }
        }catch (e: TimeoutCancellationException) {
            _error.postValue("Operation timed out after 10 seconds")
        } catch(e : Exception)
        {
            _error.postValue(e.message)
        }

        myJob?.invokeOnCompletion { throwable ->

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


    fun getCurrentUser() : User?
    {
        return currentUser.value
    }




    fun getAllReports(url : String, model : ReportAll)
    {
        viewModelScope.launch {
            try {

                val allReportsJson = withContext(Dispatchers.IO) {
                    adminRepo.getAllReports(model,url)
                }

                val gson = Gson()
                val receivedReport = gson.fromJson(allReportsJson, ReceivedReportAll::class.java)

                _allReports.postValue(receivedReport)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue(e.message)
            }
        }
    }


    fun getOneReport(url : String, model: Report)
    {
        viewModelScope.launch {
            try {

                val reportJson = withContext(Dispatchers.IO) {
                    adminRepo.getOneReport(model,url)
                }

                val gson = Gson()
                val receivedReport = gson.fromJson(reportJson, ReceivedReport::class.java)

                _report.postValue(receivedReport)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.postValue(e.message)
            }
        }

    }

}
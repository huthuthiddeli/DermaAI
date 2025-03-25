package com.example.dermaai_android_140.repoimpl

import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.AiModel
import com.example.dermaai_android_140.myClasses.Prediction
import com.example.dermaai_android_140.myClasses.Report
import com.example.dermaai_android_140.myClasses.ReportAll
import com.example.dermaai_android_140.myClasses.Retrain
import com.example.dermaai_android_140.myClasses.RetrainAll
import com.example.dermaai_android_140.repo.AdminRepo
import com.google.gson.Gson

class AdminRepoImpl : AdminRepo {

    override fun retrainModel(model : Retrain?, url : String) : String? {

        val result = API.callApi(url, "POST", model)

        if (result.isSuccess) {

            val receivedData = result.getOrNull()

            try{
                val stringResponse = Gson().fromJson(receivedData, String::class.java)
                return stringResponse
            }
            catch (e: Exception)
            {

            }

        } else if (result.isFailure) {
            result.exceptionOrNull()?.message
        }

        return null

    }

    override fun retrainAllModel(model : RetrainAll, url : String) : String? {

        val result = API.callApi(url, "POST", model)

        if (result.isSuccess) {

            val receivedData = result.getOrNull()

            try{
                val stringResponse = Gson().fromJson(receivedData, String::class.java)
                return stringResponse

            }
            catch (e: Exception)
            {

            }

        } else if (result.isFailure) {
            result.exceptionOrNull()?.message
        }

        return null
    }



    override fun getOneReport(model : Report, url : String) : String? {

        val result = API.callApi(url, "POST", model)
        
        if (result.isSuccess) {

            val receivedData = result.getOrNull()
            return receivedData

        } else if (result.isFailure) {
            result.exceptionOrNull()?.message
        }
        
        return null
    }

    override fun getAllReports(model : ReportAll, url : String) : String? {

        val result = API.callApi(url, "POST", model)

        if (result.isSuccess) {

            val receivedData = result.getOrNull()
            return receivedData

        } else if (result.isFailure) {
            result.exceptionOrNull()?.message
        }

        return null
    }

    


}
package com.example.dermaai_android_140.repoimpl

import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.Retrain
import com.example.dermaai_android_140.myClasses.RetrainAll
import com.example.dermaai_android_140.myClasses.Report
import com.example.dermaai_android_140.myClasses.ReportAll
import com.example.dermaai_android_140.repo.AdminRepo
import com.google.gson.Gson
import kotlin.Result

class AdminRepoImpl : AdminRepo {

    override fun retrainModel(model: Retrain?, url: String): Result<String> {
        val result = API.callApi(url, "POST", model)
        return result.fold(
            onSuccess = { response ->
                try {
                    val parsedResponse = Gson().fromJson(response, String::class.java)
                    Result.success(parsedResponse)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse response: ${e.message}"))
                }
            },
            onFailure = { exception ->
                Result.failure(Exception("Retraining failed: ${exception.message}"))
            }
        )
    }

    override fun retrainAllModel(model: RetrainAll, url: String): Result<String> {
        val result = API.callApi(url, "POST", model)
        return result.fold(
            onSuccess = { response ->
                try {
                    val parsedResponse = Gson().fromJson(response, String::class.java)
                    Result.success(parsedResponse)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse response: ${e.message}"))
                }
            },
            onFailure = { exception ->
                Result.failure(Exception("Retraining all failed: ${exception.message}"))
            }
        )
    }

    override fun getOneReport(model: Report, url: String): Result<String> {
        val result = API.callApi(url, "GET", model)
        return result.fold(
            onSuccess = { response ->
                try {
                    val parsedReport = Gson().fromJson(response, String::class.java)
                    Result.success(parsedReport)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse report: ${e.message}"))
                }
            },
            onFailure = { exception ->
                Result.failure(Exception("Failed to fetch report: ${exception.message}"))
            }
        )
    }

    override fun getAllReports(model: ReportAll, url: String): Result<String> {
        val result = API.callApi(url, "GET", model)
        return result.fold(
            onSuccess = { response ->
                try {
                    val parsedReports = Gson().fromJson(response, String::class.java)
                    Result.success(parsedReports)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse reports: ${e.message}"))
                }
            },
            onFailure = { exception ->
                Result.failure(Exception("Failed to fetch reports: ${exception.message}"))
            }
        )
    }
}
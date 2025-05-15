package com.example.dermaai_android_140.repoimpl

import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.ReceivedRetrain
import com.example.dermaai_android_140.myClasses.ReceivedRetrainAll
import com.example.dermaai_android_140.myClasses.Report
import com.example.dermaai_android_140.myClasses.ReportAll
import com.example.dermaai_android_140.myClasses.Retrain
import com.example.dermaai_android_140.myClasses.RetrainAll
import com.example.dermaai_android_140.repo.AdminRepo
import com.google.gson.Gson

class AdminRepoImpl : AdminRepo {

    // Fixed return type: Result<Retrain> instead of Result<String>
    override fun retrainModel(model: Retrain?, url: String): Result<ReceivedRetrain> {
        return try {
            val result = API.callApi(url, "POST", model)
            if (result.isSuccess) {
                try {
                    val parsedResponse = Gson().fromJson(result.getOrThrow(), ReceivedRetrain::class.java)
                    Result.success(parsedResponse)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse response: ${e.message}"))
                }
            } else {
                Result.failure(Exception("Retraining failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Retraining failed: ${e.message}"))
        }
    }

    // Fixed return type: Result<RetrainAll>
    override fun retrainAllModel(model: RetrainAll, url: String): Result<ReceivedRetrainAll> {
        return try {
            val result = API.callApi(url, "POST", model)
            if (result.isSuccess) {
                try {
                    val parsedResponse = Gson().fromJson(result.getOrThrow(), ReceivedRetrainAll::class.java)
                    Result.success(parsedResponse)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse response: ${e.message}"))
                }
            } else {
                Result.failure(Exception("Retraining all failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Retraining all failed: ${e.message}"))
        }
    }

    // Fixed return type: Result<ReceivedReport>
    override fun getOneReport(model: Report, url: String): Result<String> {
        return try {
            val result = API.callApi(url, "POST", model)
            if (result.isSuccess) {
                try {
                    //val parsedReport = Gson().fromJson(result.getOrThrow(), ReceivedReport::class.java)
                    Result.success(result.getOrThrow())
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse report: ${e.message}"))
                }
            } else {
                Result.failure(Exception("Failed to fetch report: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch report: ${e.message}"))
        }
    }

    // Fixed return type: Result<ReceivedReportAll>
    override fun getAllReports(model: ReportAll, url: String): Result<String> {
        return try {
            val result = API.callApi(url, "POST", model)
            if (result.isSuccess) {
                try {
                    //val parsedReports = Gson().fromJson(result.getOrThrow(), ReceivedReportAll::class.java)
                    Result.success(result.getOrThrow())
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse reports: ${e.message}"))
                }
            } else {
                Result.failure(Exception("Failed to fetch reports: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch reports: ${e.message}"))
        }
    }
}
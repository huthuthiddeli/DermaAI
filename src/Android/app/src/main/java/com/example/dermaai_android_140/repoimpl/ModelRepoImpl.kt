package com.example.dermaai_android_140.repoimpl

import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.ModelResponse
import com.example.dermaai_android_140.myClasses.ModelTrainer
import com.example.dermaai_android_140.repo.ModelRepo
import com.google.gson.Gson

class ModelRepoImpl : ModelRepo {

    override fun getModels(url: String): Result<ModelTrainer> {
        return try {
            val result = API.callApi(url, "GET", null)
            if (result.isSuccess) {
                try {
                    val models = Gson().fromJson(result.getOrThrow(), ModelTrainer::class.java)
                    Result.success(models)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse models: ${e.message}"))
                }
            } else {
                Result.failure(Exception("Failed to fetch models: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch models: ${e.message}"))
        }
    }


    override fun getModelsDescription(url : String): Result<ModelResponse>
    {
        return try {
            val result = API.callApi(url, "GET", null)
            if (result.isSuccess) {
                try {
                    val modelsInfo = Gson().fromJson(result.getOrThrow(), ModelResponse::class.java)
                    Result.success(modelsInfo)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse models: ${e.message}"))
                }
            } else {
                Result.failure(Exception("Failed to fetch models: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch models: ${e.message}"))
        }

    }
}
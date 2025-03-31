package com.example.dermaai_android_140.repoimpl

import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.ModelTrainer
import com.example.dermaai_android_140.repo.ModelRepo
import com.google.gson.Gson
import kotlin.Result

class ModelRepoImpl : ModelRepo {

    override fun getModels(url: String): Result<ModelTrainer> {
        val result = API.callApi(url, "GET")
        return result.fold(
            onSuccess = { response ->
                try {
                    val models = Gson().fromJson(response, ModelTrainer::class.java)
                    Result.success(models)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse models: ${e.message}"))
                }
            },
            onFailure = { exception ->
                Result.failure(Exception("Failed to fetch models: ${exception.message}"))
            }
        )
    }
}
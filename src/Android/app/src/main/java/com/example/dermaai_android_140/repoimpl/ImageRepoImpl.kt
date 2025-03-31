package com.example.dermaai_android_140.repoimpl

import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.AiModel
import com.example.dermaai_android_140.myClasses.Image
import com.example.dermaai_android_140.myClasses.Prediction
import com.example.dermaai_android_140.myClasses.PredictionImage
import com.example.dermaai_android_140.myClasses.PredictionImageList
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.ImageRepo
import com.google.gson.Gson
import kotlin.Result

class ImageRepoImpl : ImageRepo {

    override fun sendImage(model: AiModel, url: String): Result<Prediction> {
        val result = API.callApi(url, "POST", model)
        return result.fold(
            onSuccess = { response ->
                try {
                    val prediction = Gson().fromJson(response, Prediction::class.java)
                    Result.success(prediction)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse prediction: ${e.message}"))
                }
            },
            onFailure = { exception ->
                Result.failure(Exception("Image analysis failed: ${exception.message}"))
            }
        )
    }

    override fun savePrediction(model: PredictionImage, url: String): Result<String> {
        val result = API.callApi(url, "POST", model)
        return result.fold(
            onSuccess = { response ->
                Result.success(response)
            },
            onFailure = { exception ->
                Result.failure(Exception("Failed to save prediction: ${exception.message}"))
            }
        )
    }

    override fun loadPredictions(model: User, url: String): Result<PredictionImageList> {
        val result = API.callApi(url, "POST", model)
        return result.fold(
            onSuccess = { response ->
                try {
                    val predictions = Gson().fromJson(response, PredictionImageList::class.java)
                    Result.success(predictions)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse predictions: ${e.message}"))
                }
            },
            onFailure = { exception ->
                Result.failure(Exception("Failed to load predictions: ${exception.message}"))
            }
        )
    }

    override fun resizeImage(url: String, base64: String): Result<Image> {
        val image = Image(base64)
        val result = API.callApi(url, "POST", image)
        return result.fold(
            onSuccess = { response ->
                try {
                    val resizedImage = Gson().fromJson(response, Image::class.java)
                    Result.success(resizedImage)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse resized image: ${e.message}"))
                }
            },
            onFailure = { exception ->
                Result.failure(Exception("Image resize failed: ${exception.message}"))
            }
        )
    }
}
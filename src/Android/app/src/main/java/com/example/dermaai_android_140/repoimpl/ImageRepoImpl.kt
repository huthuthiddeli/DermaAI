package com.example.dermaai_android_140.repoimpl

import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.AiModel
import com.example.dermaai_android_140.myClasses.Image
import com.example.dermaai_android_140.myClasses.Prediction
import com.example.dermaai_android_140.myClasses.PredictionImage
import com.example.dermaai_android_140.myClasses.ReceivedPredictionsAndImages
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.ImageRepo
import com.google.gson.Gson

class ImageRepoImpl : ImageRepo {

    override fun sendImage(model: AiModel, url: String): Result<Prediction> {
        return try {
            val result = API.callApi(url, "POST", model)
            if (result.isSuccess) {
                try {
                    val prediction = Gson().fromJson(result.getOrThrow(), Prediction::class.java)
                    Result.success(prediction)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse prediction: ${e.message}"))
                }
            } else {
                Result.failure(Exception("Image analysis failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Image analysis failed: ${e.message}"))
        }
    }

    override fun savePrediction(model: PredictionImage, url: String): Result<String> {
        return try {
            val result = API.callApi(url, "POST", model)
            if (result.isSuccess) {
                Result.success(result.getOrThrow())
            } else {
                Result.failure(Exception("Failed to save prediction: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to save prediction: ${e.message}"))
        }
    }


    override fun loadPredictions(model: User, url: String): Result<List<ReceivedPredictionsAndImages>> {
        return try {
            val result = API.callApi(url, "POST", model)
            if (result.isSuccess) {
                try {
                    val jsonString = result.getOrThrow()
                    val predictions = Gson().fromJson(jsonString, Array<ReceivedPredictionsAndImages>::class.java).toList()
                    Result.success(predictions)
                } catch (e: Exception) {
                    Result.failure(Exception("Parsing failed: ${e.message}"))
                }
            } else {
                Result.failure(Exception("API error: ${result.exceptionOrNull()?.message ?: "Unknown"}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override fun resizeImage(url: String, base64: String): Result<Image> {
        return try {
            val image = Image(base64)
            val result = API.callApi(url, "POST", image)
            if (result.isSuccess) {
                try {
                    val resizedImage = Gson().fromJson(result.getOrThrow(), Image::class.java)
                    Result.success(resizedImage)
                } catch (e: Exception) {
                    Result.failure(Exception("Failed to parse resized image: ${e.message}"))
                }
            } else {
                Result.failure(Exception("Image resize failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Image resize failed: ${e.message}"))
        }
    }
}
package com.example.dermaai_android_140.repo

import com.example.dermaai_android_140.myClasses.AiModel
import com.example.dermaai_android_140.myClasses.Image
import com.example.dermaai_android_140.myClasses.Prediction
import com.example.dermaai_android_140.myClasses.PredictionImage
import com.example.dermaai_android_140.myClasses.PredictionImageList
import com.example.dermaai_android_140.myClasses.User

interface ImageRepo {

    fun sendImage(model : AiModel,url : String): Result<Prediction?>
    fun resizeImage(url : String, base64 : String) : Result<Image?>
    fun savePrediction(model : PredictionImage, url : String) : Result<String?>
    fun loadPredictions(model : User, url : String) : Result<PredictionImageList?>


}
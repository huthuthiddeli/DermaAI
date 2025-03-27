package com.example.dermaai_android_140.repo

import com.example.dermaai_android_140.myClasses.AiModel
import com.example.dermaai_android_140.myClasses.Image
import com.example.dermaai_android_140.myClasses.Prediction
import com.example.dermaai_android_140.myClasses.PredictionImage
import com.example.dermaai_android_140.myClasses.PredictionImageList
import com.example.dermaai_android_140.myClasses.User

interface ImageRepo {

    fun sendImage(model : AiModel,url : String): Prediction?
    fun resizeImage(url : String, base64 : String) : Image?
    fun savePrediction(model : PredictionImage, url : String) : String?
    fun loadPredictions(model : User, url : String) : PredictionImageList?


}
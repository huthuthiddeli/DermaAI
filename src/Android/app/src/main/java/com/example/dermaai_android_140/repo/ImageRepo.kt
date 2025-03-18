package com.example.dermaai_android_140.repo

import com.example.dermaai_android_140.myClasses.AiModel
import com.example.dermaai_android_140.myClasses.Image
import com.example.dermaai_android_140.myClasses.Prediction

interface ImageRepo {

    fun sendImage(model : AiModel,url : String): Prediction?
    fun resizeImage(url : String, base64Image : String) : Image?




}
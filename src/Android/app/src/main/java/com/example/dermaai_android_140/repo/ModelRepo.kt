package com.example.dermaai_android_140.repo;

import com.example.dermaai_android_140.myClasses.ModelResponse
import com.example.dermaai_android_140.myClasses.ModelTrainer


interface ModelRepo {

    fun getModels(url : String) : Result<ModelTrainer?>

    fun getModelsDescription(url : String): Result<ModelResponse>

}



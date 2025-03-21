package com.example.dermaai_android_140.repo

import com.example.dermaai_android_140.myClasses.Retrain
import com.example.dermaai_android_140.myClasses.RetrainAll

interface AdminRepo {

    fun retrainModel(model : Retrain, url : String) : String?

    fun retrainAllModel(model : RetrainAll, url : String) : String?

    fun getReport(model : Retrain, url : String) : String?
    
}
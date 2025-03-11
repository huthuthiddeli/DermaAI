package com.example.dermaai_android_140.repoimpl

import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.ModelTrainer
import com.example.dermaai_android_140.myClasses.User
import com.google.gson.Gson

class ModelRepoImpl {

    fun getModels(url : String) : ModelTrainer?
    {
        var models : ModelTrainer? = null

        val result = API.callApi(url, "GET")

        if (result.isSuccess) {
            val receivedData = result.getOrNull()
            val gson = Gson()
            try{
                
                models = gson.fromJson(receivedData, ModelTrainer::class.java)
                
            }
            catch (e: Exception)
            {

            }
        } else if (result.isFailure) {

        }

        return models
    }

}
package com.example.dermaai_android_140.repoimpl
import android.util.Base64
import com.example.dermaai_android_140.myClasses.API
import com.example.dermaai_android_140.myClasses.AiModel
import com.example.dermaai_android_140.myClasses.Prediction
import com.example.dermaai_android_140.myClasses.User
import com.example.dermaai_android_140.repo.ImageRepo
import com.google.gson.Gson
import com.example.dermaai_android_140.myClasses.Image


/*
import javax.inject.Inject
import javax.inject.Singleton

*/
class ImageRepoImpl : ImageRepo {


    override fun sendImage(model : AiModel,url : String) : Prediction? {

        val result = API.callApi(url, "POST", model)

        if (result.isSuccess) {

            val receivedData = result.getOrNull()

            try{
                val predictionObject = Gson().fromJson(receivedData, Prediction::class.java)
                return predictionObject

            }
            catch (e: Exception)
            {

            }

        } else if (result.isFailure) {
            result.exceptionOrNull()?.message
        }

        return null
    }


    override fun resizeImage(url : String, base64 : String) : Image? {

        val image = Image(base64)

        val result = API.callApi(url, "POST", image)

        if (result.isSuccess) {

            val receivedData = result.getOrNull()

            try{
                val resizedImage = Gson().fromJson(receivedData, Image::class.java)
                return resizedImage

            }
            catch (e: Exception)
            {
                println(e.message)
            }

        } else if (result.isFailure) {
            result.exceptionOrNull()?.message
        }
        
        return null
    }

}
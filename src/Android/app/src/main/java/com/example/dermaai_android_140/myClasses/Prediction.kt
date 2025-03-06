package com.example.dermaai_android_140.myClasses

class Prediction(
    val trainer_string: String,
    val model_id: Int,
    val prediction: Map<String, Int>
)
{
    fun getPredictionMap() : Map<String, Int>
    {
        return prediction
    }
    
}

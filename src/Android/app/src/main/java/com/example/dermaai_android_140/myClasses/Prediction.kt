package com.example.dermaai_android_140.myClasses

data class Prediction(
    val trainer_string: String,
    val model_id: Int,
    val prediction: Map<String, Double>
) {
    fun getPredictionMap() : Map<String, Double>
    {
        return prediction
    }
}

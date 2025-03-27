package com.example.dermaai_android_140.myClasses

data class Prediction(
    val trainer_string: String,         // Identifiziert den Trainer
    val model_id: Int,                  // Identifiziert das Modell
    val prediction: Map<String, Double>    // Dynamische Map, die die Diagnose und die Wahrscheinlichkeit dieser enthält
) {
    fun getPredictionMap() : Map<String, Double>
    {
        return prediction
    }
}

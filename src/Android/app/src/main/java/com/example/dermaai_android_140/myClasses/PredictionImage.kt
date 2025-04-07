package com.example.dermaai_android_140.myClasses

data class PredictionImage (val email : String, val password : String, val image : String, val prediction : Map<String, Double>)


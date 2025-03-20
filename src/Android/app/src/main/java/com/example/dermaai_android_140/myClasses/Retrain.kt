package com.example.dermaai_android_140.myClasses

data class Retrain(
    val email: String,
    val password: String,
    val trainer_string: String,
    val model_int: Int,
    val num_epochs: Int,
    val reshape_size: Int
)

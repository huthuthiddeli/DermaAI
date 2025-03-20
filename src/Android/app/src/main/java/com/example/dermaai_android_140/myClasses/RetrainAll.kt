package com.example.dermaai_android_140.myClasses

data class RetrainAll(
    val email: String,
    val password: String,
    val num_epochs: Int,
    val reshape_size: Int
)
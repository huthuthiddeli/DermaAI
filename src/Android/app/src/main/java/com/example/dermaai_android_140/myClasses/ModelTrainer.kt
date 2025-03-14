package com.example.dermaai_android_140.myClasses

class ModelTrainer(
    var ModelTrainerPyTorch: List<String>,
    var ModelTrainerSKLearn: List<String>,
    var ModelTrainerTensorFlow: List<String>)
{
    fun getPyTorch() : List<String>
    {
        return ModelTrainerPyTorch
    }
    fun getSKLearn() : List<String>
    {
        return ModelTrainerSKLearn
    }
    fun getTensorFlow() : List<String>
    {
        return ModelTrainerSKLearn
    }
}
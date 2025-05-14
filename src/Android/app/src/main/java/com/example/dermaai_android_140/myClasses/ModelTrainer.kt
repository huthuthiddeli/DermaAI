package com.example.dermaai_android_140.myClasses

class ModelTrainer(
    var modelTrainerPyTorch: List<String>,
    var modelTrainerSKLearn: List<String>,
    var modelTrainerTensorFlow: List<String>)
{
    fun getPyTorch() : List<String>
    {
        return modelTrainerPyTorch
    }
    fun getSKLearn() : List<String>
    {
        return modelTrainerSKLearn
    }
    fun getTensorFlow() : List<String>
    {
        return modelTrainerSKLearn
    }
}
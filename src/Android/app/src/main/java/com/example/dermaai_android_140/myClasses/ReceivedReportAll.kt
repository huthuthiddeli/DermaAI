package com.example.dermaai_android_140.myClasses

data class ReceivedReportAll(
    val reports: Reports,
    val errors: ModelErrors
) {
    data class Reports(
        val ModelTrainerPyTorch: Map<String, ClassificationReport>,
        val ModelTrainerSKLearn: Map<String, ClassificationReport>,
        val ModelTrainerTensorFlow: Map<String, ClassificationReport>
    )

    data class ClassificationReport(
        val precision: Map<String, Double>,
        val recall: Map<String, Double>,
        val f1Score: Map<String, Double>,
        val support: Map<String, Int>,
        val accuracy: Double,
        val macroAvg: Metrics,
        val weightedAvg: Metrics
    ) {
        data class Metrics(
            val precision: Double,
            val recall: Double,
            val f1Score: Double
        )
    }

    data class ModelErrors(
        val ModelTrainerPyTorch: List<String>,
        val ModelTrainerSKLearn: List<String>,
        val ModelTrainerTensorFlow: List<String>
    )
}
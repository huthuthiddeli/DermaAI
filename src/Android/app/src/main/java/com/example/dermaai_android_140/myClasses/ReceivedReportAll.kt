package com.example.dermaai_android_140.myClasses

data class ReceivedReportAll(
    val reports: Map<String, Map<String, String>>,
    val errors: Map<String, List<String>>
)
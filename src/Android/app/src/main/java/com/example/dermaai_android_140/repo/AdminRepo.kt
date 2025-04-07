package com.example.dermaai_android_140.repo

import com.example.dermaai_android_140.myClasses.Report
import com.example.dermaai_android_140.myClasses.ReportAll
import com.example.dermaai_android_140.myClasses.Retrain
import com.example.dermaai_android_140.myClasses.RetrainAll

interface AdminRepo {

    fun retrainModel(model : Retrain?, url : String) : Result<String?>

    fun retrainAllModel(model : RetrainAll, url : String) : Result<String?>

    fun getOneReport(model : Report, url : String) : Result<String?>

    fun getAllReports(model : ReportAll, url : String) : Result<String?>
    
}
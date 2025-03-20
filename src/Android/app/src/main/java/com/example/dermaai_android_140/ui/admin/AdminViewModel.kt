package com.example.dermaai_android_140.ui.admin

import androidx.lifecycle.ViewModel
import com.example.dermaai_android_140.repoimpl.AdminRepoImpl
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import org.koin.java.KoinJavaComponent

class AdminViewModel : ViewModel() {

    private val adminRepo: AdminRepoImpl by KoinJavaComponent.inject(AdminRepoImpl::class.java)

    fun retrainAll(url : String){

    }


    fun retrain(url : String){

    }


}
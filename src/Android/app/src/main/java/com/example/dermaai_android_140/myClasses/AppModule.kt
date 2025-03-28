package com.example.dermaai_android_140.myClasses

import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import com.example.dermaai_android_140.repoimpl.ModelRepoImpl
import com.example.dermaai_android_140.repoimpl.AdminRepoImpl
import com.example.dermaai_android_140.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single { LoginRepoImpl() }
    single { ImageRepoImpl() }
    single { UserRepoImpl(context = get()) }
    single { ModelRepoImpl() }
    single { AdminRepoImpl() }


    viewModel { SettingsViewModel() }
    

}


package com.example.dermaai_android_140.myClasses

import com.example.dermaai_android_140.repo.ImageRepo
import com.example.dermaai_android_140.repo.LoginRepo
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import com.example.dermaai_android_140.ui.accountinfo.AccountinfoViewModel
import com.example.dermaai_android_140.ui.photo.PhotoViewModel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<LoginRepo> { LoginRepoImpl() }

    single<ImageRepo> { ImageRepoImpl() }

    viewModel{ AccountinfoViewModel(get()) }

    viewModel { PhotoViewModel(get()) }


}

package com.example.dermaai_android_140.myClasses

import com.example.dermaai_android_140.repo.ImageRepo
import com.example.dermaai_android_140.repo.LoginRepo
import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import com.example.dermaai_android_140.ui.accountinfo.AccountinfoViewModel
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.StructureKind.OBJECT
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named

import org.koin.dsl.module


val appModule = module {


    single { LoginRepoImpl() }
    single { ImageRepoImpl() }

    /*
    single<Repo>(named("login")) { LoginRepoImpl() }
    single<Repo>(named("image")) { ImageRepoImpl() }
    */

}



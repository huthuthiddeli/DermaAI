package com.example.dermaai_android_140.myClasses

import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import com.example.dermaai_android_140.repoimpl.UserRepoImpl
import org.koin.dsl.module


val appModule = module {


    single { LoginRepoImpl() }
    single { ImageRepoImpl() }
    single { UserRepoImpl(context = get()) }


}



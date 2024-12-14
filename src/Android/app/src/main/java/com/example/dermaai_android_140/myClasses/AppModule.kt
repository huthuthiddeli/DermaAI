package com.example.dermaai_android_140.myClasses

import com.example.dermaai_android_140.repoimpl.ImageRepoImpl
import com.example.dermaai_android_140.repoimpl.LoginRepoImpl
import org.koin.dsl.module


val appModule = module {


    single { LoginRepoImpl() }
    single { ImageRepoImpl() }

    /*
    single<Repo>(named("login")) { LoginRepoImpl() }
    single<Repo>(named("image")) { ImageRepoImpl() }
    */

}



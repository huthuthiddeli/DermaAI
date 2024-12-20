import com.sun.tools.javac.resources.compiler
import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.kotlin.dsl.support.kotlinCompilerOptions

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.dermaai_android_140"
    compileSdkVersion(rootProject.extra["compileSdkVersionExtra"] as Int)

    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
/*
    compileOptions{
        kotlinCompilerOptions()
    }*/

    defaultConfig {
        applicationId = "com.example.dermaai_android_140"
        targetSdkVersion(rootProject.extra["compileSdkVersionExtra"] as Int)
        minSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }


    // Performance / Shrinking
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
                    signingConfig = signingConfigs.getByName("debug")
            multiDexEnabled = true
            matchingFallbacks += listOf()// Consider using a release signing config

        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            isDebuggable = false
        }
    }

    compileOptions {
        sourceCompatibility = VERSION_1_8
        targetCompatibility = VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    flavorDimensions += listOf()
    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
    buildToolsVersion = "35.0.0"
    ndkVersion = "28.0.12674087 rc2"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.filament.android)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.totp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.googleauth)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.coil)
    implementation(libs.play.services.cronet)
    implementation(libs.org.jetbrains.kotlin.plugin.compose.gradle.plugin)
    implementation(libs.org.jetbrains.kotlin.android.gradle.plugin)
    implementation(libs.com.google.devtools.ksp.gradle.plugin)
    implementation(libs.androidx.room.compiler)
    implementation(libs.support.annotations)
    implementation(libs.androidx.annotation)
    implementation(libs.kotlin.ksp)
    implementation(libs.com.android.legacy.kapt.gradle.plugin)
    implementation(libs.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.viewmodel)
}
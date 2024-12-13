plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.example.dermaai_android_140"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dermaai_android_140"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionNameSuffix = rootProject.extra["sourceCompatibility"] as String
        applicationIdSuffix = rootProject.extra["sourceCompatibility"] as String
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packagingOptions {
        resources {
            excludes += setOf("META-INF/DEPENDENCIES", "META-INF/NOTICE", "META-INF/LICENSE")
        }
    }

    compileOptions {
        sourceCompatibility = rootProject.extra["sourceCompatibility"] as String
        targetCompatibility = rootProject.extra["sourceCompatibility"] as String
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    flavorDimensions += listOf()
    ndkVersion = "28.0.12674087 rc2"
    buildToolsVersion = "36.0.0 rc1"
}

dependencies {

    implementation(libs.androidx.appcompat)
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
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)
    implementation(libs.google.material)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.androidx.viewmodel)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.ui)
    implementation(libs.googleauth)
    implementation(libs.totp)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.support.annotations)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.koin.android)
    "baselineProfile"(project(":baselineprofile"))
}
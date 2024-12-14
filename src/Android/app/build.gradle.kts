import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.dermaai_android_140"
    compileSdk = 35

    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
    }

    defaultConfig {
        applicationId = "com.example.dermaai_android_140"
        targetSdk = 35
        minSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.viewmodel)
}
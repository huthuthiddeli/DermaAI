
import org.gradle.api.JavaVersion


plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")

    //alias(libs.plugins.compose.compiler)
    //id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21"
    //id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.example.dermaai_android_140"
    compileSdkVersion(rootProject.extra["compileSdkVersionExtra"] as Int)

    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
    }

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
            //multiDexEnabled = true
            //matchingFallbacks += listOf()// Consider using a release signing config

        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            isDebuggable = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {

        compose = true
        viewBinding = true
    }


    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    flavorDimensions += listOf()
    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
}

dependencies {
    /*
    implementation("com.example:some-library:1.0.0") {
        exclude(group = "com.intellij", module = "annotations")
    }*/

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
    implementation(libs.androidx.activity.ktx)
    implementation(libs.totp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.googleauth)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.coil)
    implementation(libs.play.services.cronet)
    implementation(libs.androidx.runtime)
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.room.compiler)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))


    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.viewmodel)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    /*
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
*/
}
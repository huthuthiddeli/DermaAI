import org.gradle.api.JavaVersion.VERSION_11

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.dermaai_android_140"
    compileSdk = 35

    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("mozilla/public-suffix-list.txt")
    }

    buildFeatures {
        prefab = true
    }
    defaultConfig {
        applicationId = "com.example.dermaai_android_140"
        targetSdk = 35
        minSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    android {
        buildFeatures {
            compose = true
        }
    }
    // Performance / Shrinking
    buildTypes {
        release {
            // entfernt ungenutzten Code
            isMinifyEnabled = true
            // entfernt nicht verwendete Ressourcen
            isShrinkResources = true
            // Lädt die Konfiguration aus der Datei
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt")
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = VERSION_11
        targetCompatibility = VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}
dependencies {
    implementation(libs.firebase.perf)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.exifinterface)
    implementation(libs.firebase.database.ktx)
    implementation(libs.google.material)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.dexmaker.mockito)
    androidTestImplementation(libs.androidx.espresso.contrib)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.material3)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.gson)
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
    implementation(libs.androidx.exifinterface)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.viewmodel)

}
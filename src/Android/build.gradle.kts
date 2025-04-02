
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.firebase.firebase.perf) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}


buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.google.services.v442) {
            exclude(group = "com.google.guava", module = "listenablefuture")
        }
    }
}

configurations.all {
    resolutionStrategy {
        force("com.google.firebase:firebase-common:24.0.0")
        force("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict")
    }
}


val compileSdkVersion = 35
val compileSdkVersionExtra by extra(compileSdkVersion)


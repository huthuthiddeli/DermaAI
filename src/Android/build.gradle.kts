
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.compose.compiler) apply false
    //id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
}




val compileSdkVersion = 35
val compileSdkVersionExtra by extra(compileSdkVersion)


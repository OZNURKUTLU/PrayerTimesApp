// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val hilt_version = "2.48" // En son Hilt sürümünü kontrol edin
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0") // Gradle sürümünüzü kontrol edin
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0") // Kotlin sürümünüzü kontrol edin
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.dagger.hilt.android").version("2.48").apply(false)
}

buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(platform("com.google.firebase:firebase-bom:32.8.1"))
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.gms.google-services") version "4.3.15" apply false
    alias(libs.plugins.androidApplication) apply false
}

dependencyLocking {
    lockAllConfigurations()
}

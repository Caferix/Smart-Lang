import org.gradle.kotlin.dsl.annotationProcessor
import org.gradle.kotlin.dsl.implementation

plugins {

    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.scu.smartlang"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.scu.smartlang"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        resources {
            excludes += "google/type/color.proto"
            excludes += "google/type/date.proto"
            excludes += "google/type/latlng.proto"
            excludes += "google/type/timeofday.proto"
            excludes += "google/type/calendar_period.proto"
            excludes += "google/type/datetime.proto"
            excludes += "google/type/dayofweek.proto"
            excludes += "google/type/expr.proto"
            excludes += "google/type/fraction.proto"
            excludes += "google/type/money.proto"
            excludes += "google/type/postal_address.proto"
            excludes += "google/type/quaternion.proto"
            excludes += "google/type/time_zone.proto"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    implementation (libs.hilt.android)
    annotationProcessor (libs.hilt.android.compiler)

    implementation (libs.hilt.lifecycle.viewmodel)
    annotationProcessor (libs.hilt.compiler)

    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.auth) {
        exclude(group = "com.google.api.grpc")
    }

    implementation(libs.firebase.firestore) {
        exclude(group = "com.google.cloud")
        exclude(group = "com.google.api.grpc")
    }

    implementation(libs.firebase.storage)
    implementation(libs.firebase.analytics)
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "com.google.android.material") {
                useVersion(libs.versions.material.get())
            }
        }
    }
}
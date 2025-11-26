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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    // Lottie Animasyon Kütüphanesi
    implementation("com.airbnb.android:lottie:6.1.0") // En güncel sürümü kontrol edebilirsiniz


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
    annotationProcessor (libs.hilt.compiler)

    implementation(libs.rxjava3)
    implementation(libs.rxandroid)

    //BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    //Firebase

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.analytics)

    implementation(libs.play.services.auth)

    implementation("com.google.code.gson:gson:2.13.2")

    // work manager
    implementation("androidx.work:work-runtime:2.9.0")

}

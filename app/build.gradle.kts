import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("kotlin-android")
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.gms.google.services)
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.cimon_chilimonitoring"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cimon_chilimonitoring"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //load the values from .properties file
        val keystoreFile = project.rootProject.file("local.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        //return empty key in case something goes wrong
        val geminiApiKey = properties.getProperty("GEMINI_API_KEY") ?: ""
        buildConfigField(
            type = "String",
            name = "GEMINI_API_KEY",
            value = geminiApiKey
        )

        val cimonApikey = properties.getProperty("CIMON_API_KEY") ?: ""
        buildConfigField(
            type = "String",
            name = "CIMON_API_KEY",
            value = cimonApikey
        )
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        mlModelBinding = true
        buildConfig = true
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
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // image manipulation
    implementation (libs.glide)

    // viewmodel
    implementation (libs.androidx.activity.ktx)
    implementation (libs.androidx.lifecycle.viewmodel.ktx)

    // uCrop
    implementation (libs.ucrop)
    implementation (libs.circleimageview)

    // room
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // coroutine support
    implementation (libs.kotlinx.coroutines.android)

    // retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    // error handling
    implementation(libs.logging.interceptor)

    // TF Lite
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.play.services.tflite.support)
    implementation(libs.play.services.tflite.gpu)
    implementation(libs.tensorflow.lite.task.vision.play.services)
    implementation(libs.tensorflow.lite.gpu)

    // chatbot
    implementation(libs.generativeai)

    // data store
    implementation(libs.androidx.datastore.preferences)

    // firebase related
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // splash screen animation
    implementation (libs.lottie)
}
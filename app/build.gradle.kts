plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
//    kotlin("jvm") version "1.9.0"
//    id("io.ktor.plugin") version "2.3.3"
}

android {
    namespace = "com.example.videoplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.videoplayer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation (libs.ui)
    implementation(libs.material3)
    implementation (libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.vision.internal.vkp)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer.v141)
    //Dagger - Hilt
    implementation(libs.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.android)
    implementation (libs.osmdroid.android)
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.gson)
    implementation(libs.coil)

//    implementation (libs.here.sdk.android)
//    implementation (libs.osmdroid.android.v6111)
//    implementation (libs.okhttp.v500alpha2)
//    implementation(libs.ktor.server.netty) // Ktor Netty server engine
//    implementation(libs.ktor.server.core)  // Ktor core server
//    implementation(libs.ktor.server.html.builder) // For generating HTML using Kotlin DSL
//    implementation(libs.ktor.server.content.negotiation) // Content negotiation for JSON
//    implementation(libs.ktor.serialization.jackson) // Jackson for JSON serialization
//    implementation(libs.ktor.client.cio) // HTTP client engine for making API requests
//    implementation(libs.kotlinx.serialization.json) // Kotlinx Serialization for handling JSON
//    implementation(libs.ktor.client.serialization) // Client JSON serialization
//    implementation(libs.json) // JSON parsing
}


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt)
    id("org.jetbrains.kotlin.kapt")                    // <-- use kapt instead of ksp
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.otic.learn"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.otic.learn"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // ---- Firebase & Crashlytics ----
    implementation(libs.firebase.storage)
    implementation(libs.firebase.crashlytics)

    // Desugaring (for java.time etc.)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")

    // ---- Compose & AndroidX core ----
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // ---- Firebase (BOM + KTX) ----
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation(libs.firebase.database)

    // Coroutines + Play Services
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // ---- Testing ----
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ---- Hilt (Google, via kapt) ----
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)                        // <-- was ksp before
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // ---- Room (via kapt) ----
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)                       // <-- was ksp before
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-paging:2.6.1")

    // ---- Retrofit / OkHttp ----
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.gson)
    implementation(libs.squareup.okhttp3.logging)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // ---- Lifecycle ViewModel ----
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // ---- Images (Coil) ----
    implementation("io.coil-kt:coil-compose:2.2.2")

    // ---- Navigation Compose ----
    implementation("androidx.navigation:navigation-compose:2.8.7")

    // ---- System UI controller ----
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.27.0")

    // ---- Image cropper ----
    implementation("com.github.CanHub:Android-Image-Cropper:4.0.0")

    // ---- WorkManager + Hilt-Work (via kapt) ----
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")               // <-- Hilt-Work compiler via kapt

    // ---- Paging ----
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")
    implementation("androidx.paging:paging-compose:3.3.2")

    // ---- Coroutines (pinned) ----
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ---- Logging ----
    implementation("com.jakewharton.timber:timber:5.0.1")

    // ---- Hilt testing ----
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)             // <-- use kapt for tests too
}

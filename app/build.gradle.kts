plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.minkiapps.scanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.minkiapps.scanner"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.10.0")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("androidx.camera:camera-camera2:1.4.0-alpha02")
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha02")
    implementation("androidx.camera:camera-view:1.4.0-alpha02")

    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")

    implementation("com.huawei.hms:ml-computer-vision-ocr-latin-model:3.11.0.301")
    implementation("com.huawei.hms:ml-computer-vision-ocr:3.11.0.301")

    implementation("com.github.minkiapps:mrz-java:mrz-java-0.6")

    implementation("org.iban4j:iban4j:3.2.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
}


android {
    namespace = "ru.surf.learn2invest"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.surf.learn2invest"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true

    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // for firngerprint callback
    implementation(libs.androidx.activity.v160alpha05)
    implementation(libs.androidx.room.runtime)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.logging.interceptor)
    // got using fingerprint
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.core.i18n)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    // for graphics
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
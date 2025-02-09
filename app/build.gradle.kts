plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.tarea_7_gestortareas"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tarea_7_gestortareas"
        minSdk = 24
        targetSdk = 34
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

    implementation(libs.lifecycle.livedata.core)
    implementation(libs.room.common)
    implementation(libs.appcompat)
    implementation(libs.recyclerview)
    implementation(libs.room.runtime)
    implementation(libs.material)
    annotationProcessor(libs.room.compiler)
}
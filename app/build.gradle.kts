plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // Для Room
}

android {
    namespace = "com.example.notemanagerapp" // Укажите ваше имя пакета
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.notemanagerapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileSdk = 34
    buildToolsVersion = "34.0.0" // Укажите версию Build Tools

    defaultConfig {
        applicationId = "com.example.notemanagerapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation (libs.material.v190) // Проверьте последнюю версию
    implementation (libs.androidx.material3) // Проверьте последнюю версию
    implementation (libs.androidx.room.ktx)
    // Jetpack Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.activity.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coil для загрузки изображений
    implementation(libs.coil.compose)
    implementation(libs.coil.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ViewModel для Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}

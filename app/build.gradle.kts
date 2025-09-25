plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization") version  "2.0.21"
}

android {
    namespace = "com.weegley.xchangeclient"
    compileSdk = 34 // можно 35, если обновишь compileSdk в проекте

    defaultConfig {
        applicationId = "com.weegley.xchangeclient"
        minSdk = 24   // Android 7.0
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        debug {
            // Включай при желании
            // applicationIdSuffix = ".debug"
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // С Kotlin 2.x и плагином compose отдельный compilerExtensionVersion не нужен.
    // composeOptions { }

    // Java 17 — рекомендуемо для AGP 8.x/Kotlin 2.x
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- Compose BOM (выравнивает версии артефактов Compose) ---
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // --- Compose ---
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Навигация по Compose
    implementation("androidx.navigation:navigation-compose:2.8.2")

    // Lifecycle / State in Compose
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    // DataStore (Preferences)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Корутины
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // kotlinx-serialization (JSON)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Retrofit + OkHttp (включая WebSocket и логирование)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Если планируешь использовать kotlinx-serialization с Retrofit — конвертер:
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // WebSocket — это модуль внутри okhttp (в 4.12.0 идёт в основном артефакте),
    // отдельный dependency обычно не требуется. Если нужно:
    // implementation("com.squareup.okhttp3:okhttp-ws:4.12.0") // чаще НЕ нужен

    // Для совместимости с материал-иконками/ресурсами (если используешь)
    implementation("androidx.compose.material:material-icons-extended")

    // Тесты (оставил базовые)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.12.0") // <-- для JavaNetCookieJar

// Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0") // <-- ScalarsConverterFactory
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")    // <-- GsonConverterFactory
}

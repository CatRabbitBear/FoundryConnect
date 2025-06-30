// 1) Load .env into a Map<String, String>
val envFile = rootProject.file("app/assets/env")
val envMap: Map<String, String> = if (envFile.exists()) {
    envFile
        .readLines()                                   // List<String>
        .mapNotNull { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
                null
            } else {
                val (key, value) = trimmed.split("=", limit = 2)
                key.trim() to value.trim().trim('"', '\'')
            }
        }
        .toMap()
} else {
    emptyMap()
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {

    namespace = "com.booji.foundryconnect"
    compileSdk = 36

    defaultConfig {

        applicationId = "com.booji.foundryconnect"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "AZURE_PROJECT",
            "\"${envMap["AZURE_PROJECT"] ?: "YOUR_PROJECT"}\""
        )
        buildConfigField(
            "String",
            "AZURE_MODEL",
            "\"${envMap["AZURE_MODEL"] ?: "YOUR_MODEL"}\""
        )
        buildConfigField(
            "String",
            "AZURE_API_KEY",
            "\"${envMap["AZURE_API_KEY"] ?: ""}\""
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
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {

    implementation(libs.dotenv.kotlin)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.converter.gson)
    testImplementation(libs.retrofit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Networking (Retrofit and OkHttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.mockwebserver.v500alpha14)
}
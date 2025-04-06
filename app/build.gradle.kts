plugins {
    id("com.android.application")
}

android {
    namespace = "com.emsi.projetws"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.emsi.projetws"
        minSdk = 24
        targetSdk = 33
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
        // Update Java version to 11 (or 17 if your project supports it)
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Add Material Design library
    implementation("com.google.android.material:material:1.9.0")
    
    // Volley for network requests
    implementation("com.android.volley:volley:1.2.1")
    
    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.8.2")
    implementation(libs.firebase.crashlytics.buildtools)

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
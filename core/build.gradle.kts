plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.kotlinchatapp.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    }
}

dependencies {
    //core-dependencies
    implementation(libs.bundles.core.impls)
    ksp(libs.core.kapt.room.compiler)
    androidTestImplementation(libs.core.androidtestimpl.room.testing)
    api(libs.core.api.lifecycle.livedata)

    //main-dependencies
    implementation(libs.bundles.main.impls)
    kapt(libs.bundles.main.kapts)
    testImplementation(libs.main.testimpl.junit)
    androidTestImplementation(libs.bundles.main.androidtestimpl)
}
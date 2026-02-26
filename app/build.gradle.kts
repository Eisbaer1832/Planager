plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinxSerialization).apply(true)
    id("maven-publish")
    id("com.mikepenz.aboutlibraries.plugin.android") version "13.2.1"
}

android {
    namespace = "com.capputinodevelopment.planager"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.capputinodevelopment.planager"
        minSdk = 26
        targetSdk = 36
        versionCode = 147
        versionName = "1.47"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes.add( "/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("**/dump_syms/**")
        }
    }
}

dependencies {
    implementation("com.google.accompanist:accompanist-drawablepainter:0.37.3")
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.material.icons.extended)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)
    implementation("com.google.protobuf:protobuf-javalite:4.32.1")
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.foundation)
    implementation(libs.material3)
    implementation (libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.ui)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.runtime.saveable)
    implementation(libs.androidx.compose.ui.geometry)
    implementation(libs.androidx.camera.mlkit.vision)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose.core)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation (libs.androidx.compose.material.icons.extended)
    implementation(libs.multiplatform.settings)
    implementation(libs.theme.core)
    implementation(libs.qrose)
    implementation (libs.barcode.scanning)
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.google.accompanist.permissions)
}

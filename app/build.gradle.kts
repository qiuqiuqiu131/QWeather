import java.util.Properties

/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.qiuqiuqiu.weatherPredicate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.qiuqiuqiu.weatherPredicate"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        val tianKey: String = (project.findProperty("TIAN_API_KEY") as? String)
            ?: run {
                val localProps = Properties()
                val localFile = rootProject.file("local.properties")
                if (localFile.exists()) {
                    localProps.load(localFile.inputStream())
                    localProps.getProperty("TIAN_API_KEY", "")
                } else ""
            }
        buildConfigField("String", "TIAN_API_KEY", "\"$tianKey\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    hilt {
        enableAggregatingTask = false
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM 管理版本（保留较新的 BOM）
    implementation(platform("androidx.compose:compose-bom:2025.08.01"))

    // Compose / Activity / Lifecycle
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // Compose debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // Core / AppCompat / Splash
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Lifecycle / ViewModel (compose helpers)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    // Baidu 地图 SDK
    implementation("com.baidu.lbsyun:BaiduMapSDK_Map:7.6.5")
    implementation("com.baidu.lbsyun:BaiduMapSDK_Search:7.6.5.1")
    implementation("com.baidu.lbsyun:BaiduMapSDK_Location:9.6.4")
    implementation("com.baidu.lbsyun:BaiduMapSDK_Util:7.6.4")
    implementation("com.baidu.lbsyun:BaiduMapSDK_Panorama:2.9.0")

    // 本地 jar（和风天气等）
    implementation(fileTree("libs") { include("*.jar") })

    // 网络 / JSON / Retrofit / 加密
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("net.i2p.crypto:eddsa:0.3.0")

    // 图片加载
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("io.coil-kt:coil-compose:2.6.0") // 可同时使用 Coil 和 Glide

    // Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("com.google.dagger:hilt-android:2.57.1")
    kapt("com.google.dagger:hilt-android-compiler:2.57.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // Accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")

    // Play Services (location)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}


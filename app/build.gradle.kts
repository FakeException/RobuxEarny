plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

android {
    namespace = "com.robuxearny.official"
    compileSdk = 34

    dataBinding {
        enable = true
    }

    defaultConfig {
        applicationId = "com.robuxearny.official"
        minSdk = 24
        targetSdk = 34
        versionCode = 32
        versionName = "3.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

configurations { create("natives") }

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-common:2.7.0")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics-ktx:21.6.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.23")
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.scottyab:rootbeer-lib:0.1.0")
    implementation("com.google.guava:guava:33.1.0-android")
    implementation("com.google.firebase:firebase-firestore:24.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-functions-ktx:20.4.0")
    implementation("com.google.firebase:firebase-appcheck-playintegrity:17.1.2")
    implementation("com.google.firebase:firebase-appcheck-debug:17.1.2")
    implementation("com.google.firebase:firebase-inappmessaging-display:20.4.1")
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.core:core-splashscreen:1.1.0-alpha02")
    implementation("com.google.android.ump:user-messaging-platform:2.2.0")
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.appodeal.ads:sdk:3.2.1.0") {
        exclude(group = "com.appodeal.ads.sdk.services", module = "adjust")
        exclude(group = "com.appodeal.ads.sdk.services", module = "appsflyer")
        exclude(group = "com.appodeal.ads.sdk.services", module = "firebase")
        exclude(group = "com.appodeal.ads.sdk.services", module = "facebook_analytics")
        exclude(group = "com.appodeal.ads.sdk.networks", module = "adcolony")
    }
    implementation("com.github.MakeOpinionGmbH:cpx-research-SDK-Android:1.5.9")
    implementation("com.google.firebase:firebase-crashlytics:18.6.3")
    implementation("com.google.firebase:firebase-analytics:21.6.1")
    implementation("com.google.firebase:firebase-perf:20.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.android.volley:volley:1.2.1")
}
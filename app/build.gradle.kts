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
        versionCode = 30
        versionName = "2.99"

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
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-common:2.6.2")
    implementation("androidx.lifecycle:lifecycle-process:2.6.2")
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))
    implementation("com.google.firebase:firebase-analytics-ktx:21.4.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
    implementation("com.google.android.gms:play-services-ads:22.4.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.scottyab:rootbeer-lib:0.1.0")
    implementation("com.google.guava:guava:32.1.3-jre")
    implementation("com.google.firebase:firebase-firestore:24.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.firebase:firebase-auth-ktx:22.2.0")
    implementation("com.google.firebase:firebase-functions-ktx:20.4.0")
    implementation("com.google.firebase:firebase-appcheck-playintegrity:17.1.0")
    implementation("com.google.firebase:firebase-appcheck-debug:17.1.0")
    implementation("com.google.firebase:firebase-inappmessaging-display:20.4.0")
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.core:core-splashscreen:1.1.0-alpha02")
    implementation("com.google.android.ump:user-messaging-platform:2.1.0")
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.firebase:firebase-messaging:23.3.0")
    implementation("com.appodeal.ads:sdk:3.1.3.1") {
        exclude(group = "com.appodeal.ads.sdk.services", module = "adjust")
        exclude(group = "com.appodeal.ads.sdk.services", module = "appsflyer")
        exclude(group = "com.appodeal.ads.sdk.services", module = "firebase")
        exclude(group = "com.appodeal.ads.sdk.services", module = "facebook_analytics")
    }
    implementation("com.github.MakeOpinionGmbH:cpx-research-SDK-Android:1.5.9")
    implementation("com.google.firebase:firebase-crashlytics:18.5.0")
    implementation("com.google.firebase:firebase-analytics:21.4.0")
    implementation("com.google.firebase:firebase-perf:20.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.android.volley:volley:1.2.1")
}
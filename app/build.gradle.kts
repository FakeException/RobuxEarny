plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

android {
    namespace = "com.robuxearny.official"
    compileSdk = 35

    dataBinding {
        enable = true
    }

    defaultConfig {
        applicationId = "com.robuxearny.official"
        minSdk = 24
        targetSdk = 35
        versionCode = 48
        versionName = "3.9-FIX"

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

    implementation(AndroidX.appCompat)
    implementation(Google.android.material)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.lifecycle.common)
    implementation(libs.googleid)
    implementation(AndroidX.credentials)
    implementation(AndroidX.credentials.playServicesAuth)
    implementation(AndroidX.lifecycle.process)
    implementation(platform(Firebase.bom))
    implementation(Firebase.analyticsKtx)
    implementation(Kotlin.stdlib.jdk8)
    implementation(libs.play.services.ads)
    implementation(Google.android.playServices.auth)
    implementation(libs.rootbeer.lib)
    implementation(libs.guava)
    implementation(Firebase.cloudFirestore)
    implementation(Square.okHttp3)
    implementation(libs.gson)
    implementation(Firebase.authenticationKtx)
    implementation(Firebase.cloudFunctionsKtx)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.appcheck.debug)
    implementation(Firebase.inAppMessagingDisplay)
    annotationProcessor(libs.response.type.keeper)
    testImplementation(Testing.junit4)
    implementation(AndroidX.core.splashscreen)
    implementation(libs.user.messaging.platform)
    implementation(libs.user.messaging.platform)
    implementation(Google.android.play.appUpdate)
    implementation(Firebase.cloudMessaging)
    implementation(libs.sdk) {
        exclude(group = "com.appodeal.ads.sdk.services", module = "adjust")
        exclude(group = "com.appodeal.ads.sdk.services", module = "appsflyer")
        exclude(group = "com.appodeal.ads.sdk.services", module = "firebase")
        exclude(group = "com.appodeal.ads.sdk.services", module = "facebook_analytics")
        exclude(group = "com.appodeal.ads.sdk.networks", module = "sentry_analytics")
    }
    implementation(libs.cpx.research.sdk.android)
    implementation(Firebase.crashlytics)
    implementation(Firebase.analytics)
    implementation(Firebase.performanceMonitoring)
    implementation(Android.billingClient)
    implementation(Square.retrofit2)
    implementation(Square.retrofit2.converter.gson)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)
    implementation(libs.volley)
}
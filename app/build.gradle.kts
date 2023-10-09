plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.robuxearny.official"
    compileSdk = 33

    dataBinding {
        enable = true
    }

    defaultConfig {
        applicationId = "com.robuxearny.official"
        minSdk = 24
        targetSdk = 33
        versionCode = 24
        versionName = "2.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        compileSdkPreview = "UpsideDownCake"
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
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
    implementation("com.google.android.gms:play-services-ads:22.4.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.scottyab:rootbeer-lib:0.1.0")
    implementation("com.google.guava:guava:32.1.2-jre")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-functions-ktx")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation("com.google.firebase:firebase-appcheck-debug")
    implementation("com.google.firebase:firebase-inappmessaging-display")
    testImplementation("junit:junit:4.13.2")
    implementation("com.paymentwall.sdk:offerwallsdk:1.0.0")
    implementation("androidx.core:core-splashscreen:1.1.0-alpha02")
    implementation("com.google.android.ump:user-messaging-platform:2.1.0")
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.appodeal.ads:sdk:3.1.3.1")
    implementation("com.github.MakeOpinionGmbH:cpx-research-SDK-Android:1.5.9")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
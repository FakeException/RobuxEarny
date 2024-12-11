
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

val gdxVersion: String by extra("1.13.0")

android {
    namespace = "com.robuxearny.official"
    compileSdk = 35

    dataBinding {
        enable = true
    }

    sourceSets {
        getByName("main") {
            jniLibs {
                srcDirs("libs")
            }
        }
    }

    defaultConfig {
        applicationId = "com.robuxearny.official"
        minSdk = 24
        targetSdk = 35
        versionCode = 63
        versionName = "4.9"

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

val natives: Configuration by configurations.creating

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
    implementation(libs.firebase.auth)
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
    implementation(libs.spinninwheel)
    implementation(libs.gdx)
    implementation(libs.gdx.freetype)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86_64")
    implementation(libs.gdx.backend.android)
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
    implementation(libs.gdx.box2d)
    natives("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86_64")

    implementation(libs.appsprize)
    implementation(libs.glide)
}

tasks.register("copyAndroidNatives") {
    doFirst {
        file("libs/armeabi-v7a").mkdirs()
        file("libs/arm64-v8a").mkdirs()
        file("libs/x86_64").mkdirs()
        file("libs/x86").mkdirs()

        val natives = configurations.getByName("natives") // Access natives configuration
        natives.copy().files.forEach { jar ->
            val outputDir = when {
                jar.name.endsWith("natives-armeabi-v7a.jar") -> file("libs/armeabi-v7a")
                jar.name.endsWith("natives-arm64-v8a.jar") -> file("libs/arm64-v8a")
                jar.name.endsWith("natives-x86_64.jar") -> file("libs/x86_64")
                jar.name.endsWith("natives-x86.jar") -> file("libs/x86")
                else -> null
            }
            if (outputDir != null) {
                copy {
                    from(zipTree(jar))
                    into(outputDir)
                    include("*.so")
                }
            }
        }
    }
}
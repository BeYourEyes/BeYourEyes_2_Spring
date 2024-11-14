import com.android.build.api.dsl.LintOptions

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //Google Firebase plugin
    id("com.google.gms.google-services")

}

android {
    namespace = "com.dna.beyoureyes"
    compileSdk = 34

    viewBinding {
        enable = true
    }

    defaultConfig {
        applicationId = "com.dna.beyoureyes"
        minSdk = 24
        targetSdk = 34
        versionCode = 20
        versionName = "2.2"

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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
    buildToolsVersion = "34.0.0"
}

dependencies {
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Google Firebase
    // 익명 계정을 위한 dependency 추가
    // BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    // google login
    implementation ("com.google.android.gms:play-services-auth:21.2.0")

    //MPAndroidChart
    implementation("com.github.PhilJay:MpAndroidChart:v3.1.0")


    //LocalDate 사용을 위한 백포팅
    implementation("com.jakewharton.threetenabp:threetenabp:1.3.0")


    //openCV
    implementation(project(":opencv2"))

    implementation ("com.google.android.gms:play-services-mlkit-text-recognition-common:19.1.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition-korean:16.0.1")


    implementation ("androidx.camera:camera-core:1.4.0-rc01")
    implementation ("androidx.camera:camera-camera2:1.4.0-rc01")
    implementation ("androidx.camera:camera-lifecycle:1.4.0-rc01")
    implementation ("androidx.camera:camera-video:1.4.0-rc01")
    implementation ("androidx.camera:camera-view:1.4.0-rc01")
    implementation ("androidx.camera:camera-extensions:1.4.0-rc01")

    //
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.tbuonomo:dotsindicator:4.3")
}

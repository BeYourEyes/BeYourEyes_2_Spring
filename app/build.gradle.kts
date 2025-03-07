import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //Google Firebase plugin
    id("com.google.gms.google-services")
    // navigation safeargs, classpath
    id("androidx.navigation.safeargs.kotlin")
}

fun getApiKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
        ?: "\"No Key\"".also { println("in build.gradle: Can't Find $propertyKey") }
}

android {
    namespace = "com.dna.beyoureyes"
    compileSdk = 34

    viewBinding {
        enable = true
    }

    dataBinding {
        enable = true
    }

    defaultConfig {
        applicationId = "com.dna.beyoureyes"
        minSdk = 24
        targetSdk = 34
        versionCode = 22
        versionName = "2.2.2"
        println ("Current defaultConfig versionName: ${versionName}")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "OPEN_API_KEY", getApiKey("OPEN_API_KEY"))
        println(getApiKey("OPEN_API_KEY"))
    }

    buildFeatures {
        buildConfig = true
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

    // Fragment & Navigation
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Google Firebase
    // 익명 계정을 위한 dependency 추가
    // BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-installations:17.1.4") // FID 사용
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    // Glide (이미지 로드 라이브러리)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // google login
    implementation ("com.google.android.gms:play-services-auth:21.2.0")

    //MPAndroidChart
    implementation("com.github.PhilJay:MpAndroidChart:v3.1.0")


    //LocalDate 사용을 위한 백포팅
    implementation("com.jakewharton.threetenabp:threetenabp:1.3.0")

    //Review
    implementation("com.google.android.play:review:2.0.2")


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
    implementation("com.tbuonomo:dotsindicator:5.0")

    //Circle Image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //View Animations
    implementation("com.daimajia.androidanimations:library:2.4@aar")
    // Fragment
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    // MVVM pattern
    implementation ("androidx.core:core-ktx:1.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation ("androidx.activity:activity-ktx:1.7.0")

    implementation("com.airbnb.android:lottie:6.6.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0") // Converter ( JSON -> 객체 매핑 )

    // OkHttp (HTTP 클라이언트)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // 코루틴
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Data Store(액세스 토큰 저장용)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

}
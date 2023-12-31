plugins {
    id("com.android.application")
}

android {
    namespace = "com.victormugo.nsign_media"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.victormugo.nsign_media"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    implementation("com.squareup.okhttp3:logging-interceptor:3.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.afollestad.material-dialogs:core:0.9.6.0")
    implementation("com.afollestad.material-dialogs:commons:0.9.6.0")
    implementation("androidx.databinding:library:3.2.0-alpha11")
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("com.afollestad.material-dialogs:core:0.9.6.0")
    implementation("com.afollestad.material-dialogs:commons:0.9.6.0")
    implementation("com.github.junrar:junrar:7.5.5")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
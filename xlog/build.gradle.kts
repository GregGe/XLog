plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.logger.xlog"
    compileSdk = 35

    defaultConfig {
        minSdk = 14
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = false
        compose = false
        aidl = false
        viewBinding = false
        dataBinding = false
        renderScript = false
        shaders = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}


mavenPublishing {
    signAllPublications()
    publishToMavenCentral(automaticRelease = true)
}

dependencies {
    testImplementation(libs.androidx.junit)
    testImplementation(libs.mockito)
}
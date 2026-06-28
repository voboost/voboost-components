plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ru.voboost.components.demo.shared"
    compileSdk = 34

    defaultConfig {
        minSdk = 28 // Android 9 for automotive compatibility
        targetSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("main") {
            java {
                srcDir("src/main/java")
            }
        }
    }
}

androidComponents {
    beforeVariants { variant ->
        variant.enable = variant.buildType != "debug"
    }
}

// Exclude Kotlin compilation tasks (this is a pure Java project)
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    enabled = false
}

dependencies {
    implementation(project(":"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
}

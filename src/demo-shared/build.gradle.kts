plugins {
    id("com.android.library")
}

android {
    namespace = "ru.voboost.components.demo.shared"
    compileSdk = 34

    defaultConfig {
        minSdk = 28 // Android 9 for automotive compatibility

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

// Exclude Kotlin compilation tasks (this is a pure Java project)
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    enabled = false
}

dependencies {
    // Main voboost-components library
    implementation(project(":")) {
        // Exclude Kotlin stdlib — this module is pure Java
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
    }

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

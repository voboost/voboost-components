plugins {
    id("com.android.application")
    id("io.github.takahirom.roborazzi") version "1.48.0"
}

android {
    namespace = "ru.voboost.components.demo.pixel"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.voboost.components.demo.pixel"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/*.kotlin_module"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
            excludes += "DebugProbesKt.bin"
            excludes += "kotlin/**"
            excludes += "META-INF/com.android.tools/**"
        }
    }

    sourceSets {
        getByName("main") {
            java {
                srcDir("java")
            }
            manifest.srcFile("AndroidManifest.xml")
            // Add assets from main library for font access (BEM structure: fonts in src/main/java)
            assets.srcDir("../../main/java")
        }
        getByName("test") {
            // BEM co-located tests: tests live alongside main code
            java {
                srcDir("java")
            }
            // Reference images for pixel comparison
            resources {
                srcDir("java/ru/voboost/components/demo/pixel/MainActivity.resources")
            }
            // Add assets from main library for font access in tests (BEM structure: fonts in src/main/java)
            assets.srcDir("../../main/java")
        }
    }
}

// Exclude test files from main compilation (BEM co-located structure)
tasks.withType<JavaCompile>().configureEach {
    if (!name.contains("Test") && !name.contains("test")) {
        // Exclude only files in .tests directories
        exclude("**/*.tests/**")
        // Exclude test files that start with Test (not utility classes like FontTest)
        exclude("**/Test*.java")
    }
}

// Exclude Kotlin compilation tasks (this is a pure Java project)
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    enabled = false
}

// Exclude Compose debug dependencies from root project
configurations {
    debugImplementation {
        exclude(group = "androidx.compose.ui", module = "ui-tooling")
        exclude(group = "androidx.compose.ui", module = "ui-test-manifest")
    }
}

dependencies {
    // Main voboost-components library
    implementation(project(":")) {
        // Exclude Kotlin stdlib — this demo is pure Java
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
    }

    // Android Core
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.annotation:annotation:1.7.1")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.github.takahirom.roborazzi:roborazzi:1.48.0")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:1.48.0")
    testImplementation("org.robolectric:robolectric:4.14.1")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

roborazzi {
    outputDir = file("java/ru/voboost/components/demo/pixel/MainActivity.screenshots")
}

tasks.register("testDemoPixel") {
    group = "demo"
    description = "Run all tests for pixel demo application"
    dependsOn("testDebugUnitTest")
}

tasks.register("testPixelVisualSave") {
    group = null // internal — use recordDemos from root
    description = "Record and save pixel demo visual test screenshots"
    dependsOn("recordRoborazziDebug")
}

tasks.register("testPixelVisualCompare") {
    group = null // internal — use verifyDemos from root
    description = "Compare pixel demo visual test screenshots"
    dependsOn("compareRoborazziDebug")
}

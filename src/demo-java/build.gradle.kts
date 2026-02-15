plugins {
    id("com.android.application")
    id("io.github.takahirom.roborazzi") version "1.48.0"
}

android {
    namespace = "ru.voboost.components.demo.java"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.voboost.components.demo.java"
        minSdk = 28 // Android 9 for automotive compatibility
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
            signingConfig = signingConfigs.getByName("debug")
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
        }
        getByName("test") {
            java {
                srcDir("src/test/java")
                srcDir("java/ru/voboost/components/demo/java/MainActivity.tests")
            }
        }
    }
}

// Exclude test files from main compilation (BEM co-located structure)
tasks.withType<JavaCompile>().configureEach {
    if (!name.contains("Test") && !name.contains("test")) {
        exclude("**/MainActivity.tests/**")
        exclude("**/*Test*.java")
        exclude("**/*.tests/**")
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

    // Demo shared module
    implementation(project(":demo-shared")) {
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
    // Compose dependencies for Roborazzi tests only
    testImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    testImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("androidx.compose.ui:ui-test-manifest")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// Configure Roborazzi for automotive resolution screenshots
roborazzi {
    outputDir = file("screenshots")
}

// Demo testing tasks
tasks.register("testDemoJava") {
    group = "demo"
    description = "Run all tests for Java demo application"
    dependsOn("testDebugUnitTest")
}

tasks.register("testDemoJavaVisual") {
    group = "demo"
    description = "Run only visual tests for Java demo application"
    dependsOn("verifyRoborazziDebug")
}

tasks.register("testDemoJavaVisualSave") {
    group = "demo"
    description = "Run visual tests and save screenshots for Java demo application"
    dependsOn("recordRoborazziDebug")
}

// Legacy screenshot tasks (for backward compatibility)
tasks.register("recordDemoScreenshots") {
    group = "demo"
    description = "Record screenshots for Java demo application"
    dependsOn("recordRoborazziDebug")
}

tasks.register("verifyDemoScreenshots") {
    group = "demo"
    description = "Verify screenshots for Java demo application"
    dependsOn("verifyRoborazziDebug")
}

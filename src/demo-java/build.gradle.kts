plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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

dependencies {
    // Main voboost-components library
    implementation(project(":"))

    // Android Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    // Activity and Fragment KTX
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.github.takahirom.roborazzi:roborazzi:1.48.0")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:1.48.0")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")

    // Compose testing dependencies for Roborazzi
    testImplementation("androidx.compose.ui:ui-test-junit4:1.5.8")
    testImplementation("androidx.compose.ui:ui-test-manifest:1.5.8")

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

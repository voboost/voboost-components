plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("io.github.takahirom.roborazzi") version "1.48.0"
}

android {
    namespace = "ru.voboost.components.demo.kotlin"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.voboost.components.demo.kotlin"
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
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
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
                srcDir("java/ru/voboost/components/demo/kotlin/MainActivity.tests")
            }
        }
    }
}

// Exclude test files from main compilation (BEM co-located structure)
tasks.withType<JavaCompile>().configureEach {
    if (!name.contains("Test") && !name.contains("test")) {
        exclude("**/MainActivity.tests/**")
        exclude("**/*Test*.kt")
        exclude("**/*.tests/**")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    if (!name.contains("Test") && !name.contains("test")) {
        exclude("**/MainActivity.tests/**")
        exclude("**/*Test*.kt")
        exclude("**/*.tests/**")
    }
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
    implementation(project(":"))

    // Demo shared module
    implementation(project(":demo-shared"))

    // Android Core
    implementation("androidx.core:core-ktx:1.12.0")

    // These are required for Kotlin Compose wrappers
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

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

// Configure Roborazzi for automotive resolution screenshots
roborazzi {
    outputDir = file("screenshots")
}

// Demo screenshot tasks
tasks.register("recordDemoScreenshots") {
    group = "demo"
    description = "Record screenshots for Kotlin demo application"
    dependsOn("recordRoborazziDebug")
}

tasks.register("verifyDemoScreenshots") {
    group = "demo"
    description = "Verify screenshots for Kotlin demo application"
    dependsOn("verifyRoborazziDebug")
}

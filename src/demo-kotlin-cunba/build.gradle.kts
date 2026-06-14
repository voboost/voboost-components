plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "ru.voboost.components.demo.kotlin.cunba"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.voboost.components.demo.kotlin.cunba"
        minSdk = 28 // Android 9 for automotive compatibility
        targetSdk = 30
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

    kotlinOptions {
        jvmTarget = "17"
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
            res.srcDir("res")
            manifest.srcFile("AndroidManifest.xml")
        }
        getByName("test") {
            java {
                srcDir("src/test/java")
                srcDir("java/ru/voboost/components/demo/kotlin/cunba/MainActivity.tests")
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
    implementation(project(":"))
    implementation(project(":demo-shared"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}

// Configure Roborazzi for automotive resolution screenshots
roborazzi {
    outputDir = file("screenshots")
}

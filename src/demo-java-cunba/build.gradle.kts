plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "ru.voboost.components.demo.java.cunba"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.voboost.components.demo.java.cunba"
        minSdk = 28
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
            isDebuggable = (project.findProperty("debuggable")?.toString() == "true")
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    applicationVariants.all {
        outputs.forEach { output ->
            val apk = output as com.android.build.gradle.api.ApkVariantOutput
            apk.outputFileName = apk.outputFileName.replace("-release", "")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
            res.srcDirs("res")
        }
        getByName("test") {
            java {
                srcDir("src/test/java")
                srcDir("java/ru/voboost/components/demo/java/cunba/MainActivity.tests")
            }
        }
    }
}

androidComponents {
    beforeVariants { variant ->
        variant.enable = variant.buildType != "debug"
    }
}

tasks.withType<JavaCompile>().configureEach {
    if (!name.contains("Test") && !name.contains("test")) {
        exclude("**/MainActivity.tests/**")
        exclude("**/*Test*.java")
        exclude("**/*.tests/**")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    enabled = false
}

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

roborazzi {
    outputDir = file("screenshots")
}

tasks.register("testDemoCunba") {
    group = "demo"
    description = "Run all tests for CunBA 3 demo application"
    dependsOn("testReleaseUnitTest")
}

tasks.register("testDemoCunbaVisual") {
    group = "demo"
    description = "Run only visual tests for CunBA 3 demo application"
    dependsOn("verifyRoborazziRelease")
}

tasks.register("testDemoCunbaVisualSave") {
    group = "demo"
    description = "Run visual tests and save screenshots for CunBA 3 demo application"
    dependsOn("recordRoborazziRelease")
}

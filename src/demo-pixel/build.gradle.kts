plugins {
    id("com.android.application")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "ru.voboost.components.demo.pixel"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.voboost.components.demo.pixel"
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
            // Add assets from main library for font access (BEM structure: fonts in src/main/java)
            // ONLY include the font directory — not the entire source tree
            assets.srcDir("../../main/java/ru/voboost/components/font")
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
            // ONLY include the font directory — not the entire source tree
            assets.srcDir("../../main/java/ru/voboost/components/font")
        }
    }
}

androidComponents {
    beforeVariants { variant ->
        variant.enable = variant.buildType != "debug"
    }
}

// Exclude test files from main compilation (BEM co-located structure)
tasks.withType<JavaCompile>().configureEach {
    if (!name.contains("Test") && !name.contains("test")) {
        // Exclude only files in .tests directories
        exclude("**/*.tests/**")
        // Exclude test files that start with Test (not utility classes like FontTest)
        exclude("**/Test*.java")
    } else {
        doFirst {
            println("Test sources for task $name:\n" + source.files.joinToString("\n"))
        }
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
    implementation(project(":"))
    implementation(project(":demo-shared"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
}

// Robolectric visual tests read/write files inside src/.../MainActivity.screenshots/
// which Gradle does not track as task inputs/outputs. Without this override the
// test task is reported UP-TO-DATE and new steps (e.g. section-info-short step 02)
// never produce *_2actual.png / *.txt artefacts.
tasks.withType<Test>().configureEach {
    outputs.upToDateWhen { false }
}

roborazzi {
    outputDir = file("java/ru/voboost/components/demo/pixel/MainActivity.screenshots")
}

// Custom clear task that ONLY deletes generated files (_2actual, _3diff, _4magenta)
// NEVER deletes _1original.png reference images from real vehicle hardware
tasks.register("clearRoborazziSafe") {
    group = "verification"
    description = "Clear only generated roborazzi files, preserving _1original.png references"
    doFirst {
        val screenshotsDir = file("java/ru/voboost/components/demo/pixel/MainActivity.screenshots")
        if (screenshotsDir.exists()) {
            val deleted = screenshotsDir.listFiles { _, name ->
                name.endsWith("_2actual.png") ||
                name.endsWith("_3diff.png") ||
                name.endsWith("_4magenta.png")
            }
            deleted?.forEach { it.delete() }
            val count = deleted?.size ?: 0
            println("Cleared $count generated roborazzi files (_1original.png preserved)")
        }
    }
}

tasks.register("save") {
    group = "demo"
    description = "Generate screenshots and comparison reports"
    dependsOn("testReleaseUnitTest")
}

tasks.register("verify") {
    group = "demo"
    description = "Run tests and validate (runs save first, then checks reports)"
    dependsOn("save")
    doLast {
        val screenshotsDir = file("java/ru/voboost/components/demo/pixel/MainActivity.screenshots")
        val reports = screenshotsDir.listFiles { _, name -> name.endsWith(".txt") } ?: emptyArray()
        val failed = reports.mapNotNull { report ->
            val content = report.readText()
            val matchRegex = """(\d+\.\d+)%\s+match""".toRegex()
            val match = matchRegex.find(content)?.groupValues?.get(1)?.toDoubleOrNull()
            if (match == null || match < 95.0) {
                "${report.name}: ${match?.let { "$it% match" } ?: "no match data"}"
            } else null
        }
        if (failed.isNotEmpty()) {
            throw GradleException("Tests failed:\n  ${failed.joinToString("\n  ")}")
        }
    }
}

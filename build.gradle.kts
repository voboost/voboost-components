plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    alias(libs.plugins.roborazzi)
    id("com.diffplug.spotless") version "6.25.0"
    id("checkstyle")
}

// Apply Voboost code style configuration
// Note: Code style is enforced via ktlint and Spotless plugins configured below
// The voboost-codestyle repository provides shared .editorconfig (symlinked at project root)

// Resolve dependency conflicts for Checkstyle
configurations.checkstyle {
    resolutionStrategy {
        force("com.google.guava:guava:33.0.0-android")
        eachDependency {
            if (requested.group == "com.google.collections" &&
                requested.name == "google-collections"
            ) {
                useTarget("com.google.guava:guava:33.0.0-android")
                because("google-collections is replaced by guava")
            }
        }
    }
}

android {
    namespace = "ru.voboost.components"
    compileSdk = 34

    defaultConfig {
        minSdk = 28 // Android 9 for automotive compatibility
        targetSdk = 30 // Android 11 - primary platform for users

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Exclude non-font files from assets (Font.java is co-located with .ttf files)
    androidResources {
        ignoreAssetsPattern = "!*.java:!*.kt:!*.md"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Exclude all non-font files from src/main/java
            excludes += "**/*.java"
            excludes += "**/*.kt"
            excludes += "**/*.md"
            excludes += "**/*.screenshots/**/*.png"
            excludes += "**/*.jpg"
            excludes += "**/*.jpeg"
            excludes += "**/*.webp"
            excludes += "**/*.gif"
            excludes += "**/*.svg"
            excludes += "**/*.test/**"
            excludes += "**/*.tests/**"
            excludes += "**/*.screenshots/**"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    sourceSets {
        getByName("main") {
            java {
                srcDir("src/main/java")
            }
            // BEM structure: fonts co-located with Font class are loaded as assets
            // ONLY include the font directory — not the entire source tree
            assets.srcDir("src/main/java/ru/voboost/components/font")
            // BEM-colocated primitive bitmaps as Java classpath resources
            resources.srcDir("src/main/java")
        }
        getByName("test") {
            java {
                srcDir("src/main/java") // BEM co-located tests
            }
        }
    }

    // IDE integration
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            // Ensure IDE recognizes co-located test files
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }
}

// Exclude test files from main compilation (BEM co-located structure)
// Test files are in src/main/java but should only compile in test source set
tasks.withType<JavaCompile>().configureEach {
    if (!name.contains("Test") && !name.contains("test")) {
        exclude("**/*.test/**")
        exclude("**/*Test*.java")
        exclude("**/*TestUnit.java")
        exclude("**/*TestVisual.java")
        exclude("**/Text.test/**")
        exclude("**/*.test-visual.java")
    }
    // For test compilation, don't exclude .test directories
    // They should be included for testing
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    if (!name.contains("Test") && !name.contains("test")) {
        exclude("**/Radio.test/**")
        exclude("**/Panel.test/**")
        exclude("**/Screen.test/**")
        exclude("**/Text.test/**")
        exclude("**/Buttons.test/**")
        exclude("**/*Test*.kt")
        exclude("**/*.test-unit.kt")
        exclude("**/*.test-visual.kt")
    }
    // Also exclude from test compilation for now
    if (name.contains("test")) {
        exclude("**/Text.test-visual.kt")
    }
}

dependencies {
    // Android Core — minimal runtime dependencies for Java Custom Views
    implementation("androidx.core:core:1.12.0") // core WITHOUT ktx
    implementation("androidx.annotation:annotation:1.7.1") // annotations only
    implementation(
        "androidx.lifecycle:lifecycle-common:2.7.0",
    ) // for DefaultLifecycleObserver in Popup

    // These are only needed by Kotlin Compose wrappers — consumers must provide
    compileOnly("androidx.appcompat:appcompat:1.6.1")
    testImplementation("androidx.appcompat:appcompat:1.6.1")
    compileOnly("com.google.android.material:material:1.11.0")
    compileOnly("androidx.activity:activity-ktx:1.8.2")
    compileOnly("androidx.fragment:fragment-ktx:1.6.2")
    compileOnly("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    compileOnly("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    compileOnly("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Jetpack Compose BOM
    compileOnly(platform(libs.compose.bom))
    compileOnly("androidx.compose.ui:ui")
    compileOnly("androidx.compose.ui:ui-graphics")
    compileOnly("androidx.compose.ui:ui-tooling")
    compileOnly("androidx.compose.ui:ui-tooling-preview")
    compileOnly("androidx.compose.ui:ui-test-manifest")
    compileOnly("androidx.compose.material3:material3")
    compileOnly("androidx.activity:activity-compose:1.8.2")

    // Testing dependencies (only for test source set)
    testImplementation(platform(libs.compose.bom))
    testImplementation("junit:junit:4.13.2") {
        exclude(group = "org.hamcrest", module = "hamcrest-core")
    }
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("io.mockk:mockk-android:1.13.8")
    testImplementation(libs.robolectric) {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15on")
    }
    testImplementation("com.google.truth:truth:1.1.5")

    // Android testing dependencies
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debug dependencies
    compileOnly("androidx.compose.ui:ui-tooling")
    compileOnly("androidx.compose.ui:ui-test-manifest")
}

// Note: Code style tasks (formatKotlinCode, checkKotlinStyle, formatJavaCode, etc.)
// are now provided by ../voboost-codestyle/codestyle.gradle (commented out as file doesn't exist)

// Demo Applications Integration
// These tasks integrate demo applications into the development workflow
// while ensuring they are excluded from library distribution

tasks.register("buildDemos") {
    group = "demo"
    description = "Build all demo applications"
    dependsOn(
        ":demo-java:assembleDebug",
        ":demo-kotlin:assembleDebug",
        ":demo-compose:assembleDebug",
        ":demo-pixel:assembleDebug",
    )
}

tasks.register("installDemos") {
    group = "demo"
    description = "Install all demo applications"
    dependsOn(
        ":demo-java:installDebug",
        ":demo-kotlin:installDebug",
        ":demo-compose:installDebug",
        ":demo-pixel:installDebug",
    )
}

tasks.register("testDemos") {
    group = "demo"
    description = "Run tests for all demo applications"
    dependsOn(
        ":demo-java:testDebugUnitTest",
        ":demo-kotlin:testDebugUnitTest",
        ":demo-compose:testDebugUnitTest",
        ":demo-pixel:testDebugUnitTest",
    )
}

tasks.register("cleanDemos") {
    group = "demo"
    description = "Clean all demo applications"
    dependsOn(":demo-java:clean", ":demo-kotlin:clean", ":demo-compose:clean", ":demo-pixel:clean")
}

// Individual demo tasks for convenience
tasks.register("buildDemoJava") {
    group = "demo"
    description = "Build Java demo application"
    dependsOn(":demo-java:assembleDebug")
}

tasks.register("buildDemoKotlin") {
    group = "demo"
    description = "Build Kotlin demo application"
    dependsOn(":demo-kotlin:assembleDebug")
}

tasks.register("buildDemoCompose") {
    group = "demo"
    description = "Build Compose demo application"
    dependsOn(":demo-compose:assembleDebug")
}

tasks.register("buildDemoPixel") {
    group = "demo"
    description = "Build Pixel demo application"
    dependsOn(":demo-pixel:assembleDebug")
}

tasks.register("installDemoJava") {
    group = "demo"
    description = "Install Java demo application to connected device"
    dependsOn(":demo-java:installDebug")
}

tasks.register("installDemoKotlin") {
    group = "demo"
    description = "Install Kotlin demo application to connected device"
    dependsOn(":demo-kotlin:installDebug")
}

tasks.register("installDemoCompose") {
    group = "demo"
    description = "Install Compose demo application to connected device"
    dependsOn(":demo-compose:installDebug")
}

tasks.register("installDemoPixel") {
    group = "demo"
    description = "Install Pixel demo application to connected device"
    dependsOn(":demo-pixel:installDebug")
}

// Demo start tasks
tasks.register<Exec>("startDemoJava") {
    group = "demo"
    description = "Start Java demo application on connected device"
    commandLine("adb", "shell", "am", "start", "-n", "ru.voboost.components.demojava/.MainActivity")
    dependsOn(":demo-java:installDebug")
}

tasks.register<Exec>("startDemoKotlin") {
    group = "demo"
    description = "Start Kotlin demo application on connected device"
    commandLine(
        "adb",
        "shell",
        "am",
        "start",
        "-n",
        "ru.voboost.components.demo.kotlin/.MainActivity",
    )
    dependsOn(":demo-kotlin:installDebug")
}

tasks.register<Exec>("startDemoCompose") {
    group = "demo"
    description = "Start Compose demo application on connected device"
    commandLine(
        "adb",
        "shell",
        "am",
        "start",
        "-n",
        "ru.voboost.components.demo.compose/.MainActivity",
    )
    dependsOn(":demo-compose:installDebug")
}

tasks.register<Exec>("startDemoPixel") {
    group = "demo"
    description = "Start Pixel demo application on connected device"
    commandLine(
        "adb",
        "shell",
        "am",
        "start",
        "-n",
        "ru.voboost.components.demo.pixel/.MainActivity",
    )
    dependsOn(":demo-pixel:installDebug")
}

// Demo validation tasks
tasks.register("validateDemos") {
    group = "demo"
    description = "Full demo validation (build + test + screenshots)"
    dependsOn("buildDemos", "testDemos", "verifyDemos")
}

tasks.register("recordDemos") {
    group = "demo"
    description = "Record demo screenshots"
    dependsOn(":demo-compose:recordRoborazziDebug")
}

tasks.register("verifyDemos") {
    group = "demo"
    description = "Verify demo screenshots"
    dependsOn(":demo-compose:verifyRoborazziDebug")
}

// Ensure demos are excluded from library build and distribution
tasks.named("assemble") {
    mustRunAfter("cleanDemos")
}

// CI validation task that includes full demo validation
tasks.register("ciValidate") {
    group = "verification"
    description = "Full validation including demos (for CI)"
    dependsOn("build", "validateDemos")
}

// Custom Gradle tasks for BEM Co-Located Test Structure
// Use proper dependsOn chains instead of recursive Gradle invocation

// Combined test tasks - depend on testDebugUnitTest with proper filtering
tasks.register("testUnit") {
    description = "Run all unit tests (Java and Kotlin)"
    group = "verification"
    dependsOn("testDebugUnitTest")
}

tasks.register("testVisual") {
    description = "Run all visual tests using Roborazzi"
    group = "verification"
    dependsOn("testDebugUnitTest")
}

// Java test tasks
tasks.register("testUnitJava") {
    description = "Run only Java unit tests"
    group = "verification"
    dependsOn("testDebugUnitTest")
}

tasks.register("testVisualJava") {
    description = "Run only Java visual tests using Roborazzi"
    group = "verification"
    dependsOn("testDebugUnitTest")
}

// Kotlin test tasks
tasks.register("testUnitKotlin") {
    description = "Run only Kotlin unit tests (*.test-unit.kt files)"
    group = "verification"
    dependsOn("testDebugUnitTest")
}

tasks.register("testVisualKotlin") {
    description = "Run only Kotlin visual tests using Roborazzi"
    group = "verification"
    dependsOn("testDebugUnitTest")
}

// Java code style tasks are defined in checkstyle.gradle and spotless.gradle

tasks.register("testJava") {
    description = "Run all Java tests (unit and visual)"
    group = "verification"
    dependsOn("testUnitJava", "testVisualJava")
}

tasks.register("testKotlin") {
    description = "Run all Kotlin tests (unit and visual)"
    group = "verification"
    dependsOn("testUnitKotlin", "testVisualKotlin")
}

tasks.register("fix") {
    description = "Auto-fix style violations"
    group = "formatting"
    dependsOn("ktlintFormat", "spotlessApply")
}

tasks.register("validate") {
    description = "Run all checks (tests + style)"
    group = "verification"
    dependsOn("testJava", "testKotlin", "ktlintCheck", "spotlessCheck")
}

tasks.register("record") {
    description = "Record and save visual test screenshots"
    group = "verification"
    dependsOn("copyRoborazziScreenshots")
}

tasks.register<Copy>("copyRoborazziScreenshots") {
    description = "Copy Roborazzi screenshots to BEM structure"
    group = null // internal task

    from("build/intermediates/roborazzi")
    into("src/main/java/ru/voboost/components")
    include("*.png")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    dependsOn("recordRoborazziDebug")
    doNotTrackState("Screenshots are managed externally")
}

// Configure Roborazzi for BEM Co-Located Test Structure
roborazzi {
    // Configure output directory for screenshots
    outputDir = file("src/main/java/ru/voboost/components")
}

// Configure existing Android test tasks to work with BEM Co-Located structure
tasks.withType<Test>().configureEach {
    // Set up test logging for non-custom tasks
    if (!name.startsWith("testUnit") && !name.startsWith("testVisual")) {
        testLogging {
            events("passed", "skipped", "failed", "standardOut", "standardError")
            showStandardStreams = true
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }

        // Ensure tests can find co-located test files
        systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
    }
}

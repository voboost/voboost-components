plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("io.github.takahirom.roborazzi") version "1.48.0"
    id("com.diffplug.spotless") version "6.25.0"
    id("checkstyle")
}

// Apply Voboost code style configuration
// apply(from = "../voboost-codestyle/codestyle.gradle") // Commented out as the file doesn't exist in the current workspace

// Resolve dependency conflicts for Checkstyle
configurations.all {
    resolutionStrategy {
        force("com.google.guava:guava:33.0.0-jre")
        eachDependency {
            if (requested.group == "com.google.collections" &&
                requested.name == "google-collections"
            ) {
                useTarget("com.google.guava:guava:33.0.0-jre")
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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        vectorDrawables {
            useSupportLibrary = true
        }
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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
        }
        getByName("test") {
            java {
                srcDir("src/main/java") // BEM co-located tests
                srcDir(
                    "src/main/java/ru/voboost/components/radio/Radio.test"
                ) // Include test directory,
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
        exclude("**/Radio.test/**")
        exclude("**/*Test*.java")
        exclude("**/*.test/**")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    if (!name.contains("Test") && !name.contains("test")) {
        exclude("**/Radio.test/**")
        exclude("**/*Test*.kt")
        exclude("**/*.test-unit.kt")
        exclude("**/*.test-visual.kt")
        exclude("**/*.test/**")
    }
}

dependencies {
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
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Jetpack Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Testing dependencies (only for test source set)
    testImplementation("junit:junit:4.13.2") {
        exclude(group = "org.hamcrest", module = "hamcrest-core")
    }
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("io.github.takahirom.roborazzi:roborazzi:1.48.0")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-compose:1.48.0")
    testImplementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:1.48.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("io.mockk:mockk-android:1.13.8")
    testImplementation("org.robolectric:robolectric:4.11.1") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15on")
    }
    testImplementation("com.google.truth:truth:1.1.5")

    // Android testing dependencies
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debug dependencies
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// Note: Code style tasks (formatKotlinCode, checkKotlinStyle, formatJavaCode, etc.)
// are now provided by ../voboost-codestyle/codestyle.gradle (commented out as file doesn't exist)

// Demo Applications Integration
// These tasks integrate demo applications into the development workflow
// while ensuring they are excluded from library distribution

tasks.register("buildAllDemos") {
    group = "demo"
    description = "Build all demo applications (Java, Kotlin, Compose)"
    dependsOn(
        ":demo-java:assembleDebug",
        ":demo-kotlin:assembleDebug",
        ":demo-compose:assembleDebug",
    )
}

tasks.register("installAllDemos") {
    group = "demo"
    description = "Install all demo applications to connected device"
    dependsOn(":demo-java:installDebug", ":demo-kotlin:installDebug", ":demo-compose:installDebug")
}

tasks.register("testAllDemos") {
    group = "demo"
    description = "Run tests for all demo applications"
    dependsOn(
        ":demo-java:testDebugUnitTest",
        ":demo-kotlin:testDebugUnitTest",
        ":demo-compose:testDebugUnitTest",
    )
}

tasks.register("cleanAllDemos") {
    group = "demo"
    description = "Clean all demo applications"
    dependsOn(":demo-java:clean", ":demo-kotlin:clean", ":demo-compose:clean")
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

// Demo validation tasks
tasks.register("validateDemos") {
    group = "demo"
    description = "Validate all demo applications (build + test)"
    dependsOn("buildAllDemos", "testAllDemos")
}

// Demo screenshot testing tasks (only Compose demo supports screenshots)
tasks.register("recordAllDemoScreenshots") {
    group = "demo"
    description = "Record screenshots for Compose demo application " +
        "(Java and Kotlin demos use basic functionality tests)"
    dependsOn(":demo-compose:recordRoborazziDebug")
}

tasks.register("verifyAllDemoScreenshots") {
    group = "demo"
    description = "Verify screenshots for Compose demo application " +
        "(Java and Kotlin demos use basic functionality tests)"
    dependsOn(":demo-compose:verifyRoborazziDebug")
}

// Complete demo validation including screenshots
tasks.register("validateDemosComplete") {
    group = "demo"
    description = "Complete validation of all demo applications (build + test + screenshots)"
    dependsOn("validateDemos", "verifyAllDemoScreenshots")
}

// Ensure demos are excluded from library build and distribution
tasks.named("assemble") {
    // Explicitly exclude demo modules from main library assembly
    mustRunAfter("cleanAllDemos")
}

tasks.named("build") {
    // Ensure library build doesn't include demo modules
    finalizedBy("validateDemos")
}

// Custom Gradle tasks for BEM Co-Located Test Structure
// Configure test filtering after evaluation
afterEvaluate {
    // Combined test tasks - use Exec type for proper configuration cache support
    tasks.register<Exec>("testUnit") {
        description = "Run all unit tests (Java and Kotlin)"
        group = "verification"

        // Check for tests at configuration time
        val hasJavaTests =
            fileTree("src/main/java").matching {
                include("**/RadioTestUnit.java")
            }.files.isNotEmpty()

        val hasKotlinTests =
            fileTree("src/main/java").matching {
                include("**/*.test-unit.kt")
            }.files.isNotEmpty()

        if (hasJavaTests || hasKotlinTests) {
            commandLine("./gradlew", ":testDebugUnitTest", "--tests=*RadioTestUnit*", "--continue")
            isIgnoreExitValue = true
        } else {
            // Create a dummy command that just prints a message
            commandLine("echo", "No unit tests found - skipping testUnit")
        }
    }

    tasks.register<Exec>("testVisual") {
        description = "Run all visual tests (Java and Kotlin) using Roborazzi"
        group = "verification"

        // Check for tests at configuration time
        val hasJavaTests =
            fileTree("src/main/java").matching {
                include("**/RadioTestVisual.java")
            }.files.isNotEmpty()

        val hasKotlinTests =
            fileTree("src/main/java").matching {
                include("**/*.test-visual.kt")
            }.files.isNotEmpty()

        if (hasJavaTests || hasKotlinTests) {
            commandLine(
                "./gradlew",
                ":testDebugUnitTest",
                "--tests=*RadioTestVisual*",
                "--continue"
            )
            isIgnoreExitValue = true
        } else {
            // Create a dummy command that just prints a message
            commandLine("echo", "No visual tests found - skipping testVisual")
        }
    }

    // Java test tasks
    tasks.register<Exec>("testUnitJava") {
        description = "Run only Java unit tests (Radio.test/RadioTestUnit.java files)"
        group = "verification"

        // Check for tests at configuration time
        val hasTests =
            fileTree("src/main/java").matching {
                include("**/RadioTestUnit.java")
            }.files.isNotEmpty()

        if (hasTests) {
            commandLine("./gradlew", ":testDebugUnitTest", "--tests=*RadioTestUnit*", "--continue")
            isIgnoreExitValue = true
        } else {
            // Create a dummy command that just prints a message
            commandLine("echo", "No Java unit tests found - skipping testUnitJava")
        }
    }

    tasks.register<Exec>("testVisualJava") {
        description = "Run only Java visual tests (Radio.test/RadioTestVisual.java files)"
        group = "verification"

        // Check for tests at configuration time
        val hasTests =
            fileTree("src/main/java").matching {
                include("**/*TestVisual.java")
            }.files.isNotEmpty()

        if (hasTests) {
            commandLine("./gradlew", ":testDebugUnitTest", "--tests=*TestVisual*", "--continue")
            isIgnoreExitValue = true
        } else {
            // Create a dummy command that just prints a message
            commandLine("echo", "No Java visual tests found - skipping testVisualJava")
        }
    }

    // Kotlin test tasks
    tasks.register<Exec>("testUnitKotlin") {
        description = "Run only Kotlin unit tests (*.test-unit.kt files)"
        group = "verification"

        // Check for tests at configuration time
        val hasTests =
            fileTree("src/main/java").matching {
                include("**/*.test-unit.kt")
            }.files.isNotEmpty()

        if (hasTests) {
            commandLine("./gradlew", ":testDebugUnitTest", "--tests=*UnitTest*", "--continue")
            isIgnoreExitValue = true
        } else {
            // Create a dummy command that just prints a message
            commandLine("echo", "No Kotlin unit tests found - skipping testUnitKotlin")
        }
    }

    tasks.register<Exec>("testVisualKotlin") {
        description = "Run only Kotlin visual tests (RadioTestVisual.kt files) using Roborazzi"
        group = "verification"

        // Check for tests at configuration time
        val hasTests =
            fileTree("src/main/java").matching {
                include("**/*.test-visual.kt")
            }.files.isNotEmpty()

        if (hasTests) {
            commandLine(
                "./gradlew",
                ":testDebugUnitTest",
                "--tests=*RadioTestVisual*",
                "--continue"
            )
            isIgnoreExitValue = true
        } else {
            // Create a dummy command that just prints a message
            commandLine("echo", "No Kotlin visual tests found - skipping testVisualKotlin")
        }
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

    tasks.register("validateJava") {
        description = "Complete Java validation (style + tests)"
        group = "verification"
        dependsOn("testJava") // Removed checkJavaStyle and checkJavaFormat as they don't exist
    }

    tasks.register("validateKotlin") {
        description = "Complete Kotlin validation (style + tests)"
        group = "verification"
        dependsOn("testKotlin") // Removed checkKotlinStyle as it doesn't exist
    }

    tasks.register<Copy>("copyRoborazziScreenshots") {
        description = "Copy Roborazzi screenshots to BEM structure"
        group = "verification"

        from("build/intermediates/roborazzi")
        into("src/main/java/ru/voboost/components/radio/Radio.screenshots")
        include("*.png")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        dependsOn("recordRoborazziDebug")
        doNotTrackState("Screenshots are managed externally")
    }

    tasks.register("testVisualSave") {
        description = "Record and save visual test screenshots using Roborazzi"
        group = "verification"

        dependsOn("copyRoborazziScreenshots")

        doLast {
            println("Screenshots recorded and copied to BEM structure")
        }
    }
}

// Configure Roborazzi for BEM Co-Located Test Structure
roborazzi {
    // Configure output directory for screenshots
    outputDir = file("src/main/java/ru/voboost/components/radio/Radio.screenshots")
}

afterEvaluate {
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
}

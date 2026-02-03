import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint")
    id("app.cash.paparazzi") version "1.3.4"
    id("checkstyle")
    id("com.diffplug.spotless")
}

// Load versions from voboost-codestyle
val codeStyleVersions =
    Properties().apply {
        file("../voboost-codestyle/versions.properties").inputStream().use {
            this.load(it)
        }
    }

// Configure ktlint for Kotlin
configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set(codeStyleVersions.getProperty("ktlint.version"))
    android.set(true)
    ignoreFailures.set(false)

    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }

    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

// Configure Checkstyle for Java
configure<CheckstyleExtension> {
    toolVersion = codeStyleVersions.getProperty("checkstyle.version")
    configFile = file("../voboost-codestyle/checkstyle.xml")
    isIgnoreFailures = false
    isShowViolations = true
}

// Configure Spotless for Java and Kotlin formatting
configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
        target("src/**/*.java")
        targetExclude("**/generated/**", "**/build/**")

        googleJavaFormat(codeStyleVersions.getProperty("google.java.format.version"))
            .aosp()
            .reflowLongStrings()
            .skipJavadocFormatting()

        removeUnusedImports()
        importOrder("java", "javax", "android", "androidx", "com", "org", "")
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlin {
        target("src/**/*.kt")
        targetExclude("**/generated/**", "**/build/**")

        ktlint(codeStyleVersions.getProperty("ktlint.version"))
        trimTrailingWhitespace()
        endWithNewline()
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
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
                srcDir("src/main/java")
            }
        }
    }

    // IDE integration
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            // Ensure IDE recognizes co-located test files
            freeCompilerArgs +=
                listOf(
                    "-Xjvm-default=all",
                )
        }
    }

    // CRITICAL: Строго исключаем тестовые файлы из основной компиляции
    afterEvaluate {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            if (!name.contains("Test") && !name.contains("test")) {
                exclude("**/*.test-*.kt")
                exclude("**/*Test.kt")
                exclude("**/*Tests.kt")
            }
        }

        // Исключаем Java тестовые файлы из основной компиляции
        tasks.withType<JavaCompile>().configureEach {
            if (!name.contains("Test") && !name.contains("test")) {
                exclude("**/*.test-*.java")
                exclude("**/*Test.java")
                exclude("**/*Tests.java")
            }
        }

        // Дополнительная защита: исключаем тестовые файлы из JAR
        tasks.withType<Jar>().configureEach {
            exclude("**/*.test-*.kt")
            exclude("**/*Test.kt")
            exclude("**/*Tests.kt")
            exclude("**/*.test-*.class")
            exclude("**/*Test.class")
            exclude("**/*Tests.class")
            exclude("**/*.test-*.java")
            exclude("**/*Test.java")
            exclude("**/*Tests.java")
        }

        // Исключаем тестовые файлы из AAR
        tasks.withType<com.android.build.gradle.tasks.BundleAar>().configureEach {
            exclude("**/*.test-*.kt")
            exclude("**/*Test.kt")
            exclude("**/*Tests.kt")
            exclude("**/*.test-*.class")
            exclude("**/*Test.class")
            exclude("**/*Tests.class")
            exclude("**/*.test-*.java")
            exclude("**/*Test.java")
            exclude("**/*Tests.java")
        }
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
    testImplementation("app.cash.paparazzi:paparazzi:1.3.4") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15on")
        exclude(group = "org.hamcrest", module = "hamcrest-core")
    }
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

// Custom Gradle tasks for BEM Co-Located Test Structure
tasks.register<Test>("testUnit") {
    description = "Run only unit tests (*.test-unit.kt files)"
    group = "verification"

    // Copy configuration from testDebugUnitTest
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    // Filter to include only unit test files
    filter {
        includeTestsMatching("*RadioUnitTest*")
        excludeTestsMatching("*RadioVisualTest*")
    }

    // Set up test logging
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

tasks.register<Test>("testVisual") {
    description = "Run only visual tests (*.test-visual.kt files) using Paparazzi"
    group = "verification"

    // Copy configuration from testDebugUnitTest
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    // Filter to include only visual test files
    filter {
        includeTestsMatching("*RadioVisualTest*")
        excludeTestsMatching("*RadioUnitTest*")
    }

    // Set up test logging
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }

    // Configure Paparazzi for BEM Co-Located structure
    systemProperty("paparazzi.test.record", "true")
    systemProperty("paparazzi.test.verify", "true")

    // Ensure tests can find co-located test files
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")

    doFirst {
        // Ensure screenshot directories exist
        val screenshotDir = file("src/main/java/ru/voboost/components")
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs()
        }
    }
}

// Override the default test task to run all our custom tests
tasks.named("test") {
    description = "Run all tests (unit and visual) - BEM Co-Located structure"
    dependsOn("testUnit", "testVisual")
}

// Configure Paparazzi for BEM Co-Located Test Structure
afterEvaluate {
    // Configure Paparazzi snapshot directory for BEM Co-Located structure
    extensions.configure<app.cash.paparazzi.gradle.PaparazziExtension> {
        // Set custom snapshot directory to component-specific folders
        snapshotHandler.set(
            app.cash.paparazzi.gradle.PaparazziSnapshotHandler { snapshot, testName, context ->
                // Extract component name from test class
                val componentName =
                    testName.className.substringAfterLast(".")
                        .removeSuffix("VisualTest")
                        .removeSuffix("Test")
                        .lowercase()

                // Create component-specific screenshot directory
                val componentDir =
                    file(
                        "src/main/java/ru/voboost/components/" +
                            "$componentName/$componentName.screenshots",
                    )
                componentDir.mkdirs()

                // Save screenshot with descriptive name
                val screenshotFile =
                    File(componentDir, "${testName.methodName}.png")
                screenshotFile.writeBytes(snapshot)
            },
        )
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
}

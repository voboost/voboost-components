pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.2.2"
        id("com.android.library") version "8.2.2"
        id("org.jetbrains.kotlin.android") version "2.0.21"
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "voboost-components"

// Include demo modules
include(":demo-java")
project(":demo-java").projectDir = file("src/demo-java")

include(":demo-kotlin")
project(":demo-kotlin").projectDir = file("src/demo-kotlin")

include(":demo-compose")
project(":demo-compose").projectDir = file("src/demo-compose")

include(":demo-shared")
project(":demo-shared").projectDir = file("src/demo-shared")

include(":demo-pixel")
project(":demo-pixel").projectDir = file("src/demo-pixel")

include(":demo-cunba")
project(":demo-cunba").projectDir = file("src/demo-cunba")

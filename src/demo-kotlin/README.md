# Kotlin Demo Application

This demo application demonstrates the integration of voboost-components library in a Kotlin Android project using modern Kotlin language features and patterns.

## Purpose

This demo showcases:
- **Kotlin Language Integration**: How to use voboost-components with Kotlin-specific features
- **Modern Android Patterns**: Kotlin-friendly APIs and extension functions
- **Automotive UI Optimization**: Kotlin-based state management for automotive displays
- **Type Safety**: Leveraging Kotlin's type system for safer component integration

## Architecture Principles

- **Kotlin-First Design**: Uses Kotlin language features like data classes, when expressions, and extension functions
- **Automotive-Optimized**: Designed for 1920x720 automotive displays with proper touch targets
- **Traditional Android Callbacks**: Uses standard Android callback patterns for value changes
- **Type-Safe APIs**: Leverages Kotlin's null safety and type inference

## Building and Running

### Prerequisites
- Android SDK with API level 28 or higher
- Kotlin 1.9.25 or higher
- Android device or emulator with automotive profile

### Build Commands

```bash
# Build the Kotlin demo
./gradlew :demo-kotlin:assembleDebug

# Install to connected device
./gradlew :demo-kotlin:installDebug

# Build using main project convenience tasks
./gradlew buildDemoKotlin
./gradlew installDemoKotlin
```

### Build All Demos
```bash
# Build all demos simultaneously
./gradlew buildAllDemos

# Install all demos
./gradlew installAllDemos
```

## Testing

### Unit Tests
```bash
# Run unit tests
./gradlew :demo-kotlin:testDebugUnitTest
```

### Visual Regression Tests

Visual tests are co-located with the MainActivity following BEM structure:

```
java/ru/voboost/components/demo/kotlin/
├── MainActivity.kt
├── MainActivity.tests/
│   └── MainActivityTestVisual.kt
└── MainActivity.screenshots/
    ├── demo_kotlin_default.png
    ├── demo_kotlin_russian.png
    ├── demo_kotlin_dark.png
    ├── demo_kotlin_dreamer.png
    └── demo_kotlin_full_combination.png
```

Standard test scenarios:
1. **demo_kotlin_default** - Default state: English, Light, Free
2. **demo_kotlin_russian** - Russian language: Russian, Light, Free
3. **demo_kotlin_dark** - Dark theme: English, Dark, Free
4. **demo_kotlin_dreamer** - Dreamer car type: English, Light, Dreamer
5. **demo_kotlin_full_combination** - Full combination: Russian, Dark, Dreamer

```bash
# Run visual tests
./gradlew :demo-kotlin:testDebugUnitTest --tests "ru.voboost.components.demo.kotlin.MainActivityTestVisual"

# Record new screenshots
./gradlew :demo-kotlin:recordDemoScreenshots

# Verify against existing screenshots
./gradlew :demo-kotlin:verifyDemoScreenshots
```

### Complete Validation
```bash
# Full validation (build + test + screenshots)
./gradlew validateDemosComplete
```

## Running the Demo

1. **Build and Install**:
   ```bash
   ./gradlew installDemoKotlin
   ```

2. **Launch**: Find "Kotlin Demo" app on your device/emulator

3. **Test Functionality**:
   - Experience Kotlin-friendly API patterns
   - Test reactive state management
   - Verify automotive-appropriate interactions
   - Test the Test Radio component with animation options (Close, Normal, Sync with Music, Sync with Driving)

## Integration Example

Kotlin integration pattern with proper API usage:

```kotlin
import ru.voboost.components.radio.Radio
import ru.voboost.components.radio.RadioButton
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

// In your Activity
val radio = findViewById<Radio>(R.id.radio_component)

// Create radio buttons with localization maps
val buttons = listOf(
    RadioButton("option1", mapOf(
        "en" to "Option 1",
        "ru" to "Вариант 1"
    )),
    RadioButton("option2", mapOf(
        "en" to "Option 2",
        "ru" to "Вариант 2"
    )),
    RadioButton("option3", mapOf(
        "en" to "Option 3",
        "ru" to "Вариант 3"
    ))
)

// Proper initialization sequence
radio.apply {
    // Set language and theme first (required)
    setLanguage(Language.fromCode("en"))
    setTheme(Theme.fromValue("free-light"))

    // Then set buttons and initial selection
    setButtons(buttons)
    setSelectedValue("option1")

    // Set up value change listener
    setOnValueChangeListener { newValue ->
        // Handle selection with traditional Android callback
        updateApplicationState(newValue)
    }
}
```

## Performance Characteristics

- **Target**: 60fps rendering on automotive hardware
- **Memory**: Kotlin-optimized object allocation
- **Traditional Callbacks**: Standard Android callback patterns for value changes
- **Screen Resolution**: Native 1920x720 automotive display support

## Troubleshooting

### Build Issues
- Verify Kotlin 1.9.25+ configuration
- Ensure Android SDK API 28+ is installed
- Check voboost-components library dependency

### Runtime Issues
- Confirm automotive screen resolution settings
- Verify proper initialization sequence (language/theme before buttons)
- Check that RadioButton objects are created with proper localization maps

### Common Configuration Problems

#### Incorrect Initialization Sequence
```kotlin
// WRONG - This will cause issues
radio.setButtons(buttons)  // Setting buttons before language/theme
radio.setLanguage(Language.EN)
radio.setTheme(Theme.FREE_LIGHT)

// CORRECT - Proper initialization sequence
radio.setLanguage(Language.EN)
radio.setTheme(Theme.FREE_LIGHT)
radio.setButtons(buttons)
```

#### Creating RadioButton Objects
```kotlin
// WRONG - Missing localization map
val button = RadioButton("value", "Label")

// CORRECT - With proper localization map
val button = RadioButton("value", mapOf(
    "en" to "Label",
    "ru" to "Метка"
))
```

#### Using Language and Theme Enums
```kotlin
// WRONG - Using string values directly
radio.setLanguage("en")
radio.setTheme("free-light")

// CORRECT - Using enum methods
radio.setLanguage(Language.fromCode("en"))
radio.setTheme(Theme.fromValue("free-light"))
```

### Screenshot Test Issues
- Ensure consistent test environment
- Verify Roborazzi automotive resolution configuration
- Check Kotlin test runner configuration

This demo serves as a reference for integrating voboost-components into modern Kotlin Android projects with automotive requirements and demonstrates best practices for Kotlin-based UI development.

# Compose Demo Application

This demo application demonstrates the integration of voboost-components library in a Jetpack Compose project using modern declarative UI patterns with the new 7-tab structure.

## Purpose

This demo showcases:
- **7-Tab Navigation Structure**: Language, Theme, Car Type, Climate, Audio, Display, System, and Screen Lift
- **Proper Component Hierarchy**: Screen → Panel → Tabs → Section → Radio
- **Jetpack Compose Integration**: How to use voboost-components in modern Compose-based projects
- **Declarative UI Patterns**: Reactive state management with Compose state primitives
- **Automotive Compose UI**: Best practices for automotive displays using Compose
- **AndroidView Integration**: Seamless integration of Custom Views within Compose hierarchy

## Architecture Principles

- **7-Tab Structure**: Unified navigation across all demo applications
- **Proper Component Hierarchy**: Screen → Panel → Tabs → Section → Radio
- **Compose-First Design**: Uses `setContent {}` with Composable functions
- **Automotive-Optimized**: Designed for 1920x720 automotive displays with Compose layouts
- **Reactive State**: Leverages Compose state management (`remember`, `mutableStateOf`)
- **Declarative Patterns**: Functional UI composition with automatic recomposition
- **Shared Demo Module**: Uses demo-shared module for consistent functionality

## Key Difference from Other Demos

Unlike `demo-java` and `demo-kotlin` which use traditional Android Views with `setContentView()`, this demo uses:

1. **`setContent {}`** - Compose way to set UI content
2. **`@Composable` functions** - Declarative UI components
3. **Compose State** - `remember` and `mutableStateOf` for reactive updates
4. **AndroidView** - Integration layer for Java Custom Views

## Building and Running

### Prerequisites
- Android SDK with API level 28 or higher
- Kotlin 1.9.25 or higher with Compose compiler
- Android device or emulator with automotive profile

### Build Commands

```bash
# Build the Compose demo
./gradlew :demo-compose:assembleDebug

# Install to connected device
./gradlew :demo-compose:installDebug

# Build using main project convenience tasks
./gradlew buildDemoCompose
./gradlew installDemoCompose
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
./gradlew :demo-compose:testDebugUnitTest
```

### Visual Regression Tests

Visual tests are co-located with the MainActivity following BEM structure:

```
java/ru/voboost/components/demo/compose/
├── MainActivity.kt
├── MainActivity.tests/
│   └── MainActivityTestVisual.kt
└── MainActivity.screenshots/
    ├── demo_compose_default.png
    ├── demo_compose_russian.png
    ├── demo_compose_dark.png
    ├── demo_compose_dreamer.png
    └── demo_compose_full_combination.png
```

Standard test scenarios:
1. **demo_compose_default** - Default state: English, Light, Free, Language tab
2. **demo_compose_russian** - Russian language: Russian, Light, Free, Language tab
3. **demo_compose_dark** - Dark theme: English, Dark, Free, Theme tab
4. **demo_compose_dreamer** - Dreamer car type: English, Light, Dreamer, Car Type tab
5. **demo_compose_full_combination** - Full combination: Russian, Dark, Dreamer, Language tab
6. **demo_compose_all_tabs** - All 7 tabs with default values
7. **demo_compose_component_hierarchy** - Component hierarchy verification

```bash
# Run visual tests
./gradlew :demo-compose:testDebugUnitTest --tests "ru.voboost.components.demo.compose.MainActivityTestVisual"

# Record new screenshots
./gradlew :demo-compose:recordDemoScreenshots

# Verify against existing screenshots
./gradlew :demo-compose:verifyDemoScreenshots
```

### Complete Validation
```bash
# Full validation (build + test + screenshots)
./gradlew validateDemosComplete
```

## Running the Demo

1. **Build and Install**:
   ```bash
   ./gradlew installDemoCompose
   ```

2. **Launch**: Find "Compose Demo" app on your device/emulator

3. **Test Functionality**:
   - Navigate through all 7 tabs: Language, Theme, Car Type, Climate, Audio, Display, System, Screen Lift
   - Experience declarative UI updates
   - Test Compose state management
   - Verify automotive-appropriate Compose layouts
   - Test proper component hierarchy: Screen → Panel → Tabs → Section → Radio

## Integration Example

Compose integration pattern with declarative UI:

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ru.voboost.components.radio.Radio
import ru.voboost.components.radio.RadioButton
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

@Composable
fun RadioExample() {
    var selectedValue by remember { mutableStateOf("") }

    // Create radio buttons with localization maps
    val buttons = listOf(
        RadioButton("option1", mapOf(
            "en" to "Option 1",
            "ru" to "Вариант 1"
        )),
        RadioButton("option2", mapOf(
            "en" to "Option 2",
            "ru" to "Вариант 2"
        ))
    )

    Radio(
        buttons = buttons,
        lang = Language.EN,
        theme = Theme.FREE_LIGHT,
        value = selectedValue,
        onValueChange = { newValue ->
            selectedValue = newValue
        }
    )
}

// In your Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoboostTheme {
                DemoScreen()
            }
        }
    }
}
```

### Full Application Pattern

The demo shows a complete Compose application structure:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use Compose setContent instead of setContentView
        setContent {
            VoboostDemoApp()
        }
    }
}

@Composable
fun VoboostDemoApp() {
    // Compose state management
    var selectedTab by remember { mutableStateOf("language") }
    var currentLanguage by remember { mutableStateOf("en") }
    var currentTheme by remember { mutableStateOf("light") }
    var currentCarType by remember { mutableStateOf("free") }

    // Derived state
    val combinedTheme = remember(currentCarType, currentTheme) {
        "$currentCarType-$currentTheme"
    }

    // UI with AndroidView integration
    Box(modifier = Modifier.fillMaxSize()) {
        ScreenWrapper(
            selectedTab = selectedTab,
            combinedTheme = combinedTheme,
            currentLanguage = currentLanguage,
            onTabSelected = { selectedTab = it },
            onValueChange = { tab, value ->
                when (tab) {
                    "language" -> currentLanguage = value
                    "theme" -> currentTheme = value
                    "car_type" -> currentCarType = value
                }
            }
        )
    }
}
```

### AndroidView Integration Pattern

For integrating Java Custom Views in Compose:

```kotlin
@Composable
fun ScreenWrapper(
    selectedTab: String,
    combinedTheme: String,
    currentLanguage: String,
    onTabSelected: (String) -> Unit,
    onValueChange: (String, String) -> Unit
) {
    AndroidView(
        factory = { context ->
            // Create Java Custom View
            val screen = Screen(context)
            val tabs = Tabs(context).apply {
                setOnValueChangeListener { onTabSelected(it) }
            }
            screen.setTabs(tabs)
            screen
        },
        update = { screenView ->
            // Update when parameters change
            screenView.setTheme(Theme.fromValue(combinedTheme))
            screenView.getTabs()?.apply {
                setLanguage(Language.fromCode(currentLanguage))
                setSelectedValue(selectedTab, false)
            }
        }
    )
}
```

## Performance Characteristics

- **Target**: 60fps rendering with Compose optimizations
- **Memory**: Compose-optimized recomposition and state management
- **Recomposition**: Efficient updates with Compose compiler optimizations
- **Screen Resolution**: Native 1920x720 automotive display support

## Troubleshooting

### Build Issues
- Verify Compose compiler version compatibility
- Ensure Android SDK API 28+ is installed
- Check voboost-components library dependency

### Runtime Issues
- Confirm automotive screen resolution in Compose
- Verify Compose state management setup
- Check AndroidView integration configuration

### Screenshot Test Issues
- Ensure Roborazzi Compose integration is configured
- Verify automotive resolution for Compose tests
- Check Compose test environment setup

### Compose-Specific Issues
- Monitor recomposition performance
- Verify state hoisting patterns
- Check Compose lifecycle integration

### Configuration Issues

#### Initialization Problems
**Problem**: Component not rendering or showing blank space
**Solution**: Ensure proper initialization sequence:
```kotlin
// WRONG - Missing language/theme setup
val radioView = Radio(context)
radioView.setButtons(buttons) // Won't work without language/theme

// CORRECT - Proper initialization sequence
val radioView = Radio(context)
radioView.setLanguage(Language.EN)        // Required first
radioView.setTheme(Theme.FREE_LIGHT)      // Required second
radioView.setButtons(buttons)             // Works after setup
```

#### RadioButton Creation Issues
**Problem**: IllegalArgumentException when creating RadioButton objects
**Solution**: Ensure proper RadioButton construction with localization maps:
```kotlin
// WRONG - Empty or null label map
RadioButton("value", emptyMap()) // Throws exception

// CORRECT - Proper localization map
RadioButton("value", mapOf(
    "en" to "English Text",
    "ru" to "Русский текст"
))
```

#### Language/Theme Conversion Issues
**Problem**: Using string values instead of enum values
**Solution**: Use proper conversion methods:
```kotlin
// WRONG - Direct string usage
radioView.setLanguage("en")    // Compile error
radioView.setTheme("free-light") // Compile error

// CORRECT - Using enum conversion methods
radioView.setLanguage(Language.fromCode("en"))
radioView.setTheme(Theme.fromValue("free-light"))
```

#### State Management Issues
**Problem**: Component not updating when state changes
**Solution**: Ensure proper Compose state management:
```kotlin
// WRONG - Not using Compose state
var selectedValue = "option1" // Won't trigger recomposition

// CORRECT - Using Compose state
var selectedValue by remember { mutableStateOf("option1") }
```

#### AndroidView Integration Issues
**Problem**: Component not responding to parameter changes
**Solution**: Ensure proper update block implementation:
```kotlin
AndroidView(
    factory = { context ->
        Radio(context).apply {
            // Initial setup
            setLanguage(lang)
            setTheme(theme)
            setButtons(buttons)
        }
    },
    update = { radioView ->
        // IMPORTANT: Update all parameters when they change
        radioView.setLanguage(lang)     // Required for language changes
        radioView.setTheme(theme)       // Required for theme changes
        radioView.setButtons(buttons)   // Required for button changes
        radioView.setSelectedValue(value) // Required for value changes
    }
)
```

This demo serves as a reference for integrating voboost-components into modern Jetpack Compose projects with automotive requirements and demonstrates best practices for declarative UI development.

# Voboost Components Library

A multilingual UI components library for Voyah vehicle applications supporting Java, Kotlin and Jetpack Compose. This library provides automotive-grade components with maximum compatibility across all Android development approaches.

## Project Overview

**Purpose**: Universal UI components library for the voboost ecosystem
**Target**: Java, Kotlin and Jetpack Compose Android applications
**Architecture**: Java Custom View foundation with Kotlin Compose wrapper
**Focus**: Starting with Radio component, expandable to other automotive UI components
**Compatibility**: Android 9 (API 28) and Android 11 (API 30) for automotive systems

## Multilingual Architecture

### Java Custom View + Kotlin Wrapper Design

The library implements a unique multilingual architecture that maximizes compatibility:

```
┌─────────────────────────────────────────────────────────────┐
│                    Component Architecture                    │
├─────────────────────────────────────────────────────────────┤
│  Jetpack Compose Projects                                   │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Radio.kt (Compose Wrapper)                              │ │
│  │ @Composable fun Radio(...)                              │ │
│  │ └─ AndroidView integration                               │ │
│  └─────────────────────────────────────────────────────────┘ │
│                            │                                │
│                            ▼                                │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Radio.java (Core Implementation)                        │ │
│  │ extends View                                            │ │
│  │ • Canvas-based rendering                                │ │
│  │ • Touch event handling                                  │ │
│  │ • Animation system                                      │ │
│  │ • Theme management                                      │ │
│  │ • Localization support                                  │ │
│  └─────────────────────────────────────────────────────────┘ │
│                            ▲                                │
│  Java/Kotlin Projects      │                                │
│  Direct usage ─────────────┘                                │
└─────────────────────────────────────────────────────────────┘
```

### Key Architectural Principles

- **Java Custom View Foundation**: Core implementation ensures maximum compatibility and performance
- **Kotlin Compose Wrapper**: Lightweight AndroidView integration for modern Compose projects
- **Universal API**: Consistent functionality across Java, Kotlin and Compose usage patterns
- **Single Source of Truth**: All visual behavior, animations and business logic centralized in Java implementation
- **Cross-Platform Performance**: Optimized for automotive hardware across all usage scenarios

### Component Design Patterns

- **Multilingual Support**: Components work seamlessly in Java, Kotlin and Compose projects
- **Theme-Aware Components**: All components can be rendered in different vehicle themes
- **Touch-Optimized**: Large touch targets (minimum 48dp) and automotive-friendly interactions
- **State Management**: Consistent state handling across all implementation approaches
- **Performance Optimized**: Canvas-based rendering with efficient animations for automotive hardware

### Multi-Theme Support

The library supports four theme combinations:
- `free-light` - Voyah Free model with light theme
- `free-dark` - Voyah Free model with dark theme
- `dreamer-light` - Voyah Dreamer model with light theme
- `dreamer-dark` - Voyah Dreamer model with dark theme

### Component API Design

All components follow consistent patterns:

```kotlin
@Composable
fun ComponentName(
    // Configuration parameters
    options: List<ComponentOption>,
    lang: Language,
    theme: Theme,
    // Value management
    value: String,
    onValueChange: (String) -> Unit
)
```

### Internationalization

Components use `Map<String, String>` for labels where the key is the language code:

```kotlin
data class ComponentOption(
    val value: String,
    val labels: Map<String, String> // "en" -> "English Label", "ru" -> "Русская метка"
)
```

## Technical Specifications

### Android Compatibility
- **Minimum SDK**: 28 (Android 9)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

### Dependencies
- **Jetpack Compose BOM**: 2024.02.00
- **Kotlin**: 1.9.25
- **Lifecycle Components**: 2.7.0

### Performance Requirements
- **Target Frame Rate**: 60fps on automotive hardware
- **Memory Efficiency**: Optimized object creation with `remember` caching
- **Recomposition Optimization**: Minimal unnecessary recompositions
- **Touch Response**: < 100ms response time for automotive use

## Build Instructions

### Prerequisites

1. **JDK**: 11 or later
2. **Android SDK**: API 28
3. **Gradle**: 8.7.3 or later

### Building the Library

```bash
# Build the library
./gradlew assembleDebug

# Run tests
./gradlew test
```

## Demo Applications

The library includes three comprehensive demo applications that showcase integration patterns for different development approaches:

### Available Demos

- **[Java Demo](src/demo-java/README.md)**: Pure Java Android project demonstrating Java Custom View integration
- **[Kotlin Demo](src/demo-kotlin/README.md)**: Kotlin Android project showcasing modern Kotlin language features
- **[Compose Demo](src/demo-compose/README.md)**: Jetpack Compose project demonstrating declarative UI patterns

### Demo Features

Each demo application demonstrates:
- **Automotive UI Patterns**: Optimized for 1920x720 automotive displays
- **Multi-language Support**: English and Russian localization
- **Theme Management**: Dynamic switching between vehicle themes
- **State Management**: Reactive component updates and event handling
- **Performance Optimization**: 60fps rendering for automotive hardware

### Building and Running Demos

```bash
# Build all demo applications
./gradlew buildAllDemos

# Install all demos to connected device
./gradlew installAllDemos

# Build individual demos
./gradlew buildDemoJava
./gradlew buildDemoKotlin
./gradlew buildDemoCompose

# Install individual demos
./gradlew installDemoJava
./gradlew installDemoKotlin
./gradlew installDemoCompose
```

### Testing Demo Applications

```bash
# Run tests for all demos
./gradlew testAllDemos

# Record screenshots for all demos (automotive resolution)
./gradlew recordAllDemoScreenshots

# Verify screenshots for all demos
./gradlew verifyAllDemoScreenshots

# Complete demo validation (build + test + screenshots)
./gradlew validateDemosComplete
```

### Demo Integration in Development Workflow

The demo applications are:
- **Included in Development**: Part of the main project for continuous validation
- **Excluded from Distribution**: Not included in library artifacts
- **CI/CD Integrated**: Automatically tested in continuous integration
- **Documentation by Example**: Serve as living documentation for integration patterns

## Component Usage

### Radio Component

For comprehensive Radio component documentation including usage examples for Java, Kotlin and Jetpack Compose projects, see [Radio.md](src/main/java/ru/voboost/components/radio/Radio.md)

The Radio component supports three usage patterns:
- **Pure Java Projects**: Direct instantiation of Java Custom View
- **Pure Kotlin Projects**: Java Custom View with Kotlin syntax
- **Jetpack Compose Projects**: Kotlin wrapper using AndroidView integration

## Development Guide

### Code Style Requirements

This project follows the voboost-codestyle rules with multilingual support. Key requirements:

- **Language**: All code comments, documentation and commit messages in English only
- **Formatting**: ktlint for Kotlin, Google Java Style for Java with voboost-specific rules
- **Architecture**: Java Custom View foundation with Kotlin Compose wrapper
- **Testing**: Minimum 80% code coverage across both Java and Kotlin implementations
- **Cross-Platform Compatibility**: Components must work in Java, Kotlin and Compose projects

### Multilingual Component Development Patterns

#### 1. Multilingual Co-Located File Organization

Each component follows the enhanced co-located structure supporting both Java and Kotlin:

```
src/main/java/ru/voboost/components/
├── radio/                          # Component block
│   ├── Radio.java                 # Primary Java Custom View implementation
│   ├── Radio.kt                   # Kotlin Compose wrapper using AndroidView
│   ├── RadioButton.java           # Java data model
│   ├── RadioTheme.java            # Java theme constants
│   ├── Radio.test/                # BEM co-located test directory
│   │   ├── RadioTestUnit.java     # Java unit tests
│   │   └── RadioTestVisual.java   # Java visual tests
│   ├── Radio.md                   # Comprehensive multilingual documentation
│   └── Radio.screenshots/         # Shared visual test results
│       ├── README.md              # Screenshot documentation
│       └── *.png                  # Generated screenshots
```

#### 2. Component Development Principles

- **Java Custom View Foundation**: Implement core functionality in Java extending Android View
- **Kotlin Compose Wrapper**: Create lightweight AndroidView integration for Compose projects
- **Self-Contained Architecture**: Each component contains only what it needs
- **External Localization**: All strings provided via Map parameters
- **Dynamic Theme Support**: Components support runtime theme switching
- **Performance Optimization**: Optimize for automotive hardware requirements

#### 3. API Design Guidelines

- **Consistent Parameters**: Standardized parameter naming across components
- **Universal Compatibility**: Same functionality across Java, Kotlin and Compose usage
- **Reactive Updates**: Components automatically respond to parameter changes
- **Automotive Performance**: Target 60fps rendering and <100ms touch response

### Performance Considerations

For automotive applications, performance is critical:

1. **Object Creation**: Use `remember` to cache expensive operations
2. **Recomposition**: Minimize with proper key usage and state management
3. **Memory Usage**: Avoid memory leaks in long-running automotive applications
4. **Animation**: Use hardware-accelerated animations with spring physics

## Testing Structure

The co-located structure integrates testing directly with components for better maintainability:

1. **Unit Tests**: Component-specific `Component.test/ComponentTestUnit.java` files for logic testing
2. **Visual Tests**: Roborazzi-based `Component.test/ComponentTestVisual.java` files for UI regression
3. **Screenshot Tests**: Automated visual verification across all themes

#### Component Testing Structure:
```java
// Radio.test/RadioTestUnit.java - Unit tests for component logic
public class RadioTestUnit {
    @Test
    public void testRadioInitialization() {
        Radio radio = new Radio(context);
        assertNotNull(radio);
    }

    @Test
    public void testStatePersistence() {
        // Test state save/restore
        Parcelable state = radio.onSaveInstanceState();
        radio.onRestoreInstanceState(state);
        assertEquals(expectedValue, radio.getSelectedValue());
    }
}

// Radio.test/RadioTestVisual.java - Visual regression tests with Roborazzi
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = {33}, qualifiers = "w1920dp-h720dp-land-mdpi")
public class RadioTestVisual {
    @Test
    public void radio_climate_en_1_free_light() {
        Radio radio = createRadio(getClimateButtons(), "en", "free-light", "automatic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }
}
```

## Testing Procedures

### Multilingual Testing Strategy

The library supports comprehensive testing across both Java and Kotlin implementations:

### Local Testing

```bash
# All tests (Java + Kotlin)
./gradlew test

# Unit tests
./gradlew testUnit              # All unit tests
./gradlew testUnitJava          # Java unit tests only

# Visual tests with Roborazzi
./gradlew testVisual            # All visual tests
./gradlew testVisualJava        # Java visual tests only
./gradlew testVisualSave        # Record and save screenshots

# Record new screenshots
./gradlew recordRoborazziDebug  # Generate new baseline screenshots

# Cross-platform validation
./gradlew validateJava          # Java validation (tests + style)
./gradlew checkJavaStyle        # Java code style validation
./gradlew formatJavaCode        # Java code formatting

# Code style checks
./gradlew ktlintCheck           # Kotlin style checks
./gradlew checkJavaStyle        # Java style checks
```

### Component Testing

Each component supports multilingual testing approaches:

```bash
# Test specific component (all languages)
./gradlew test --tests "*RadioTest*"
./gradlew testJava --tests "*RadioTest*"

# Generate component screenshots (both platforms)
./gradlew testDebugUnitTest --tests "*RadioVisualTest*"
./gradlew testJavaVisual --tests "*RadioVisualTest*"

# Performance testing
./gradlew testPerformance       # Automotive performance validation
```

### Cross-Platform Testing Workflow

1. **Java Custom View Testing**: Validate core implementation
2. **Kotlin Wrapper Testing**: Verify Compose integration
3. **Visual Regression**: Compare screenshots across platforms
4. **Performance Testing**: Ensure automotive requirements are met
5. **Integration Testing**: Test in real project scenarios

## Project Structure

This project follows the enhanced co-located methodology adapted for multilingual components. Each component is organized as a self-contained block supporting Java, Kotlin and Compose with integrated testing and documentation.

```
voboost-components/
├── build.gradle.kts              # Build configuration with multilingual support
├── proguard-rules.pro           # ProGuard rules
├── .roorules                    # Project-specific multilingual rules
├── README.md                    # This file
└── src/
    ├── main/
    │   ├── AndroidManifest.xml
    │   └── java/ru/voboost/components/
    │       ├── theme/                      # Shared theme definitions
    │       │   └── Theme.java             # Theme enum (FREE_LIGHT, FREE_DARK, etc.)
    │       ├── i18n/                       # Shared internationalization
    │       │   └── Language.java          # Language enum (EN, RU)
    │       └── radio/                      # Radio Component Block
    │           ├── Radio.java             # Primary Java Custom View implementation
    │           ├── Radio.kt               # Kotlin Compose wrapper using AndroidView
    │           ├── RadioButton.java       # Java data model
    │           ├── RadioTheme.java        # Java theme constants and colors
    │           ├── Radio.test/            # BEM co-located test directory
    │           │   ├── RadioTestUnit.java # Java unit tests
    │           │   └── RadioTestVisual.java # Java visual tests
    │           ├── Radio.md               # Component documentation
    │           └── Radio.screenshots/     # Visual test screenshots
    │               ├── README.md          # Screenshot documentation
    │               └── *.png              # Generated screenshots
```

### Structure Benefits

- **Multilingual Support**: Each component supports Java, Kotlin and Compose usage patterns
- **Self-Contained Components**: Each component block contains all related files for all platforms
- **Cross-Platform Testing**: Integrated testing across Java Custom View and Kotlin Compose wrapper
- **Visual Regression**: Comprehensive screenshots for all themes, languages and platforms
- **Component Documentation**: Detailed `.md` files with examples for all usage patterns
- **Performance Optimization**: Structure optimized for automotive development workflows
- **Maintainability**: Clear organization with comprehensive multilingual documentation
- **Universal Compatibility**: Maximum compatibility across all Android development approaches

## Integration Guide

### Consuming the Library

1. **Add Dependency**:
   ```kotlin
   // In your app's build.gradle.kts
   dependencies {
       implementation(project(":voboost-components"))
   }
   ```

2. **Import Components**:
   ```kotlin
   import ru.voboost.components.radio.Radio
   import ru.voboost.components.radio.RadioButton
   import ru.voboost.components.theme.Theme
   import ru.voboost.components.i18n.Language
   ```

3. **Use in Compose**:
   ```kotlin
   @Composable
   fun MyApp() {
       var selectedValue by remember { mutableStateOf("option1") }

       Radio(
           buttons = listOf(
               RadioButton("option1", mapOf("en" to "Option 1", "ru" to "Вариант 1")),
               RadioButton("option2", mapOf("en" to "Option 2", "ru" to "Вариант 2"))
           ),
           lang = Language.EN,
           theme = Theme.FREE_LIGHT,
           value = selectedValue,
           onValueChange = { selectedValue = it }
       )
   }
   ```

### Backward Compatibility

- **API Stability**: Minor version updates maintain API compatibility
- **Migration Path**: Gradual migration supported through wrapper functions
- **Deprecation Policy**: 2 major versions notice for breaking changes

## Contributing

### Multilingual Component Development

When adding new components, follow the enhanced multilingual co-located structure:

1. **Create Component Block**: New directory under `src/main/java/ru/voboost/components/`
2. **Required Files**:
   - `ComponentName.java` - Primary Java Custom View implementation
   - `ComponentName.kt` - Kotlin Compose wrapper using AndroidView
   - `ComponentNameButton.java` - Java data model (if needed)
   - `ComponentNameTheme.java` - Java theme constants and colors
   - `ComponentName.test/` - BEM co-located test directory
     - `ComponentNameTestUnit.java` - Java unit tests
     - `ComponentNameTestVisual.java` - Java visual tests (Roborazzi)
   - `ComponentName.md` - Component documentation
   - `ComponentName.screenshots/` - Visual test screenshots

3. **Development Guidelines**:
   - **Code Style**: Follow voboost-codestyle rules for both Java and Kotlin
   - **Testing**: Maintain comprehensive test coverage
   - **Documentation**: Create component-specific `.md` files with usage examples
   - **Performance**: Validate automotive performance requirements (60fps, <100ms touch response)
   - **Visual Testing**: Generate screenshots for all themes and languages
   - **API Consistency**: Ensure consistent functionality across Java, Kotlin and Compose usage
   - **Localization Support**: Implement Map-based localization with language parameter
   - **Theme Management**: Support dynamic theme switching using shared Theme enum

4. **Implementation Workflow**:
   1. **Java Custom View**: Core implementation extending Android View
   2. **Kotlin Wrapper**: AndroidView-based Compose integration
   3. **Testing**: Comprehensive unit and visual tests
   4. **Documentation**: Complete usage examples and API reference
   5. **Validation**: Cross-platform testing and performance verification

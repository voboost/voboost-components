# Voboost Components Library

A multilingual UI component library for Voyah vehicle applications supporting
Java, Kotlin and Jetpack Compose. The library provides automotive-grade
components with maximum compatibility across all Android development approaches.

## Project Overview

- **Purpose**: Universal UI components library for the voboost ecosystem
- **Target**: Java, Kotlin and Jetpack Compose Android applications
- **Architecture**: Java Custom View foundation with Kotlin Compose wrapper
- **Compatibility**: Android 9 (API 28) and Android 11 (API 30) for automotive systems
- **Screen**: Fixed 1920x720 pixels (automotive infotainment, not responsive)

## Multilingual Architecture

### Java Custom View + Kotlin Wrapper Design

The library implements a unique multilingual architecture that maximizes
compatibility:

```
┌─────────────────────────────────────────────────────────────┐
│                    Component Architecture                    │
├─────────────────────────────────────────────────────────────┤
│  Jetpack Compose Projects                                   │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Component.kt (Compose Wrapper)                          │ │
│  │ @Composable fun Component(...)                          │ │
│  │ └─ AndroidView integration                               │ │
│  └─────────────────────────────────────────────────────────┘ │
│                            │                                │
│                            ▼                                │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Component.java (Core Implementation)                    │ │
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

- **Java Custom View Foundation**: Core implementation ensures maximum
  compatibility and performance
- **Kotlin Compose Wrapper**: Lightweight AndroidView integration for modern
  Compose projects
- **Universal API**: Consistent functionality across Java, Kotlin and Compose
  usage patterns
- **Single Source of Truth**: All visual behavior, animations and business
  logic centralized in the Java implementation
- **Self-Contained Components**: No shared `Component.kt`, i18n, theme, or
  utils — each component carries only what it needs
- **Cross-Platform Performance**: Optimized for automotive hardware across all
  usage scenarios

### Component Design Patterns

- **Multilingual Support**: Components work seamlessly in Java, Kotlin and
  Compose projects
- **Theme-Aware Components**: All components can be rendered in different
  vehicle themes
- **Touch-Optimized**: Large touch targets and automotive-friendly interactions
- **State Management**: Consistent state handling across all implementation
  approaches
- **Performance Optimized**: Canvas-based rendering with efficient animations
  for automotive hardware

### Multi-Theme Support

The library supports four theme combinations:

- `free-light` - Voyah Free model with light theme
- `free-dark` - Voyah Free model with dark theme
- `dreamer-light` - Voyah Dreamer model with light theme
- `dreamer-dark` - Voyah Dreamer model with dark theme

### Internationalization

Components use `Map<String, String>` for labels where the key is the language
code:

```kotlin
data class ComponentOption(
    val value: String,
    val labels: Map<String, String> // "en" -> "English Label", "ru" -> "Русская метка"
)
```

## Technical Specifications

### Build System Requirements

**DO NOT UPGRADE** - These versions are locked for Android 9/11 compatibility:

| Component    | Version  | Rationale                                                  |
|--------------|----------|------------------------------------------------------------|
| **Java**     | 11       | Max compatibility with Android 9/11. NO Java 17/21.        |
| **AGP**      | 8.2.2    | Last AGP version supporting Java 11. NO AGP 8.7+.         |
| **Gradle**   | 8.14.1   | Build tool version                                         |
| **Kotlin**   | 2.0.21   | Language version (Compose compiler plugin matches)        |
| **Compile SDK** | 34   | Android 14                                                 |
| **Min SDK**  | 28       | Android 9 (automotive)                                     |
| **Target SDK** | 30     | Android 11 - primary platform for users                    |

**Critical**: Upgrading Java to 17+ or AGP to 8.7+ will break Android 9/11
compatibility without desugaring.

### Release-Only Build Scheme

The project uses a **release-only** build scheme: the `debug` variant is
disabled entirely via `androidComponents.beforeVariants`, so `./gradlew build`
produces a single release variant for the library and every demo.

- `./gradlew build` assembles, lints and tests the release variant only.
- Debuggable release APKs are produced on demand with
  `-Pdebuggable=true` (flips `isDebuggable` on the release variant).
- All demo tasks (`buildDemos`, `testDemos`, `installDemos`, `startDemo*`)
  target the `Release` variant (`assembleRelease`, `testReleaseUnitTest`,
  `installRelease`, ...).

### Dependencies

- **Jetpack Compose BOM**: 2024.10.01
- **Kotlin**: 2.0.21 (with `org.jetbrains.kotlin.plugin.compose`)
- **Roborazzi**: 1.48.0
- **Robolectric**: 4.14.1
- **ktlint**: 12.1.0
- **Spotless**: 6.25.0

### Performance Requirements

- **Target Frame Rate**: 60fps on automotive hardware
- **Memory Efficiency**: Optimized object creation with `remember` caching
- **Recomposition Optimization**: Minimal unnecessary recompositions
- **Touch Response**: < 100ms response time for automotive use

## Build Instructions

### Prerequisites

1. **JDK**: 11 (exactly - NO 17, NO 21)
2. **Android SDK**: API 28+ (compile SDK 34)
3. **Gradle**: 8.14.1 (via `./gradlew` wrapper)
4. **AGP**: 8.2.2 (NO 8.7+)

See [Build System Requirements](#build-system-requirements) for version lock
rationale.

### Building the Library

```bash
./gradlew build        # Build library + demos + run tests (release-only)
```

See [Commands Reference](#commands-reference) for all build and test commands.

## Demo Applications

The library includes **seven** demo modules that showcase integration patterns
for different development approaches. All demos build via `./gradlew build`
(release-only) and share the `demo-shared` module for common content/state.

### Available Demos

| Module                | Language | Purpose                                              |
|-----------------------|----------|------------------------------------------------------|
| `demo-java`           | Java     | Java Custom View integration                         |
| `demo-kotlin`         | Kotlin   | Kotlin language features with Custom Views           |
| `demo-compose`        | Kotlin   | Jetpack Compose declarative UI patterns              |
| `demo-pixel`          | Java     | Pixel-perfect visual regression vs vehicle hardware  |
| `demo-java-cunba`     | Java     | CunBA 3 activation flow (Java)                       |
| `demo-kotlin-cunba`    | Kotlin   | CunBA 3 activation flow (Kotlin)                    |
| `demo-shared`         | Java     | Shared content/state/helpers for all demos           |

### Demo Features

Each demo application demonstrates:

- **7-Tab Navigation Structure**: Language, Theme, Car Type, Climate, Audio,
  Display, System, and Screen Lift
- **Proper Component Hierarchy**: Screen → Panel → Tabs → Section → Radio
- **Automotive UI Patterns**: Optimized for 1920x720 automotive displays
- **Multi-language Support**: English and Russian localization
- **Theme Management**: Dynamic switching between vehicle themes
- **State Management**: Reactive component updates and event handling
- **Performance Optimization**: 60fps rendering for automotive hardware

### Demo Application Structure

All demo applications follow the same BEM co-located test structure:

```
demo-{type}/
└── java/ru/voboost/components/demo/{type}/
    ├── MainActivity.{java|kt}
    ├── MainActivity.tests/
    │   └── MainActivityTestVisual.{java|kt}
    └── MainActivity.screenshots/
        ├── demo_{type}_default.png
        ├── demo_{type}_russian.png
        ├── demo_{type}_dark.png
        ├── demo_{type}_dreamer.png
        └── demo_{type}_full_combination.png
```

### Demo Integration in Development Workflow

The demo applications are:

- **Included in Development**: Part of the main project for continuous validation
- **Excluded from Distribution**: Not included in library artifacts
- **CI/CD Integrated**: Automatically tested in continuous integration
- **Documentation by Example**: Serve as living documentation for integration
  patterns
- **Shared Module**: Common functionality in `demo-shared` module for consistency

## Component Usage

### Component Hierarchy

The library implements a comprehensive component hierarchy optimized for
automotive applications:

```
Screen → Panel → Tabs → Section → Radio
```

### Available Components

- **Screen**: Root container with screen lift functionality
- **Panel**: Content container within the screen
- **Tabs**: 7-tab navigation system
- **Section**: Content section with title
- **Radio**: Interactive radio button group
- **Button / Buttons**: Button and button-group components
- **Checkbox**: Checkbox component
- **Select**: Selection component
- **Text**: Text rendering component
- **Hint**: Hint component
- **Dialog**: Dialog component
- **Popup**: Popup component
- **Toast**: Toast notification component
- **Font**: Font management (co-located `.ttf` assets)

### Radio Component

For comprehensive Radio component documentation including usage examples for
Java, Kotlin and Jetpack Compose projects, see
[Radio.md](src/main/java/ru/voboost/components/radio/Radio.md).

The Radio component supports three usage patterns:

- **Pure Java Projects**: Direct instantiation of Java Custom View
- **Pure Kotlin Projects**: Java Custom View with Kotlin syntax
- **Jetpack Compose Projects**: Kotlin wrapper using AndroidView integration

### Demo-Shared Module

The demo applications share common functionality through the `demo-shared`
module:

- **DemoContent**: Provides 7-tab structure and content
- **DemoState**: Manages application state across all demos
- **DemoHelpers**: Utility functions for demo applications

## Development Guide

### Code Style Requirements

This project follows the voboost-codestyle rules with multilingual support.
Key requirements:

- **Language**: All code comments, documentation and commit messages in
  English only
- **Formatting**: ktlint for Kotlin, Google Java Style for Java with
  voboost-specific rules
- **Architecture**: Java Custom View foundation with Kotlin Compose wrapper
- **Testing**: Minimum 80% code coverage across both Java and Kotlin
  implementations
- **Cross-Platform Compatibility**: Components must work in Java, Kotlin and
  Compose projects

### Coding Standards

- **ALL sizes MUST be in pixels, not dp** — critical automotive requirement for
  precise rendering across different display densities. No dp conversion in
  production code; final values stored as pixels
- **Lowercase hex colors**: ALL `#RRGGBB` colors MUST use lowercase letters
  (e.g., `#f1f5fb`, not `#F1F5FB`)

### Automotive-Specific Constraints

DO NOT implement:

- State persistence (`onSaveInstanceState`/`onRestoreInstanceState`) — fixed
  screen, no rotation
- Accessibility (`contentDescription`, `announceForAccessibility`) — no screen
  readers in vehicle

### Multilingual Component Development Patterns

#### 1. Multilingual Co-Located File Organization

Each component follows the enhanced co-located structure supporting both Java
and Kotlin:

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

- **Java Custom View Foundation**: Implement core functionality in Java
  extending Android View
- **Kotlin Compose Wrapper**: Create lightweight AndroidView integration for
  Compose projects
- **Self-Contained Architecture**: Each component contains only what it needs
- **External Localization**: All strings provided via Map parameters
- **Dynamic Theme Support**: Components support runtime theme switching
- **Performance Optimization**: Optimize for automotive hardware requirements

#### 3. API Design Guidelines

- **Consistent Parameters**: Standardized parameter naming across components
- **Universal Compatibility**: Same functionality across Java, Kotlin and
  Compose usage
- **Reactive Updates**: Components automatically respond to parameter changes
- **Automotive Performance**: Target 60fps rendering and <100ms touch response

### Performance Considerations

For automotive applications, performance is critical:

1. **Object Creation**: Use `remember` to cache expensive operations
2. **Recomposition**: Minimize with proper key usage and state management
3. **Memory Usage**: Avoid memory leaks in long-running automotive applications
4. **Animation**: Use hardware-accelerated animations with spring physics

## Testing Structure

The co-located structure integrates testing directly with components for better
maintainability:

1. **Unit Tests**: Component-specific `Component.test/ComponentTestUnit.java`
   files for logic testing
2. **Visual Tests**: Roborazzi-based `Component.test/ComponentTestVisual.java`
   files for UI regression
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

### Visual Tests Validation (demo-pixel)

**IMPORTANT**: `./gradlew :demo-pixel:test` tests NEVER fail — they always pass
regardless of visual changes. To verify visual correctness:

1. Check the `.txt` files in
   `src/demo-pixel/java/ru/voboost/components/demo/pixel/MainActivity.screenshots/`
2. Look for `PixelComparison: XX.XX% match` at the top of each file
3. **PASS condition**: match percentage is >= 95% (or unchanged from baseline)
4. **FAIL condition**: match percentage drops significantly (visual regression)

**NEVER modify `_1original.png` screenshots** — these are reference images
from real vehicle hardware. Only update `_2actual.png` by re-running tests.

## Commands Reference

### Build

```bash
./gradlew build                 # Build library + demos + run tests (release-only)
./gradlew assembleRelease       # Assemble release variant only
./gradlew buildDemos            # Build all demo applications (release)
./gradlew buildDemoJava         # Build single demo (Java/Kotlin/Compose/Pixel/Cunba)
```

### Tests

```bash
./gradlew test                  # All tests
./gradlew testUnit              # Unit tests
./gradlew testVisual            # Visual tests
./gradlew testJava              # All Java tests
./gradlew testKotlin            # All Kotlin tests
./gradlew testDemos             # Test all demo applications
./gradlew :demo-pixel:test      # Pixel visual regression tests
./gradlew test --tests "*RadioTest*"  # Specific component
```

### Validation & Recording

```bash
./gradlew validate              # All checks (tests + style)
./gradlew validateDemos         # Full demo validation (build + test + screenshots)
./gradlew record                # Record screenshots to BEM structure
./gradlew fix                   # Auto-fix style violations (ktlintFormat + spotlessApply)
```

### Demos

```bash
./gradlew buildDemos            # Build all demos
./gradlew installDemos          # Install all demos to connected device
./gradlew installDemoJava        # Install single demo
./gradlew startDemoJava          # Start demo on connected device
./gradlew cleanDemos             # Clean all demos
```

### Debuggable Release

To produce a debuggable release APK (for development on a device):

```bash
./gradlew :demo-java:installRelease -Pdebuggable=true
```

## Project Structure

This project follows the enhanced co-located methodology adapted for
multilingual components. Each component is organized as a self-contained block
supporting Java, Kotlin and Compose with integrated testing and documentation.

```
voboost-components/
├── build.gradle.kts              # Build configuration (release-only scheme)
├── settings.gradle.kts           # Includes library + 7 demo modules
├── gradle/libs.versions.toml     # Version catalog (AGP, Kotlin, Compose, ...)
├── proguard-rules.pro            # ProGuard rules
├── consumer-rules.pro            # Consumer ProGuard rules
├── lint.xml                      # Lint configuration
├── .editorconfig                 # Symlink to voboost-codestyle/.editorconfig
├── .roorules                     # Project-specific multilingual rules
├── README.md                     # This file
├── AGENTS.md                     # Agent rules (inherits voboost-codestyle)
└── src/
    ├── main/
    │   ├── AndroidManifest.xml
    │   └── java/ru/voboost/components/
    │       ├── theme/                      # Shared theme definitions
    │       │   └── Theme.java             # Theme enum (FREE_LIGHT, FREE_DARK, ...)
    │       ├── i18n/                       # Shared internationalization
    │       │   └── Language.java          # Language enum (EN, RU)
    │       ├── font/                       # Font management (co-located .ttf)
    │       ├── radio/                      # Radio Component Block
    │       ├── button/ buttons/           # Button components
    │       ├── checkbox/                   # Checkbox component
    │       ├── select/                     # Select component
    │       ├── text/                       # Text component
    │       ├── hint/                       # Hint component
    │       ├── dialog/                     # Dialog component
    │       ├── popup/                      # Popup component
    │       ├── toast/                      # Toast component
    │       ├── screen/ panel/ tabs/ section/  # Layout components
    │       └── ...                         # Each block is self-contained
    ├── demo-java/                          # Java demo
    ├── demo-kotlin/                        # Kotlin demo
    ├── demo-compose/                       # Compose demo
    ├── demo-pixel/                         # Pixel-perfect visual regression demo
    ├── demo-java-cunba/                    # CunBA 3 activation (Java)
    ├── demo-kotlin-cunba/                  # CunBA 3 activation (Kotlin)
    └── demo-shared/                        # Shared demo content/state/helpers
```

### Structure Benefits

- **Multilingual Support**: Each component supports Java, Kotlin and Compose
  usage patterns
- **Self-Contained Components**: Each component block contains all related files
  for all platforms
- **Cross-Platform Testing**: Integrated testing across Java Custom View and
  Kotlin Compose wrapper
- **Visual Regression**: Comprehensive screenshots for all themes, languages
  and platforms
- **Component Documentation**: Detailed `.md` files with examples for all usage
  patterns
- **Performance Optimization**: Structure optimized for automotive development
  workflows
- **Maintainability**: Clear organization with comprehensive multilingual
  documentation
- **Universal Compatibility**: Maximum compatibility across all Android
  development approaches

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

When adding new components, follow the enhanced multilingual co-located
structure:

1. **Create Component Block**: New directory under
   `src/main/java/ru/voboost/components/`
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
   - **Documentation**: Create component-specific `.md` files with usage
     examples
   - **Performance**: Validate automotive performance requirements (60fps,
     <100ms touch response)
   - **Visual Testing**: Generate screenshots for all themes and languages
   - **API Consistency**: Ensure consistent functionality across Java, Kotlin
     and Compose usage
   - **Localization Support**: Implement Map-based localization with language
     parameter
   - **Theme Management**: Support dynamic theme switching using shared Theme
     enum

4. **Implementation Workflow**:
   1. **Java Custom View**: Core implementation extending Android View
   2. **Kotlin Wrapper**: AndroidView-based Compose integration
   3. **Testing**: Comprehensive unit and visual tests
   4. **Documentation**: Complete usage examples and API reference
   5. **Validation**: Cross-platform testing and performance verification

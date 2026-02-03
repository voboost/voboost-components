# Java Demo Application

This demo application demonstrates the integration of voboost-components library in a pure Java Android project using traditional Android development approaches.

## Purpose

This demo showcases:
- **Java Custom View Integration**: How to use voboost-components in traditional Java Android projects
- **Automotive UI Patterns**: Best practices for automotive screen layouts and interactions
- **State Management**: Reactive component updates and event handling in Java
- **Performance Optimization**: Efficient rendering for automotive hardware requirements

## Architecture Principles

- **Pure Java Implementation**: Uses Java Custom View components directly without Kotlin wrappers
- **Automotive-First Design**: Optimized for 1920x720 automotive displays with appropriate touch targets
- **Reactive State Management**: Centralized state with automatic component updates
- **Performance Focused**: 60fps rendering with minimal memory footprint

## Building and Running

### Prerequisites
- Android SDK with API level 28 or higher
- Java 17 or higher
- Android device or emulator with automotive profile

### Build Commands

```bash
# Build the Java demo
./gradlew :demo-java:assembleDebug

# Install to connected device
./gradlew :demo-java:installDebug

# Build using main project convenience tasks
./gradlew buildDemoJava
./gradlew installDemoJava
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
./gradlew :demo-java:testDebugUnitTest
```

### Visual Regression Tests

Visual tests are co-located with the MainActivity following BEM structure:

```
java/ru/voboost/components/demo/java/
├── MainActivity.java
├── MainActivity.tests/
│   └── MainActivityTestVisual.java
└── MainActivity.screenshots/
    ├── demo_java_default.png
    ├── demo_java_russian.png
    ├── demo_java_dark.png
    ├── demo_java_dreamer.png
    └── demo_java_full_combination.png
```

Standard test scenarios:
1. **demo_java_default** - Default state: English, Light, Free
2. **demo_java_russian** - Russian language: Russian, Light, Free
3. **demo_java_dark** - Dark theme: English, Dark, Free
4. **demo_java_dreamer** - Dreamer car type: English, Light, Dreamer
5. **demo_java_full_combination** - Full combination: Russian, Dark, Dreamer

```bash
# Run visual tests
./gradlew :demo-java:testDebugUnitTest --tests "ru.voboost.components.demo.java.MainActivityTestVisual"

# Record new screenshots
./gradlew :demo-java:recordDemoScreenshots

# Verify against existing screenshots
./gradlew :demo-java:verifyDemoScreenshots
```

### Complete Validation
```bash
# Full validation (build + test + screenshots)
./gradlew validateDemosComplete
```

## Running the Demo

1. **Build and Install**:
   ```bash
   ./gradlew installDemoJava
   ```

2. **Launch**: Find "Java Demo" app on your device/emulator

3. **Test Functionality**:
   - Interact with components to see reactive updates
   - Test language switching and theme changes
   - Verify automotive-appropriate touch targets and spacing
   - Test the Test Radio component with animation options (Close, Normal, Sync with Music, Sync with Driving)

## Integration Example

Basic integration pattern for Java projects:

```java
import ru.voboost.components.radio.Radio;
import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// In your Activity
Radio radio = findViewById(R.id.radio_component);

// Create radio buttons with localization maps
List<RadioButton> buttons = new ArrayList<>();
Map<String, String> option1Labels = new HashMap<>();
option1Labels.put("en", "Option 1");
option1Labels.put("ru", "Вариант 1");
buttons.add(new RadioButton("option1", option1Labels));

Map<String, String> option2Labels = new HashMap<>();
option2Labels.put("en", "Option 2");
option2Labels.put("ru", "Вариант 2");
buttons.add(new RadioButton("option2", option2Labels));

// Set language and theme first (required initialization)
radio.setLanguage(Language.fromCode("en"));
radio.setTheme(Theme.fromValue("free-light"));

// Set buttons and initial selection
radio.setButtons(buttons);
radio.setSelectedValue("option1");

// Set value change listener
radio.setOnValueChangeListener(newValue -> {
    // Handle selection change
    updateApplicationState(newValue);
});
```

## Essential Methods

### Initialization Methods
```java
// Set language using Language enum (required)
radio.setLanguage(Language.fromCode("en"));  // or Language.EN

// Set theme using Theme enum (required)
radio.setTheme(Theme.fromValue("free-light"));  // or Theme.FREE_LIGHT

// Set radio buttons with localization maps
radio.setButtons(buttonsList);

// Set initial selection
radio.setSelectedValue("option1");

// Set value change listener
radio.setOnValueChangeListener(newValue -> {
    // Handle selection change
});
```

### Proper Initialization Sequence
1. Create Radio component instance
2. Set language using `setLanguage(Language.fromCode("en"))`
3. Set theme using `setTheme(Theme.fromValue("free-light"))`
4. Set buttons using `setButtons(buttonsList)`
5. Set initial selection using `setSelectedValue("value")`
6. Set value change listener using `setOnValueChangeListener()`

### Creating RadioButton Objects
```java
// Create a RadioButton with localization support
Map<String, String> labels = new HashMap<>();
labels.put("en", "English Text");
labels.put("ru", "Русский Текст");
RadioButton button = new RadioButton("unique_value", labels);
```

## Performance Characteristics

- **Target**: 60fps rendering on automotive hardware
- **Memory**: Optimized for multiple component instances
- **Touch Response**: Sub-100ms response times
- **Screen Resolution**: Native 1920x720 automotive display support

## Troubleshooting

### Build Issues
- Verify Java 17 configuration
- Ensure Android SDK API 28+ is installed
- Check voboost-components library dependency

### Runtime Issues
- **Initialization Order**: Always set language and theme before setting buttons
- **Null Parameters**: Language and Theme cannot be null - use Language.fromCode() and Theme.fromValue()
- **RadioButton Creation**: Ensure all RadioButton objects have non-empty label maps
- **Component Not Rendering**: Verify both language and theme are set before calling setButtons()

### Common Configuration Issues
```java
// ❌ INCORRECT - Missing language/theme initialization
radio.setButtons(buttons);
radio.setSelectedValue("option1");

// ✅ CORRECT - Proper initialization sequence
radio.setLanguage(Language.EN);
radio.setTheme(Theme.FREE_LIGHT);
radio.setButtons(buttons);
radio.setSelectedValue("option1");
```

### Screenshot Test Issues
- Ensure consistent test environment
- Verify Roborazzi automotive resolution configuration
- Check screenshot directory permissions

This demo serves as a comprehensive reference for integrating voboost-components into Java Android projects with automotive requirements.

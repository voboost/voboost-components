# Voboost Components Library - Project Intelligence

## Global Rules (CRITICAL)
- This project follows ALL common rules from ../voboost-codestyle/AGENTS.md
- The rules below are PROJECT-SPECIFIC additions to the global rules
- NEVER duplicate global rules here - they are inherited automatically

## Project-Specific Patterns

## BEM Architecture Integration
- **Co-located test files**: `*.test-unit.java`, `*.test-visual.java`
- **Component structure**: mirrors Kotlin BEM patterns
- **Shared screenshot directories**: with Kotlin tests
- **File naming**: `ComponentName.java`, `ComponentNameButton.java`, `ComponentNameTheme.java`

## Java Custom View Patterns
- **Extend View or ViewGroup** for custom components
- **Constructor patterns**: Support all 4 Android constructors
- **State management**: Implement onSaveInstanceState/onRestoreInstanceState
- **Performance**: Optimize onDraw and onMeasure for automotive hardware

## Java-Kotlin Interoperability Rules
- **Primary implementation**: Java Custom View contains all logic
- **Kotlin wrapper**: Lightweight AndroidView integration only
- **Data sharing**: Reuse Kotlin data classes where possible
- **Theme consistency**: Java components use same theme values as Kotlin
- **API compatibility**: Both implementations expose similar public APIs

### Component Library Architecture
- Reusable UI component library for Voyah vehicle applications
- Jetpack Compose components with automotive-grade design system
- Multi-theme support: Free/Dreamer models with Light/Dark variants
- Standalone library with minimal external dependencies
- Android 9 and Android 11 compatibility for automotive systems

### Technology Stack
- Kotlin/Android with Jetpack Compose for modern UI components
- Material Design 3 foundation with automotive customizations
- Theme system supporting multiple vehicle models and color schemes
- Component-based architecture with clear separation of concerns
- Performance-optimized for automotive hardware constraints

### Self-Contained Component Architecture (CRITICAL)
- **NO shared Component.kt** - each component is self-sufficient
- **NO shared i18n system** - each component receives localized strings externally
- **NO shared theme system** - theme is passed during component creation and can be changed dynamically
- **NO shared utils** - each component contains only the utilities it needs
- **External localization strategy** - all strings passed from outside during component initialization
- **Dynamic theme and language support** - components must recompose reactively when theme or lang parameters change
- **Map-based translations** - components use `label: Map<String, String>` where key is language code
- **NO fallback control** - component uses whatever Map is provided, does not control fallback logic

### Component Design Patterns
- **Self-Contained Structure**: Each component in its own directory with all required files
- **Simplified File Structure**: Minimal files per component (e.g., RadioButton.kt, RadioTheme.kt, Radio.kt)
- **State Management**: Consistent state handling across all components
- **Reactive Recomposition**: Components observe parameter changes and trigger recomposition automatically

### Multi-Theme Architecture
- **Dynamic Theming**: Runtime theme switching without component recreation
- **Internal Theme Management**: Each component stores its theme constants internally
- **Theme Parameter**: Theme passed as parameter ("free-light", "free-dark", "dreamer-light", "dreamer-dark")
- **Reactive Theme Updates**: Components recompose when theme parameter changes
- **Consistent Branding**: Model-specific visual identity maintained across components

## Critical Implementation Details

### Component API Design
- **Consistent Parameters**: Standardized parameter naming and ordering across components
- **Minimal Required Props**: Only essential parameters required, sensible defaults provided
- **Composable Functions**: All components as pure Composable functions
- **State Hoisting**: Component state managed by parent when needed
- **Event Handling**: Consistent callback patterns across all components

### Performance Rules (CRITICAL for Automotive)
- **Component Caching**: Use `remember` for expensive component initialization
- **Recomposition Optimization**: Minimize unnecessary recompositions through proper key usage
- **Memory Efficiency**: Avoid object creation in Composable body - cache with `remember`
- **Render Performance**: Optimize for 60fps on automotive hardware
- **Resource Management**: Efficient handling of fonts, colors, and drawable resources

## Development Workflow (Project-Specific)
- **Component Development**: Each component in separate file with clear naming
- **Preview Support**: All components must have `@Preview` functions for development
- **Documentation**: Comprehensive KDoc for all public APIs
- **Testing Strategy**: Unit tests for component logic, screenshot tests for UI
- **Version Management**: Semantic versioning for library releases
- **Backward Compatibility**: Maintain API compatibility across minor versions

## Quality Standards
- **Code Coverage**: Minimum 80% test coverage for component logic
- **Performance Benchmarks**: Components must meet automotive performance standards
- **Multi-Theme Testing**: Components tested across all theme combinations
- **Device Testing**: Verified on Android 9 and 11 automotive systems

## Key Success Factors
- **Reusable Design System**: Consistent components across all Voyah applications
- **Automotive Performance**: Optimized for in-vehicle hardware and usage patterns
- **Theme Flexibility**: Seamless adaptation to Free/Dreamer models and light/dark themes
- **Developer Experience**: Easy-to-use APIs with comprehensive documentation
- **Maintenance Efficiency**: Clean architecture enabling easy updates and extensions

## Self-Contained Component Structure Requirements

### Radio Component Structure (Reference Implementation)
```
radio/
├── RadioButton.kt          # Data model with text rendering logic
├── RadioTheme.kt           # Combined colors, themes, and dimensions
├── Radio.kt                # Main component implementation
├── Radio.test-unit.kt      # Co-located unit tests
├── Radio.test-visual.kt    # Co-located visual tests
└── Radio.screenshots/      # Visual test screenshots
```

### BEM Co-Located Test Structure (CRITICAL)
- **Test Location**: Tests MUST be co-located with components in `src/main/java/ru/voboost/components/[component]/`
- **Test Naming**: Use pattern `[Component].test-[type].kt` (e.g., `Radio.test-unit.kt`, `Radio.test-visual.kt`)
- **Test Types**:
  - `*.test-unit.kt` - Unit tests for component logic and behavior
  - `*.test-visual.kt` - Visual regression tests using Paparazzi
- **Screenshot Storage**: Visual test screenshots in `[Component].screenshots/` directory
- **NO Traditional Test Directories**: Do NOT use `src/test/java/` or `__tests__/` subdirectories
- **Build Configuration**: Tests run from main source set with proper sourceSets configuration
```

### Component API Requirements
- **Component Signature**: `fun Radio(buttons: List<RadioButton>, lang: String, theme: String, value: String, onValueChange: (String) -> Unit)`
- **Data Model**: `data class RadioButton(val value: String, val label: Map<String, String>)`
- **NO Modifier Parameter**: Components should not accept Modifier parameters
- **External Localization**: All strings provided through Map, no internal string resources
- **Dynamic Parameters**: Both lang and theme parameters can change during component lifecycle

### Reactive Behavior Requirements
- **Language Changes**: Component observes lang parameter and uses new Map key automatically
- **Theme Changes**: Component observes theme parameter and selects new colors/dimensions
- **Automatic Recomposition**: Components trigger recomposition on parameter changes
- **NO External Dependencies**: Components work independently without shared systems

## Implementation Patterns Discovered
- **Self-Contained Architecture**: Each component fully independent with internal theme/localization
- **Unified Theme Objects**: Single theme object combining colors, dimensions, and theme logic
- **External String Provision**: All localized strings provided externally through Map structures
- **Reactive Parameter Handling**: Components automatically respond to lang/theme parameter changes
- **Simplified File Structure**: Minimal files per component for maintainability
- **Performance Optimization**: Strategic use of `remember` for automotive performance
- **API Consistency**: Standardized patterns across all component APIs
- **Preview System**: Comprehensive preview support for development efficiency
- **Resource Management**: Efficient handling of automotive-specific resources

## Multilingual Architecture (CRITICAL)

### Java Custom View + Kotlin Wrapper Pattern
- **Foundation Layer**: Java Custom View extends Android View with complete implementation
- **Integration Layer**: Kotlin Compose wrapper using AndroidView for seamless Compose integration
- **Universal Compatibility**: Single codebase supporting Java, Kotlin, and Jetpack Compose projects
- **Performance Optimized**: Java Custom View provides maximum performance for automotive hardware

### Multilingual Component Structure
```
radio/
├── Radio.java              # Primary Java Custom View implementation (foundation)
├── Radio.kt                # Kotlin Compose wrapper using AndroidView (integration)
├── RadioButton.java        # Java data model with localization support
├── RadioButton.kt          # Kotlin data model (shared/interoperable)
├── RadioTheme.java         # Java theme constants and color management
├── RadioTheme.kt           # Kotlin theme (shared/interoperable)
├── Radio.test-unit.java    # Java unit tests for Custom View logic
├── Radio.test-visual.java  # Java visual tests for Custom View rendering
├── Radio.test-unit.kt      # Kotlin unit tests for Compose wrapper
├── Radio.test-visual.kt    # Kotlin visual tests for Compose integration
├── Radio.test/             # BEM co-located test directory (alternative structure)
│   ├── RadioTestUnit.java  # Java unit tests
│   ├── RadioTestVisual.java # Java visual tests
│   ├── RadioTestUnit.kt    # Kotlin unit tests
│   └── RadioTestVisual.kt  # Kotlin visual tests
├── Radio.md                # Comprehensive multilingual documentation
└── Radio.screenshots/      # Shared visual test results
```

### Java Custom View Implementation Requirements (CRITICAL)
- **Complete Android View Implementation**: All 4 constructors, proper lifecycle management
- **Canvas-Based Rendering**: Custom drawing with theme-aware gradients and animations
- **Touch Event Handling**: Automotive-optimized touch targets and gesture recognition
- **Animation System**: ValueAnimator with DecelerateInterpolator for smooth transitions
- **Theme Management**: Dynamic theme switching with immediate visual updates
- **Localization Support**: Map-based string handling with language parameter
- **State Persistence**: Proper onSaveInstanceState/onRestoreInstanceState implementation
- **Performance Optimization**: Efficient onDraw and onMeasure for 60fps automotive rendering

### Kotlin Compose Wrapper Requirements (CRITICAL)
- **AndroidView Integration**: Seamless embedding of Java Custom View in Compose hierarchy
- **Reactive Parameter Handling**: Automatic updates when Compose state changes
- **Compose Lifecycle Integration**: Proper factory and update block implementation
- **State Synchronization**: Bidirectional state flow between Compose and Custom View
- **API Consistency**: Composable function signature matching Compose patterns
- **Recomposition Optimization**: Efficient updates without unnecessary recreations

### Cross-Platform Usage Patterns
- **Pure Java Projects**: Direct instantiation and configuration of Java Custom View
- **Pure Kotlin Projects**: Java Custom View with Kotlin syntax and language features
- **Jetpack Compose Projects**: Kotlin wrapper with full Compose state management
- **Mixed Projects**: Consistent API across all implementation approaches
- **Legacy Integration**: Backward compatibility with existing Android View systems

### BEM Co-Located Test Structure (Enhanced)
- **Dual Language Testing**: Both Java and Kotlin test implementations
- **Test Location Flexibility**: Support both flat structure and Radio.test/ directory
- **Test Naming Conventions**:
  - Flat: `Radio.test-unit.java`, `Radio.test-visual.kt`
  - Directory: `Radio.test/RadioTestUnit.java`, `Radio.test/RadioTestVisual.kt`
- **Cross-Language Test Coverage**: Java tests for Custom View, Kotlin tests for Compose wrapper
- **Shared Test Resources**: Common test data and screenshots across implementations
- **Visual Regression Testing**: Paparazzi for Compose, custom screenshot comparison for Java

## Multilingual Naming Conventions (CRITICAL)

### Java Custom View Naming
- **Class Names**: PascalCase following Android View conventions (`Radio`, `RadioButton`, `RadioTheme`)
- **Method Names**: camelCase following Java conventions (`setButtons`, `getSelectedValue`, `setOnValueChangeListener`)
- **Field Names**: camelCase with descriptive names (`selectedValue`, `animatedX`, `currentTheme`)
- **Constants**: UPPER_SNAKE_CASE in theme classes (`ANIMATION_DURATION`, `CORNER_RADIUS`)

### Kotlin Wrapper Naming
- **Composable Functions**: PascalCase matching component name (`Radio`, `RadioButton`)
- **Parameters**: camelCase following Compose conventions (`buttons`, `lang`, `theme`, `value`, `onValueChange`)
- **Data Classes**: PascalCase with descriptive names (`RadioButton`, `RadioColors`)
- **Extension Functions**: camelCase with clear intent (`getText`, `getColors`)

### File Naming Patterns
- **Java Files**: PascalCase.java (`Radio.java`, `RadioButton.java`, `RadioTheme.java`)
- **Kotlin Files**: PascalCase.kt (`Radio.kt`, `RadioButton.kt`, `RadioTheme.kt`)
- **Test Files**: Component.test-type.extension (`Radio.test-unit.java`, `Radio.test-visual.kt`)
- **Test Directory**: Component.test/ (`Radio.test/RadioTestUnit.java`)
- **Documentation**: Component.md (`Radio.md`)

### AndroidView Wrapper Patterns (CRITICAL)
- **Factory Block**: Create and configure Java Custom View with initial parameters
- **Update Block**: Handle parameter changes and state synchronization
- **State Management**: Proper handling of Compose state changes
- **Lifecycle Integration**: Respect Compose recomposition lifecycle
- **Performance**: Minimize object creation in update block

```kotlin
// AndroidView wrapper pattern
AndroidView(
    factory = { context ->
        Radio(context).apply {
            // Initial configuration
            setButtons(buttons)
            setLanguage(lang)
            setTheme(theme)
            setSelectedValue(value)
            setOnValueChangeListener { newValue ->
                onValueChange(newValue)
            }
        }
    },
    update = { radioView ->
        // Handle parameter changes
        radioView.setButtons(buttons)
        radioView.setLanguage(lang)
        radioView.setTheme(theme)
        radioView.setSelectedValue(value)
    }
)
```

## Testing Commands (CRITICAL)
- **Visual Tests**: Use `./gradlew testVisual` for running visual regression tests
- **Visual Tests with Save**: Use `./gradlew testVisualSave` for running and saving visual test results
- **Unit Tests**: Use `./gradlew testUnit` for running unit tests
- **All Tests**: Use `./gradlew test` for running all tests

### Java Testing Commands
- **Java Unit Tests**: Use `./gradlew testJavaUnit` for running Java unit tests
- **Java Visual Tests**: Use `./gradlew testJavaVisual` for running Java visual tests
- **Java Code Style**: Use `./gradlew checkJavaStyle` for Java style validation
- **Java Code Format**: Use `./gradlew formatJavaCode` for Java code formatting
- **Complete Java Validation**: Use `./gradlew validateJava` for full Java validation

### Cross-Platform Testing Commands
- **All Java Tests**: Use `./gradlew testJava` for running all Java tests (unit + visual)
- **All Kotlin Tests**: Use `./gradlew testKotlin` for running all Kotlin tests (unit + visual)
- **Cross-Platform Validation**: Use `./gradlew validateAll` for complete validation across languages
- **Performance Tests**: Use `./gradlew testPerformance` for automotive performance validation

## Demo Application Test Structure

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

Standard test scenarios for all demos:
1. Default state - English, Light, Free
2. Russian language - Russian, Light, Free
3. Dark theme - English, Dark, Free
4. Dreamer car type - English, Light, Dreamer
5. Full combination - Russian, Dark, Dreamer

## Demo Application Commands (CRITICAL)
- **Build Demo Java**: Use `./gradlew buildDemoJava` for building demo-java application
- **Install Demo Java**: Use `./gradlew installDemoJava` for installing demo-java application
- **Start Demo Java**: Use `./gradlew startDemoJava` for starting demo-java application
- **Complete Demo Java Workflow**: Use `./gradlew buildDemoJava && ./gradlew installDemoJava && ./gradlew startDemoJava` for full build, install, and start sequence

## Coding Standards (CRITICAL)

### Dimension Standards
- **ALL sizes MUST be in pixels, not dp** - This is a critical automotive requirement for precise rendering
- **Pixel-based measurements**: All component dimensions, margins, paddings must use pixel values
- **No dp conversion in production**: dp to pixel conversion only for internal calculations, final values stored as pixels
- **Consistent sizing**: Ensures identical rendering across different automotive display densities

### Color Format Standards
- **Lowercase hex colors**: ALL #RRGGBB colors MUST use lowercase letters (e.g., #ffffff, not #FFFFFF)
- **Consistent color formatting**: Applies to all theme definitions, color constants, and inline color values
- **Example**: Use `#f1f5fb` instead of `#F1F5FB`

# Radio Component

## Architecture

The Radio component has two implementations:

1. **[`Radio.java`](Radio.java)** - Java Custom View - core implementation with canvas rendering, animations, state persistence
2. **[`Radio.kt`](Radio.kt)** - Kotlin Compose wrapper - lightweight AndroidView integration

The Java implementation handles all rendering, animations, state persistence, and logic. The Kotlin wrapper provides seamless Compose integration via AndroidView.

## Quick Start

### Java Projects

```java
Radio radio = new Radio(context);

List<RadioButton> buttons = Arrays.asList(
    new RadioButton("en", Map.of("en", "English", "ru", "Английский")),
    new RadioButton("ru", Map.of("en", "Russian", "ru", "Русский"))
);

radio.setButtons(buttons);
radio.setLanguage("en");
radio.setTheme("free-light");
radio.setSelectedValue("en");
radio.setOnValueChangeListener(newValue -> {
    // Handle selection change
});

parentLayout.addView(radio);
```

### Kotlin Projects

```kotlin
val radio = Radio(context).apply {
    val buttons = listOf(
        RadioButton("light", mapOf("en" to "Light", "ru" to "Светлая")),
        RadioButton("dark", mapOf("en" to "Dark", "ru" to "Тёмная"))
    )

    setButtons(buttons)
    setLanguage("en")
    setTheme("free-light")
    setSelectedValue("light")
    setOnValueChangeListener { newValue ->
        // Handle selection change
    }
}

parentLayout.addView(radio)
```

### Jetpack Compose Projects

```kotlin
import ru.voboost.components.radio.Radio
import ru.voboost.components.radio.RadioButton
import ru.voboost.components.theme.Theme
import ru.voboost.components.i18n.Language

@Composable
fun MyScreen() {
    var selectedValue by remember { mutableStateOf("en") }

    Radio(
        buttons = listOf(
            RadioButton("en", mapOf("en" to "English", "ru" to "Английский")),
            RadioButton("ru", mapOf("en" to "Russian", "ru" to "Русский"))
        ),
        lang = Language.EN,
        theme = Theme.FREE_LIGHT,
        value = selectedValue,
        onValueChange = { selectedValue = it }
    )
}
```

## API Reference

### Java Custom View API

#### Constructors
```java
public Radio(Context context)
public Radio(Context context, AttributeSet attrs)
public Radio(Context context, AttributeSet attrs, int defStyleAttr)
public Radio(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
```

#### Methods
```java
// Configuration
public void setButtons(List<RadioButton> buttons)
public void setLanguage(String language)  // "en", "ru"
public void setLanguage(Language language) // Language enum
public void setTheme(String theme)        // "free-light", "free-dark", "dreamer-light", "dreamer-dark"
public void setTheme(Theme theme)         // Theme enum

// Value management
public void setSelectedValue(String value)
public void setSelectedValue(String value, boolean isTriggerCallback)  // NEW
public String getSelectedValue()

// Event handling
public void setOnValueChangeListener(OnValueChangeListener listener)

// State persistence (automatic - called by Android framework)
@Override
protected Parcelable onSaveInstanceState()
@Override
protected void onRestoreInstanceState(Parcelable state)

// Listener interface
public interface OnValueChangeListener {
    void onValueChange(String newValue);
}
```

### State Persistence

The Radio component automatically saves and restores its state during configuration changes (e.g., screen rotation). The following state is preserved:

- Selected value
- Current theme
- Current language
- Button list

No additional code is required - the component handles state persistence automatically through the Android View state mechanism.

### Kotlin Compose API

```kotlin
@Composable
fun Radio(
    buttons: List<RadioButton>,
    lang: Language,
    theme: Theme,
    value: String,
    onValueChange: (String) -> Unit,
    onViewCreated: ((ru.voboost.components.radio.Radio) -> Unit)? = null  // NEW
)
```

### RadioButton Data Model

```java
public class RadioButton {
    public RadioButton(String value, Map<String, String> label)
    public String getText(String lang)
    public String getValue()
    public Map<String, String> getLabel()
}
```

## Themes

Available themes:
- `"free-light"` - Light theme with blue gradient
- `"free-dark"` - Dark theme with blue gradient
- `"dreamer-light"` - Light theme with brown gradient
- `"dreamer-dark"` - Dark theme with brown gradient

## Localization

The component uses external localization through `Map<String, String>`:

```java
RadioButton button = new RadioButton("value", Map.of(
    "en", "English Text",
    "ru", "Русский текст"
));
```

Language is set via `setLanguage("en")` or `lang` parameter in Compose.

## Complete Examples

### Java Example

```java
public class MainActivity extends AppCompatActivity {
    private Radio languageRadio;
    private String currentLanguage = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languageRadio = new Radio(this);
        setupRadio();

        LinearLayout layout = findViewById(R.id.main_layout);
        layout.addView(languageRadio);
    }

    private void setupRadio() {
        List<RadioButton> buttons = Arrays.asList(
            new RadioButton("en", Map.of("en", "English", "ru", "Английский")),
            new RadioButton("ru", Map.of("en", "Russian", "ru", "Русский"))
        );

        languageRadio.setButtons(buttons);
        languageRadio.setLanguage(currentLanguage);
        languageRadio.setTheme("free-light");
        languageRadio.setSelectedValue(currentLanguage);

        languageRadio.setOnValueChangeListener(newValue -> {
            currentLanguage = newValue;
            languageRadio.setLanguage(currentLanguage);
        });
    }
}
```

### Kotlin Example

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var themeRadio: Radio
    private var currentTheme = "light"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeRadio = Radio(this).apply {
            val buttons = listOf(
                RadioButton("light", mapOf("en" to "Light", "ru" to "Светлая")),
                RadioButton("dark", mapOf("en" to "Dark", "ru" to "Тёмная"))
            )

            setButtons(buttons)
            setLanguage("en")
            setTheme("dreamer-light")
            setSelectedValue("light")

            setOnValueChangeListener { newValue ->
                currentTheme = newValue
                setTheme("dreamer-$newValue")
            }
        }

        val layout = findViewById<LinearLayout>(R.id.main_layout)
        layout.addView(themeRadio)
    }
}
```

### Compose Example

```kotlin
import ru.voboost.components.radio.Radio
import ru.voboost.components.radio.RadioButton
import ru.voboost.components.theme.Theme
import ru.voboost.components.i18n.Language

@Composable
fun RadioDemo() {
    var selectedLanguage by remember { mutableStateOf(Language.EN) }
    var isDarkTheme by remember { mutableStateOf(false) }
    var isDreamer by remember { mutableStateOf(false) }

    // Compute theme based on selections
    val currentTheme = when {
        isDreamer && isDarkTheme -> Theme.DREAMER_DARK
        isDreamer && !isDarkTheme -> Theme.DREAMER_LIGHT
        !isDreamer && isDarkTheme -> Theme.FREE_DARK
        else -> Theme.FREE_LIGHT
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Language selection
        Radio(
            buttons = listOf(
                RadioButton("en", mapOf("en" to "English", "ru" to "Английский")),
                RadioButton("ru", mapOf("en" to "Russian", "ru" to "Русский"))
            ),
            lang = selectedLanguage,
            theme = currentTheme,
            value = selectedLanguage.getCode(),
            onValueChange = { selectedLanguage = Language.fromCode(it) }
        )

        // Theme selection (light/dark)
        Radio(
            buttons = listOf(
                RadioButton("light", mapOf("en" to "Light", "ru" to "Светлая")),
                RadioButton("dark", mapOf("en" to "Dark", "ru" to "Тёмная"))
            ),
            lang = selectedLanguage,
            theme = currentTheme,
            value = if (isDarkTheme) "dark" else "light",
            onValueChange = { isDarkTheme = (it == "dark") }
        )

        // Car type selection
        Radio(
            buttons = listOf(
                RadioButton("free", mapOf("en" to "Free", "ru" to "Фри")),
                RadioButton("dreamer", mapOf("en" to "Dreamer", "ru" to "Дример"))
            ),
            lang = selectedLanguage,
            theme = currentTheme,
            value = if (isDreamer) "dreamer" else "free",
            onValueChange = { isDreamer = (it == "dreamer") }
        )
    }
}
```

## Implementation Details

### Animation System

The component uses `ValueAnimator` with `OvershootInterpolator`:
- Duration: 400ms
- Tension: 1.0f
- Properties: X position and width of selected background
- Overshoot effect: Creates a subtle bounce when animation reaches target
- Hardware acceleration enabled for smooth rendering

### Canvas Rendering

- Background: Rounded rectangle with theme-specific colors
- Selected background: Linear gradient (vertical for "free", horizontal for "dreamer")
- Border: Multi-color gradient for "free" themes only
- Text: Centered with theme-specific colors

### Touch Handling

Touch events are handled in `onTouchEvent()`:
1. Calculate which item was touched based on X coordinate
2. Update selected value if different from current
3. Trigger animation and callback

### Performance

- Hardware acceleration enabled via `setLayerType(LAYER_TYPE_HARDWARE, null)`
- Efficient text measurement and caching
- Animation cleanup in `onDetachedFromWindow()`

## Testing

The component includes comprehensive tests with expanded coverage:

- **Java Unit tests**: [`Radio.test/RadioTestUnit.java`](Radio.test/RadioTestUnit.java) - 23 test methods covering initialization, state management, callbacks, performance, thread safety, accessibility, and edge cases
- **Java Visual tests**: [`Radio.test/RadioTestVisual.java`](Radio.test/RadioTestVisual.java) - visual regression tests using Roborazzi with comprehensive animation frame capture
- **Screenshots**: [`Radio.screenshots/`](Radio.screenshots/) directory - 70 screenshots covering all themes, languages, and animation states

### Unit Test Coverage

The unit test suite includes 23 test methods covering:

#### Core Functionality (8 tests)
- `testRadioInitialization` - Verifies proper component initialization
- `testSetButtons` - Tests button configuration and measurement
- `testSetValue` - Tests value selection and retrieval
- `testSetInvalidValue` - Tests handling of invalid values
- `testSetTheme` - Tests theme setting with getter verification
- `testSetLanguage` - Tests language setting with getter verification
- `testValueChangeCallback` - Tests callback invocation with trigger flag
- `testStatePersistence` - Tests state save/restore functionality

#### Edge Cases and Error Handling (6 tests)
- `testNullButtons` - Tests null button list handling
- `testEmptyButtons` - Tests empty button list handling
- `testStatePersistenceWithNullState` - Tests null state restoration
- `testFourthConstructor` - Tests 4-parameter constructor
- `testSetNullTheme` - Tests null theme exception
- `testSetNullLanguage` - Tests null language exception

#### Performance and Reliability (6 tests)
- `testUninitializedRadioDoesNotDraw` - Tests uninitialized component behavior
- `testAllThemeValues` - Tests all theme enum values
- `testAllLanguageValues` - Tests all language enum values
- `testPerformanceOptimization` - Tests hardware acceleration and rapid changes
- `testMemoryLeakPrevention` - Tests animation cleanup on detach
- `testThreadSafety` - Tests concurrent access safety

#### Advanced Functionality (4 tests)
- `testAnimationBehavior` - Tests animation state management
- `testTouchEventHandling` - Tests touch event processing
- `testAccessibilityFeatures` - Tests accessibility support
- `testMeasurement` - Tests component measurement with automotive dimensions

### Visual Testing Architecture

Visual tests are Java-only because:
- The Kotlin wrapper is a lightweight AndroidView integration
- Java visual tests provide complete coverage of the core rendering logic
- Both implementations produce identical screenshots since the wrapper delegates to the Java view
- AndroidView integration testing adds minimal value for this wrapper pattern

### Animation Testing

The visual test suite includes comprehensive animation testing:

#### Animation Frame Capture
- Tests animation at every 5% progress interval (0% to 110%)
- Captures overshoot animation behavior (100-110%)
- Tests all three animation directions: forward, reverse, and short distance
- Covers all 4 themes with consistent naming convention

#### Animation Test Categories
- **Static Tests** (48 screenshots): Theme/language/selection combinations
- **Animation Tests** (22 screenshots): Animation transitions and bounds
- **Total Coverage**: 70 screenshots for complete visual verification

### Run Tests

```bash
./gradlew testUnit        # All unit tests (23 test methods)
./gradlew testVisual      # All visual tests (Java-only)

./gradlew testUnitJava    # Java unit tests
./gradlew testVisualJava  # Java visual tests

./gradlew testVisualSave  # Record new screenshots
```

## Demo Applications

Three complete demo applications are available:

1. **Java Demo**: [`src/demo-java/`](../../demo-java/) - Pure Java implementation
2. **Kotlin Demo**: [`src/demo-kotlin/`](../../demo-kotlin/) - Kotlin with Java Custom View
3. **Compose Demo**: [`src/demo-compose/`](../../demo-compose/) - Jetpack Compose integration

Each demo shows language switching, theme selection, and state management patterns.

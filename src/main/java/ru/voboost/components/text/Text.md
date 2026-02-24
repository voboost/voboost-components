# Text Component

## Architecture

The Text component has two implementations:

1. **[`Text.java`](Text.java)** - Java Custom View - core implementation with canvas rendering, theme management, and localization support
2. **[`Text.kt`](Text.kt)** - Kotlin Compose wrapper - lightweight AndroidView integration

The Java implementation handles all rendering, theme management, localization, and positioning logic. The Kotlin wrapper provides seamless Compose integration via AndroidView.

## Overview

The Text component provides a unified text rendering system for the Voboost component library. It works both as a standalone view and for custom positioning within parent components. The component supports multiple text roles, dynamic theming, and localization using Map<Language, String>.

## Quick Start

### Java Projects

```java
// Create text with static string
Text text = new Text(context);
text.setText("Hello World");
text.setTheme(Theme.FREE_LIGHT);
text.setRole(TextRole.CONTROL);

// Create text with localization
Map<Language, String> localizedText = Map.of(
    Language.EN, "Settings",
    Language.RU, "Настройки"
);
Text localizedTextComponent = new Text(context);
localizedTextComponent.setText(localizedText);
localizedTextComponent.setLanguage(Language.EN);
localizedTextComponent.setTheme(Theme.FREE_LIGHT);
localizedTextComponent.setRole(TextRole.TITLE);

// Add to parent layout
parentLayout.addView(text);
```

### Kotlin Projects

```kotlin
// Create text with static string
val text = Text(context).apply {
    setText("Hello World")
    setTheme(Theme.FREE_LIGHT)
    setRole(TextRole.CONTROL)
}

// Create text with localization
val localizedText = mapOf(
    Language.EN to "Settings",
    Language.RU to "Настройки"
)
val localizedTextComponent = Text(context).apply {
    setText(localizedText)
    setLanguage(Language.EN)
    setTheme(Theme.FREE_LIGHT)
    setRole(TextRole.TITLE)
}

// Add to parent layout
parentLayout.addView(text)
```

### Jetpack Compose Projects

```kotlin
import ru.voboost.components.text.Text
import ru.voboost.components.text.TextData
import ru.voboost.components.text.TextRole
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

@Composable
fun MyScreen() {
    // Simple text without localization
    Text(
        text = "Hello World",
        theme = Theme.FREE_LIGHT,
        role = TextRole.CONTROL
    )

    // Localized text
    val localizedText = TextData(
        value = "settings",
        label = mapOf(
            Language.EN to "Settings",
            Language.RU to "Настройки"
        )
    )

    Text(
        text = localizedText,
        lang = Language.EN,
        theme = Theme.FREE_LIGHT,
        role = TextRole.TITLE
    )
}
```

## API Reference

### Java Custom View API

#### Constructors
```java
public Text(Context context)
public Text(Context context, AttributeSet attrs)
public Text(Context context, AttributeSet attrs, int defStyleAttr)
public Text(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
```

#### Text Configuration Methods
```java
// Text content
public void setText(String text)                    // Set static text
public void setText(Map<Language, String> text)     // Set localized text
public String getText()                             // Get current text

// Theme and styling
public void setTheme(Theme theme)                  // Set theme (Theme.FREE_LIGHT, etc.)
public Theme getTheme()                            // Get current theme
public void setRole(TextRole role)                  // Set text role (CONTROL, TITLE)
public TextRole getRole()                           // Get current text role
public void setColor(int color)                     // Set custom color (for animation)
public void useThemeColor()                         // Reset to theme color

// Localization
public void setLanguage(Language language)          // Set language
public Language getLanguage()                       // Get current language

// Text alignment
public void setTextAlign(Paint.Align align)         // Set text alignment
```

#### Positioning and Measurement Methods
```java
// Positioning (for custom drawing in parent components)
public void setPosition(float x, float y)           // Set draw position
public float getTextWidth()                         // Get text width in pixels
public float getTextHeight()                        // Get text height in pixels
public float getBaselineOffset()                    // Get baseline offset for centering

// Drawing
public void draw(Canvas canvas)                     // Draw at specified position
```

### State Persistence

The Text component automatically saves and restores its state during configuration changes (e.g., screen rotation). The following state is preserved:

- Current text
- Text role
- Current theme
- Current language
- Custom position

No additional code is required - the component handles state persistence automatically through the Android View state mechanism.

### Kotlin Compose API

```kotlin
// Localized text version
@Composable
fun Text(
    text: TextData,                                  // Text data model with labels
    lang: Language,                                  // Current language
    theme: String,                                   // Theme identifier
    role: TextRole = TextRole.CONTROL,               // Text role
    onViewCreated: ((ru.voboost.components.text.Text) -> Unit)? = null  // Optional callback
)

// Simple string version
@Composable
fun Text(
    text: String,                                    // Simple string text
    theme: String,                                   // Theme identifier
    role: TextRole = TextRole.CONTROL,               // Text role
    onViewCreated: ((ru.voboost.components.text.Text) -> Unit)? = null  // Optional callback
)
```

### TextData Model (Kotlin)

```kotlin
data class TextData(
    val value: String,                               // Text value identifier
    val label: Map<Language, String>                 // Translation map
) {
    fun getText(lang: Language): String              // Get localized text
}
```

## TextRole Enum

The TextRole enum defines predefined text styles with specific font sizes and weights:

```java
public enum TextRole {
    CONTROL(24, 500),   // For radio buttons, tabs, and other UI controls
    TITLE(32, 600);     // For section headers and prominent text

    public int getSizePx()    // Get font size in pixels
    public int getWeight()    // Get font weight (100-900)
}
```

### CONTROL Role
- Font size: 24px
- Font weight: 500 (medium)
- Use case: Radio buttons, tabs, and other UI controls

### TITLE Role
- Font size: 32px
- Font weight: 600 (semi-bold)
- Use case: Section headers and prominent text

## Themes

Available themes:
- `Theme.FREE_LIGHT` - Light theme with dark text
- `Theme.FREE_DARK` - Dark theme with light text
- `Theme.DREAMER_LIGHT` - Light theme with dark text
- `Theme.DREAMER_DARK` - Dark theme with light text

### Theme Colors

#### Free Light Theme
- CONTROL: #1a1a1a (very dark gray)
- TITLE: #000000 (black)

#### Free Dark Theme
- CONTROL: #ffffff (white)
- TITLE: #f1f5fb (very light blue)

#### Dreamer Light Theme
- CONTROL: #1a1a1a (very dark gray)
- TITLE: #000000 (black)

#### Dreamer Dark Theme
- CONTROL: #ffffff (white)
- TITLE: #f1f5fb (very light blue)

## Localization

The component uses external localization through `Map<Language, String>`:

```java
// Create localized text
Map<Language, String> localizedText = Map.of(
    Language.EN, "Settings",
    Language.RU, "Настройки"
);

Text text = new Text(context);
text.setText(localizedText);
text.setLanguage(Language.EN);  // Display English text
```

Language is set via `setLanguage(Language)` in Java or `lang` parameter in Compose.

## Integration Patterns

### Using Text in Custom Components

The Text component is designed to be used within other components that need custom text positioning:

```java
public class CustomComponent extends View {
    private Text titleText;
    private Text controlText;

    public CustomComponent(Context context) {
        super(context);

        // Create text instances
        titleText = new Text(context);
        titleText.setRole(TextRole.TITLE);
        titleText.setTheme("free-light");

        controlText = new Text(context);
        controlText.setRole(TextRole.CONTROL);
        controlText.setTheme("free-light");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Position and draw text
        titleText.setPosition(50, 100);
        titleText.draw(canvas);

        controlText.setPosition(50, 150);
        controlText.draw(canvas);
    }
}
```

### Animation Support

The Text component supports dynamic color changes for animation:

```java
// Animate color change
ValueAnimator colorAnimation = ValueAnimator.ofArgb(startColor, endColor);
colorAnimation.setDuration(300);
colorAnimation.addUpdateListener(animator -> {
    int color = (int) animator.getAnimatedValue();
    text.setColor(color);  // Use custom color
});
colorAnimation.addListener(new AnimatorListenerAdapter() {
    @Override
    public void onAnimationEnd(Animator animation) {
        text.useThemeColor();  // Reset to theme color
    }
});
colorAnimation.start();
```

## Complete Examples

### Java Example

```java
public class MainActivity extends AppCompatActivity {
    private Text titleText;
    private Text controlText;
    private Language currentLanguage = Language.EN;
    private String currentTheme = "free-light";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create title text
        titleText = new Text(this);
        titleText.setRole(TextRole.TITLE);
        titleText.setTheme(currentTheme);

        // Create localized title
        Map<Language, String> titleLabels = Map.of(
            Language.EN, "Application Settings",
            Language.RU, "Настройки приложения"
        );
        titleText.setText(titleLabels);
        titleText.setLanguage(currentLanguage);

        // Create control text
        controlText = new Text(this);
        controlText.setRole(TextRole.CONTROL);
        controlText.setTheme(currentTheme);
        controlText.setText("Save");

        // Add to layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(titleText);
        layout.addView(controlText);
        setContentView(layout);
    }

    private void switchLanguage() {
        currentLanguage = (currentLanguage == Language.EN) ? Language.RU : Language.EN;
        titleText.setLanguage(currentLanguage);
    }

    private void switchTheme() {
        currentTheme = currentTheme.equals("free-light") ? "free-dark" : "free-light";
        titleText.setTheme(currentTheme);
        controlText.setTheme(currentTheme);
    }
}
```

### Kotlin Example

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var titleText: Text
    private lateinit var controlText: Text
    private var currentLanguage = Language.EN
    private var currentTheme = "free-light"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create title text
        titleText = Text(this).apply {
            role = TextRole.TITLE
            theme = currentTheme

            val titleLabels = mapOf(
                Language.EN to "Application Settings",
                Language.RU to "Настройки приложения"
            )
            setText(titleLabels)
            setLanguage(currentLanguage)
        }

        // Create control text
        controlText = Text(this).apply {
            role = TextRole.CONTROL
            theme = currentTheme
            setText("Save")
        }

        // Add to layout
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(titleText)
            addView(controlText)
        }
        setContentView(layout)
    }

    private fun switchLanguage() {
        currentLanguage = if (currentLanguage == Language.EN) Language.RU else Language.EN
        titleText.setLanguage(currentLanguage)
    }

    private fun switchTheme() {
        currentTheme = if (currentTheme == Theme.FREE_LIGHT) Theme.FREE_DARK else Theme.FREE_LIGHT
        titleText.setTheme(currentTheme)
        controlText.setTheme(currentTheme)
    }
}
```

### Compose Example

```kotlin
import ru.voboost.components.text.Text
import ru.voboost.components.text.TextData
import ru.voboost.components.text.TextRole
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

@Composable
fun TextDemo() {
    var currentLanguage by remember { mutableStateOf(Language.EN) }
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
        // Title text
        val titleText = TextData(
            value = "title",
            label = mapOf(
                Language.EN to "Application Settings",
                Language.RU to "Настройки приложения"
            )
        )

        Text(
            text = titleText,
            lang = currentLanguage,
            theme = currentTheme,
            role = TextRole.TITLE
        )

        // Control text examples
        Text(
            text = if (currentLanguage == Language.EN) "Save" else "Сохранить",
            theme = currentTheme,
            role = TextRole.CONTROL
        )

        Text(
            text = if (currentLanguage == Language.EN) "Cancel" else "Отмена",
            theme = currentTheme,
            role = TextRole.CONTROL
        )

        // Language selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { currentLanguage = Language.EN }) {
                Text("English")
            }
            Button(onClick = { currentLanguage = Language.RU }) {
                Text("Русский")
            }
        }

        // Theme selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { isDarkTheme = false }) {
                Text("Light")
            }
            Button(onClick = { isDarkTheme = true }) {
                Text("Dark")
            }
        }

        // Car type selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { isDreamer = false }) {
                Text("Free")
            }
            Button(onClick = { isDreamer = true }) {
                Text("Dreamer")
            }
        }
    }
}
```

## Implementation Details

### Font System

The Text component uses the InterVariable font for consistent typography across all text roles:

- Font file: `fonts/inter_variable.ttf`
- Fallback: Default system font if InterVariable is not available
- Font weight: Applied via fake bold for compatibility (variable fonts would be preferred)

### Canvas Rendering

- Text rendering via `Canvas.drawText()`
- Anti-aliasing enabled for smooth text rendering
- Efficient text measurement using `Paint.getTextBounds()`
- Baseline offset calculation for precise vertical positioning

### Positioning System

The Text component supports two positioning modes:

1. **Standalone View**: When used as a regular View, text is drawn at the top-left corner
2. **Custom Positioning**: When `setPosition()` is called, text is drawn at the specified coordinates

### Performance Considerations

- Text measurement is cached in `Rect` object to avoid repeated allocations
- Paint object is reused for all drawing operations
- Hardware acceleration is inherited from parent View
- Minimal object creation in drawing methods

## Testing

The component includes comprehensive tests:

- **Java Unit tests**: [`Text.test/TextTestUnit.java`](Text.test/TextTestUnit.java) - unit tests for component logic
- **Java Visual tests**: [`Text.test-visual.java`](Text.test-visual.java) - visual regression tests
- **Kotlin Unit tests**: [`Text.test/TextTestUnit.kt`](Text.test/TextTestUnit.kt) - unit tests for Compose wrapper
- **Kotlin Visual tests**: [`Text.test-visual.kt`](Text.test-visual.kt) - visual tests for Compose integration
- **Screenshots**: [`Text.screenshots/`](Text.screenshots/) directory - visual test results

### Run Tests

```bash
./gradlew testUnit        # All unit tests
./gradlew testVisual      # All visual tests

./gradlew testJava        # All Java tests (unit + visual)
./gradlew testKotlin      # All Kotlin tests (unit + visual)

./gradlew testVisualSave  # Record new screenshots
```

## Multilingual Architecture

The Text component follows the project's multilingual architecture pattern:

- **Java Custom View**: Complete implementation with all rendering logic
- **Kotlin Compose Wrapper**: Lightweight AndroidView integration
- **Shared Data Models**: TextRole and TextTheme classes used by both implementations
- **Consistent API**: Similar method signatures and behavior across languages
- **Cross-Platform Compatibility**: Works in pure Java, pure Kotlin, and Jetpack Compose projects

## Integration with Other Components

The Text component is designed to work seamlessly with other components in the library:

- **Panel Component**: Text can be positioned within Panel containers
- **Screen Component**: Text can be used as titles or content within Screen containers
- **Radio Component**: Text is used internally for button labels
- **Custom Components**: Text can be integrated into any custom component that needs text rendering

## Best Practices

1. **Use appropriate TextRole**: Choose CONTROL for UI elements and TITLE for headers
2. **Provide complete localization**: Include all supported languages in label maps
3. **Use theme colors**: Avoid hardcoding colors, use the theme system instead
4. **Position carefully**: When using custom positioning, account for text dimensions
5. **Handle state properly**: Let the component manage its own state persistence

# Font Component

Provides the project typeface for all components.

## Architecture

Single file: **[Font.java](Font.java)** — static utility, no Compose wrapper needed.

Font files are co-located (BEM structure):
```
font/
├── Font.java              # Loader with caching
├── Font.ttf               # Regular weight
├── Font_bold_ascii.ttf    # Bold for ASCII text
└── Font_bold_unicode.ttf  # Bold for non-ASCII text (Cyrillic, etc.)
```

## Usage

```java
// Regular font
Typeface regular = Font.getRegular(context);
paint.setTypeface(regular);

// Bold font (auto-selects ASCII vs Unicode variant)
Typeface bold = Font.getBold(context, "Hello");    // → Font_bold_ascii.ttf
Typeface boldRu = Font.getBold(context, "Привет"); // → Font_bold_unicode.ttf
```

## API

```java
static Typeface getRegular(Context context)
static Typeface getBold(Context context, String text)  // auto-selects variant
static void clearCache()                               // for tests
```

## Key Behavior

- **Cached** — Typeface instances loaded once, reused
- **No fallback** — throws `RuntimeException` if font file not found
- **Two bold variants** — ASCII-only text uses `Font_bold_ascii.ttf`, text with non-ASCII chars uses `Font_bold_unicode.ttf`
- **Asset loading** — fonts loaded from assets via `build.gradle.kts` config: `assets.srcDir("src/main/java/ru/voboost/components/font")`

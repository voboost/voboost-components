# Language

Shared language enum for all Voboost components.

## File

**[Language.java](Language.java)** — Java enum, used directly by both Java and Kotlin code.

## Values

| Enum          | Code   |
|---------------|--------|
| `Language.EN` | `"en"` |
| `Language.RU` | `"ru"` |

## API

```java
String getCode()                  // ISO 639-1 code
static Language fromCode(String)  // parse from string, default: EN
```

## Design Decision

Java enum (not Kotlin) because:
1. Used directly in Java View code
2. Works seamlessly in Kotlin
3. No conversion needed between layers

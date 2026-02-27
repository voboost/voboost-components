# Theme

Shared theme enum for all Voboost components.

## File

**[Theme.java](Theme.java)** — Java enum, used directly by both Java and Kotlin code.

## Values

| Enum                  | String value    |
|-----------------------|-----------------|
| `Theme.FREE_LIGHT`    | `"free-light"`  |
| `Theme.FREE_DARK`     | `"free-dark"`   |
| `Theme.DREAMER_LIGHT` | `"dreamer-light"`|
| `Theme.DREAMER_DARK`  | `"dreamer-dark"` |

## API

```java
String getValue()              // string representation for serialization
boolean isLight()
boolean isDark()
boolean isFree()
boolean isDreamer()
static Theme fromValue(String) // parse from string, default: FREE_DARK
```

## Design Decision

Java enum (not Kotlin) because:
1. Used directly in Java View code
2. Works seamlessly in Kotlin
3. No conversion needed between layers

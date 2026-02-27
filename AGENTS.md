# Voboost Components — Rules

Inherits ../voboost-codestyle/AGENTS.md
[Detailed README](README.md) | [Reference component](src/main/java/ru/voboost/components/radio/Radio.md)

## Coding Standards
- ALL sizes in **pixels**, not dp
- ALL hex colors **lowercase**

## Architecture
- Each component is **self-contained** — no shared Component.kt, i18n, theme, or utils
- Strings via `Map<String, String>`, theme as parameter
- **Java Custom View** = core (logic, rendering, animations)
- **Kotlin wrapper** = AndroidView integration only

## BEM Tests
- Co-located in `src/main/java/ru/voboost/components/[component]/`
- `[Component].test/[Component]TestUnit.java`, `[Component]TestVisual.java`
- `[Component].screenshots/`
- No `src/test/java/` or `__tests__/`

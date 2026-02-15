package ru.voboost.components.tabs;

import java.util.Map;

/**
 * Data model for a single tab item in the Tabs component.
 *
 * <p>Each TabItem contains:
 * <ul>
 *   <li>value - unique identifier for the tab</li>
 *   <li>label - Map of language code to localized label text</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 * Map<String, String> labels = new HashMap<>();
 * labels.put("en", "Settings");
 * labels.put("ru", "Настройки");
 * TabItem item = new TabItem("settings", labels);
 * </pre>
 */
public class TabItem {

    private final String value;
    private final Map<String, String> label;

    /**
     * Creates a new TabItem with the specified value and localized labels.
     *
     * @param value unique identifier for this tab
     * @param label Map of language code to localized label text
     * @throws IllegalArgumentException if value is null or empty
     * @throws IllegalArgumentException if label is null or empty
     */
    public TabItem(String value, Map<String, String> label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TabItem value cannot be null or empty");
        }

        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("TabItem label cannot be null or empty");
        }

        this.value = value;
        this.label = label;
    }

    /**
     * Returns the unique identifier for this tab.
     *
     * @return the tab value
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the Map of localized labels.
     *
     * @return Map of language code to label text
     */
    public Map<String, String> getLabel() {
        return label;
    }

    /**
     * Returns the label text for the specified language.
     *
     * @param languageCode the language code (e.g., "en", "ru")
     * @return the label text, or the value if no label exists for the language
     */
    public String getText(String languageCode) {
        String text = label.get(languageCode);

        return text != null ? text : value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TabItem tabItem = (TabItem) obj;

        return value.equals(tabItem.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "TabItem{value='" + value + "', label=" + label + "}";
    }
}

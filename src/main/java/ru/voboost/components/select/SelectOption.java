package ru.voboost.components.select;

import java.util.HashMap;
import java.util.Map;

/**
 * Data model for a single Select option.
 *
 * @param value unique option identifier
 * @param label localized text: language code → display text
 */
public class SelectOption {
    private final String value;
    private final Map<String, String> label;

    /**
     * Creates a new SelectOption.
     *
     * @param value unique option value
     * @param label translation map: language code → localized text
     * @throws IllegalArgumentException if value is blank or label is empty
     */
    public SelectOption(String value, Map<String, String> label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("SelectOption value cannot be blank");
        }
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("SelectOption must have at least one label");
        }

        // Validate no null or empty label values
        for (Map.Entry<String, String> entry : label.entrySet()) {
            if (entry.getKey() == null || entry.getKey().trim().isEmpty()) {
                throw new IllegalArgumentException("SelectOption language code cannot be null or blank");
            }
            if (entry.getValue() == null || entry.getValue().trim().isEmpty()) {
                throw new IllegalArgumentException("SelectOption label text cannot be null or blank for language: " + entry.getKey());
            }
        }

        this.value = value;
        this.label = new HashMap<>(label);
    }

    /**
     * Returns localized text for the specified language.
     */
    public String getText(String lang) {
        if (lang == null) {
            lang = label.keySet().iterator().next();
        }

        String text = label.get(lang);
        if (text != null) {
            return text;
        }

        // Fallback to first available label
        String firstLabel = label.values().iterator().next();
        return firstLabel != null ? firstLabel : value;
    }

    public String getValue() {
        return value;
    }

    public Map<String, String> getLabel() {
        return new HashMap<>(label);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SelectOption that = (SelectOption) obj;
        return value.equals(that.value) && label.equals(that.label);
    }

    @Override
    public int hashCode() {
        return value.hashCode() * 31 + label.hashCode();
    }

    @Override
    public String toString() {
        return "SelectOption{value='" + value + "', label=" + label + '}';
    }
}

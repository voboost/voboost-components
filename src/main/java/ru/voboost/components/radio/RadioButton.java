package ru.voboost.components.radio;

import java.util.HashMap;
import java.util.Map;

/**
 * Data model for a single radio button option.
 *
 * @param value unique option identifier
 * @param label localized text: language code → display text
 */
public class RadioButton {
    private final String value;
    private final Map<String, String> label;

    /**
     * Creates a new RadioButton.
     *
     * @param value unique option value
     * @param label translation map: language code → localized text
     * @throws IllegalArgumentException if value is blank or label is empty
     */
    public RadioButton(String value, Map<String, String> label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("RadioButton value cannot be blank");
        }

        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("RadioButton must have at least one label");
        }

        this.value = value;
        this.label = new HashMap<>(label); // Defensive copy
    }

    /**
     * Returns localized text for the specified language.
     *
     * @param lang language code (e.g. "en", "ru")
     * @return localized text, or first available label as fallback
     */
    public String getText(String lang) {
        String text = label.get(lang);

        if (text != null) {
            return text;
        }

        // Fallback to first available label
        String firstLabel = label.values().iterator().next();
        return firstLabel != null ? firstLabel : value;
    }

    /**
     * Returns the unique value.
     *
     * @return the value string
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns a defensive copy of the label map.
     *
     * @return copy of the label map
     */
    public Map<String, String> getLabel() {
        return new HashMap<>(label);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        RadioButton that = (RadioButton) obj;
        return value.equals(that.value) && label.equals(that.label);
    }

    @Override
    public int hashCode() {
        return value.hashCode() * 31 + label.hashCode();
    }

    /**
     * Creates a copy with optionally modified values.
     *
     * @param value new value (null to keep current)
     * @param label new label map (null to keep current)
     * @return new RadioButton instance
     */
    public RadioButton copy(String value, Map<String, String> label) {
        return new RadioButton(
                value != null ? value : this.value, label != null ? label : this.label);
    }

    /**
     * Creates a copy with only value changed.
     *
     * @param value new value
     * @return new RadioButton instance
     */
    public RadioButton copy(String value) {
        return copy(value, null);
    }

    /**
     * Creates a copy with only label changed.
     *
     * @param label new label map
     * @return new RadioButton instance
     */
    public RadioButton copy(Map<String, String> label) {
        return copy(null, label);
    }

    @Override
    public String toString() {
        return "RadioButton{" + "value='" + value + '\'' + ", label=" + label + '}';
    }
}

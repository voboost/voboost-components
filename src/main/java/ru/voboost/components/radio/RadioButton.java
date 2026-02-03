package ru.voboost.components.radio;

import java.util.HashMap;
import java.util.Map;

/**
 * Pure data model for radio group option
 * Java equivalent of the Kotlin RadioButton data class
 */
public class RadioButton {
    private final String value;
    private final Map<String, String> label;

    /**
     * Constructor for RadioButton
     * @param value Unique option value
     * @param label Translation map: language code -> localized text
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
     * Get localized text for specified language
     * @param lang Language code ("ru", "en")
     * @return Localized text from the provided map
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
     * Get the unique value of this radio button
     * @return The value string
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the label map
     * @return Copy of the label map
     */
    public Map<String, String> getLabel() {
        return new HashMap<>(label);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RadioButton that = (RadioButton) obj;
        return value.equals(that.value) && label.equals(that.label);
    }

    @Override
    public int hashCode() {
        return value.hashCode() * 31 + label.hashCode();
    }

    /**
     * Create a copy of this RadioButton with modified values
     * Equivalent to Kotlin data class copy() method
     * @param value New value (null to keep current)
     * @param label New label map (null to keep current)
     * @return New RadioButton instance
     */
    public RadioButton copy(String value, Map<String, String> label) {
        return new RadioButton(
                value != null ? value : this.value, label != null ? label : this.label);
    }

    /**
     * Create a copy with only value changed
     * @param value New value
     * @return New RadioButton instance
     */
    public RadioButton copy(String value) {
        return copy(value, null);
    }

    /**
     * Create a copy with only label changed
     * @param label New label map
     * @return New RadioButton instance
     */
    public RadioButton copy(Map<String, String> label) {
        return copy(null, label);
    }

    @Override
    public String toString() {
        return "RadioButton{" + "value='" + value + '\'' + ", label=" + label + '}';
    }
}

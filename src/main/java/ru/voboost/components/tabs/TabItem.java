package ru.voboost.components.tabs;

import androidx.annotation.NonNull;

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
    private final boolean enabled;
    private final int marginTop;
    private final boolean more;

    /**
     * Creates a new TabItem with the specified value and localized labels.
     *
     * @param value unique identifier for this tab
     * @param label Map of language code to localized label text
     * @throws IllegalArgumentException if value is null or empty
     * @throws IllegalArgumentException if label is null or empty
     */
    public TabItem(String value, Map<String, String> label) {
        this(value, label, true, 40, false);
    }

    /**
     * Creates a new TabItem with the specified value, localized labels, and enabled state.
     *
     * @param value unique identifier for this tab
     * @param label Map of language code to localized label text
     * @param enabled whether this tab is enabled (true) or disabled (false)
     * @throws IllegalArgumentException if value is null or empty
     * @throws IllegalArgumentException if label is null or empty
     */
    public TabItem(String value, Map<String, String> label, boolean enabled) {
        this(value, label, enabled, 40, false);
    }

    /**
     * Creates a new TabItem with the specified value, localized labels, enabled state, and margin top.
     *
     * @param value unique identifier for this tab
     * @param label Map of language code to localized label text
     * @param enabled whether this tab is enabled (true) or disabled (false)
     * @param marginTop margin top before this tab item (default 40 if not specified)
     * @throws IllegalArgumentException if value is null or empty
     * @throws IllegalArgumentException if label is null or empty
     */
    public TabItem(String value, Map<String, String> label, boolean enabled, int marginTop) {
        this(value, label, enabled, marginTop, false);
    }

    /**
     * Creates a new TabItem with the specified value, localized labels, enabled state, margin top,
     * and "more" indicator flag.
     *
     * @param value unique identifier for this tab
     * @param label Map of language code to localized label text
     * @param enabled whether this tab is enabled (true) or disabled (false)
     * @param marginTop margin top before this tab item (default 40 if not specified)
     * @param more whether to draw a trailing ">" indicator on the right side of this tab
     * @throws IllegalArgumentException if value is null or empty
     * @throws IllegalArgumentException if label is null or empty
     */
    public TabItem(String value, Map<String, String> label, boolean enabled, int marginTop, boolean more) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TabItem value cannot be null or empty");
        }

        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("TabItem label cannot be null or empty");
        }

        this.value = value;
        this.label = label;
        this.enabled = enabled;
        this.marginTop = marginTop;
        this.more = more;
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

    /**
     * Returns whether this tab is enabled.
     *
     * @return true if the tab is enabled, false if disabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns the margin top before this tab item.
     *
     * @return margin top in pixels
     */
    public int getMarginTop() {
        return marginTop;
    }

    /**
     * Returns whether this tab item shows a trailing ">" indicator.
     *
     * <p>Semantically marks tabs that lead to another screen (matches the original
     * Voyah Setting menu where the "Vehicle" item has a trailing chevron icon).
     *
     * @return true if the more-indicator should be drawn, false otherwise
     */
    public boolean hasMore() {
        return more;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TabItem tabItem = (TabItem) obj;

        if (!value.equals(tabItem.value)) return false;
        if (enabled != tabItem.enabled) return false;
        if (marginTop != tabItem.marginTop) return false;
        if (more != tabItem.more) return false;
        return label.equals(tabItem.label);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + marginTop;
        result = 31 * result + (more ? 1 : 0);
        result = 31 * result + label.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TabItem{value='" + value + "', label=" + label + ", enabled=" + enabled + ", marginTop=" + marginTop + ", more=" + more + "}";
    }

    /**
     * Returns whether this tab item is selected.
     *
     * @return true if the tab is selected, false otherwise
     */
    public boolean isSelected() {
        return false;
    }

    // ============================================================
    // BUILDER API
    // ============================================================

    /**
     * Creates a new Builder for TabItem.
     *
     * @param value unique identifier for this tab
     * @param label Map of language code to localized label text
     * @return a new Builder instance
     * @throws IllegalArgumentException if value is null or empty
     * @throws IllegalArgumentException if label is null or empty
     */
    @NonNull
    public static Builder create(@NonNull String value, @NonNull java.util.Map<String, String> label) {
        return new Builder(value, label);
    }

    /**
     * Builder for creating TabItem instances with a fluent API.
     */
    public static class Builder {
        private final String value;
        private final java.util.Map<String, String> label;
        private boolean enabled = true;
        private boolean selected = false;
        private int marginTop = 40;
        private boolean more = false;

        private Builder(String value, java.util.Map<String, String> label) {
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
         * Sets whether this tab is enabled.
         *
         * @param enabled true if enabled, false if disabled
         * @return this Builder instance
         */
        @NonNull
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Sets whether this tab is selected.
         *
         * @param selected true if selected, false otherwise
         * @return this Builder instance
         */
        @NonNull
        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        /**
         * Sets the margin top before this tab item.
         *
         * @param marginTop margin top in pixels
         * @return this Builder instance
         */
        @NonNull
        public Builder marginTop(int marginTop) {
            this.marginTop = marginTop;
            return this;
        }

        /**
         * Sets whether this tab item shows a trailing ">" indicator.
         *
         * @param more true to draw the more-indicator, false otherwise
         * @return this Builder instance
         */
        @NonNull
        public Builder more(boolean more) {
            this.more = more;
            return this;
        }

        /**
         * Builds and returns the TabItem instance.
         *
         * @return a new TabItem instance
         */
        @NonNull
        public TabItem build() {
            return new TabItem(value, label, enabled, marginTop, more) {
                private final boolean isSelected = selected;

                @Override
                public boolean isSelected() {
                    return isSelected;
                }
            };
        }
    }
}

package ru.voboost.components.text;

/**
 * Text role enumeration for different text styles in the Voboost component library.
 * Each role defines a specific font size and weight combination.
 */
public enum TextRole {
    /**
     * Control text style for radio buttons, tabs, and other UI controls.
     * Size: 24px, Weight: 500 (medium)
     */
    CONTROL(24, 500),

    /**
     * Title text style for section headers and prominent text.
     * Size: 32px, Weight: 600 (semi-bold)
     */
    TITLE(32, 600);

    private final int sizePx;
    private final int weight;

    /**
     * Constructor for TextRole enum.
     *
     * @param sizePx Font size in pixels
     * @param weight Font weight (100-900)
     */
    TextRole(int sizePx, int weight) {
        this.sizePx = sizePx;
        this.weight = weight;
    }

    /**
     * Get the font size in pixels for this text role.
     *
     * @return Font size in pixels
     */
    public int getSizePx() {
        return sizePx;
    }

    /**
     * Get the font weight for this text role.
     *
     * @return Font weight (100-900)
     */
    public int getWeight() {
        return weight;
    }
}

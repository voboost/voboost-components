package ru.voboost.components.theme;

/**
 * Interface for components that support theme switching and propagation.
 * 
 * <p>
 * This generic interface allows container view groups (like Section or Panel)
 * to propagate themes dynamically without knowing the exact subtype of their
 * children.
 */
public interface IThemable {
    /**
     * Sets the theme for this component.
     *
     * @param theme the theme to apply
     */
    void setTheme(Theme theme);

    /**
     * Propagates the theme to all appropriate child components.
     * 
     * @param theme the theme to propagate
     */
    void propagateTheme(Theme theme);
}

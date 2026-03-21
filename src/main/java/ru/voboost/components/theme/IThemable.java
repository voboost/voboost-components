package ru.voboost.components.theme;

/**
 * Interface for components that support theme switching and propagation.
 *
 * <p>
 * This generic interface allows container view groups (like Section or Panel)
 * to propagate themes dynamically without knowing the exact subtype of their
 * children.
 *
 * <p><b>Implementation Notes:</b>
 * <ul>
 *   <li><b>Container components</b> (Screen, Panel, Section, Tabs): Override
 *       {@link #propagateTheme(Theme)} to recursively call {@link #setTheme(Theme)}
 *       on all {@link IThemable} children</li>
 *   <li><b>Leaf components</b> (Button, Checkbox, Text, Radio, Select): Implement
 *       {@link #propagateTheme(Theme)} as an empty method - no children to propagate to</li>
 * </ul>
 *
 * <p><b>Thread Safety:</b> Implementations must ensure thread-safe access to
 * theme state. Consider using {@code volatile} or synchronized methods if
 * theme can be changed from multiple threads. In typical Android usage,
 * theme changes occur on the main thread only.
 */
public interface IThemable {
    /**
     * Sets the theme for this component.
     *
     * <p>Implementations should store the theme and trigger a visual update
     * (e.g., invalidate() for custom views).
     *
     * @param theme the theme to apply, never null in typical usage
     */
    void setTheme(Theme theme);

    /**
     * Propagates the theme to all appropriate child components.
     *
     * <p>Container components should override this method to recursively
     * call {@code setTheme()} on all {@link IThemable} children.
     * Leaf components should leave this method empty.
     *
     * @param theme the theme to propagate
     */
    void propagateTheme(Theme theme);
}

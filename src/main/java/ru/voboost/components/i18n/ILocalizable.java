package ru.voboost.components.i18n;

/**
 * Interface for components that support language switching and propagation.
 * 
 * <p>
 * This generic interface allows container view groups (like Section or Panel)
 * to propagate languages dynamically without knowing the exact subtype of their
 * children.
 */
public interface ILocalizable {
    /**
     * Sets the language for this component.
     *
     * @param language the language to apply
     */
    void setLanguage(Language language);

    /**
     * Propagates the language to all appropriate child components.
     * 
     * @param language the language to propagate
     */
    void propagateLanguage(Language language);
}

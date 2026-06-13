package ru.voboost.components.demo.cunba;

import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.TextView;

import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * Plain text view that resolves its text by language and its color by theme.
 *
 * <p>Used by the custom Activation section so its title/hint texts stay in sync
 * with the rest of the demo (Section propagates theme/language to its
 * {@link IThemable}/{@link ILocalizable} descendants).
 */
public class LocalizableTextView extends TextView implements IThemable, ILocalizable {

    /** Primary text color role (titles, price titles). */
    public static final int ROLE_TITLE = 0;
    /** Muted hint text color role. */
    public static final int ROLE_HINT = 1;

    private final int role;

    private Map<String, String> textMap;
    private Theme currentTheme;
    private Language currentLanguage;

    public LocalizableTextView(Context context, float textSizePx, int role) {
        super(context);
        this.role = role;
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx);
    }

    /** Sets the localized text (language code -> text). */
    public void setData(Map<String, String> text) {
        this.textMap = text;
        resolve();
    }

    private boolean isDark() {
        return currentTheme != null && currentTheme.getValue() != null && currentTheme.getValue().endsWith("-dark");
    }

    private void resolve() {
        if (textMap != null && !textMap.isEmpty()) {
            String langCode = currentLanguage != null ? currentLanguage.getCode() : "en";
            setText(textMap.getOrDefault(langCode, textMap.values().iterator().next()));
        }
        if (currentTheme != null) {
            int color;
            if (role == ROLE_HINT) {
                color = isDark() ? Color.parseColor("#919397") : Color.parseColor("#8f949e");
            } else {
                color = isDark() ? Color.parseColor("#ffffff") : Color.parseColor("#2d3442");
            }
            setTextColor(color);
        }
    }

    @Override
    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        resolve();
    }

    @Override
    public void propagateTheme(Theme theme) {
        // Leaf view
    }

    @Override
    public void setLanguage(Language language) {
        this.currentLanguage = language;
        resolve();
    }

    @Override
    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    @Override
    public void propagateLanguage(Language language) {
        // Leaf view
    }
}

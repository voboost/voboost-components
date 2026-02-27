package ru.voboost.components.text;

import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * Text component — simple themed text rendering with localization.
 *
 * <p>
 * Extends AppCompatTextView for robust text measurement and rendering,
 * while integrating Voboost theme, language, and role-driven sizing.
 */
public class Text extends AppCompatTextView implements IThemable, ILocalizable {

    private Map<Language, String> localizedText;
    private TextRole role = TextRole.CONTROL;
    private Theme theme;
    private Language language;
    private boolean isInitializing = true;

    public Text(Context context) {
        super(context);
        init();
    }

    public Text(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Text(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        isInitializing = false;
        applyRole();
    }

    /**
     * Sets localized text.
     *
     * @param localizedText map of Language to text string
     */
    public void setText(Map<Language, String> localizedText) {
        this.localizedText = localizedText;
        updateCurrentText();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (!isInitializing) {
            updateTypeface();
        }
    }

    public void setRole(TextRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.role = role;
        applyRole();
        applyTheme();
    }

    public TextRole getRole() {
        return role;
    }

    @Override
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }
        this.theme = theme;
        applyTheme();
    }

    public Theme getTheme() {
        return theme;
    }

    @Override
    public void propagateTheme(Theme theme) {
        // Leaf component, no children
    }

    @Override
    public void setLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
        this.language = language;
        updateCurrentText();
    }

    public Language getLanguage() {
        return language;
    }

    @Override
    public void propagateLanguage(Language language) {
        // Leaf component, no children
    }

    private void applyRole() {
        setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, role.getSizePx());
        updateTypeface();
    }

    private void updateTypeface() {
        if (role == null)
            return;
        Typeface customTypeface;
        if (role.getWeight() >= 600) {
            String currentText = getText() != null ? getText().toString() : "";
            customTypeface = Font.getBold(getContext(), currentText);
        } else {
            customTypeface = Font.getRegular(getContext());
        }
        setTypeface(customTypeface);
    }

    private void applyTheme() {
        if (theme != null && role != null) {
            int color = TextTheme.getColor(role, theme);
            setTextColor(color);
        }
    }

    private void updateCurrentText() {
        if (localizedText != null && language != null && localizedText.containsKey(language)) {
            setText(localizedText.get(language));
        }
    }
}

package ru.voboost.components.hint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * Hint component - renders a muted text block for descriptions and contextual help.
 *
 * <p>Theme-aware and localizable. Text size 24px matches description style
 * in Checkbox and Radio components. Use alongside buttons, checkboxes, and
 * other components to provide explanatory text.
 *
 * <pre>
 * Hint hint = new Hint(context);
 * hint.setTheme(Theme.FREE_DARK);
 * hint.setLanguage(Language.EN);
 * hint.setText(mapOf("en", "Stop all apps", "ru", "Ostanovit"));
 * section.addView(hint);
 * </pre>
 */
public class Hint extends View implements IThemable, ILocalizable {

    // State
    private Map<String, String> textMap = new HashMap<>();
    private Theme currentTheme;
    private Language currentLanguage;

    // Rendering
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String displayText = "";
    private int computedHeight = 0;

    public Hint(Context context) {
        super(context);
        init();
    }

    public Hint(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Hint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint.setTextSize(HintTheme.TEXT_SIZE_PX);
        textPaint.setColor(HintTheme.getTextColor(null));
    }

    // ============================================================
    // Public API
    // ============================================================

    /**
     * Sets the localized text map. Key is language code ("en", "ru").
     *
     * @param textMap localized text entries
     */
    public void setText(Map<String, String> textMap) {
        if (textMap != null) {
            this.textMap = textMap;
        }
        resolveText();
        invalidate();
        requestLayout();
    }

    // ============================================================
    // IThemable
    // ============================================================

    @Override
    public void setTheme(Theme theme) {
        this.currentTheme = theme;
        textPaint.setColor(HintTheme.getTextColor(theme));
        invalidate();
    }

    @Override
    public void propagateTheme(Theme theme) {
        setTheme(theme);
    }

    // ============================================================
    // ILocalizable
    // ============================================================

    @Override
    public void setLanguage(Language language) {
        this.currentLanguage = language;
        resolveText();
        invalidate();
        requestLayout();
    }

    @Override
    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    @Override
    public void propagateLanguage(Language language) {
        setLanguage(language);
    }

    // ============================================================
    // Internal
    // ============================================================

    private void resolveText() {
        if (textMap == null || textMap.isEmpty()) {
            displayText = "";
            return;
        }
        String langCode = currentLanguage != null ? currentLanguage.getCode() : "en";
        displayText = textMap.get(langCode);
        if (displayText == null && !textMap.isEmpty()) {
            displayText = textMap.values().iterator().next();
        }
        if (displayText == null) {
            displayText = "";
        }
        textPaint.setTypeface(Font.getRegular(getContext()));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (width <= 0) {
            width = 1920;
        }

        if (displayText.isEmpty()) {
            computedHeight = 0;
        } else {
            float textWidth = width - getPaddingLeft() - getPaddingRight();
            if (textWidth < 0) {
                textWidth = width;
            }
            float textLength = textPaint.measureText(displayText);
            int lines = 1;
            if (textLength > textWidth && textWidth > 0) {
                lines = (int) Math.ceil(textLength / textWidth);
            }

            float lineHeight = HintTheme.TEXT_SIZE_PX * 1.3f;
            computedHeight = HintTheme.PADDING_TOP_PX
                    + (int) (lines * lineHeight)
                    + HintTheme.PADDING_BOTTOM_PX;
        }

        setMeasuredDimension(width, computedHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (displayText.isEmpty()) {
            return;
        }

        float x = getPaddingLeft();
        float y = getPaddingTop() + HintTheme.TEXT_SIZE_PX;

        float maxWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float lineHeight = HintTheme.TEXT_SIZE_PX * 1.3f;

        String remaining = displayText;
        while (!remaining.isEmpty() && y < getHeight()) {
            int breakIndex = textPaint.breakText(remaining, true, maxWidth, null);
            if (breakIndex <= 0) {
                break;
            }
            int lastSpace = remaining.lastIndexOf(' ', breakIndex);
            if (lastSpace > 0 && lastSpace < breakIndex && breakIndex < remaining.length()) {
                breakIndex = lastSpace + 1;
            }
            String line = remaining.substring(0, breakIndex).trim();
            canvas.drawText(line, x, y, textPaint);
            remaining = remaining.substring(breakIndex).trim();
            y += lineHeight;
        }
    }
}

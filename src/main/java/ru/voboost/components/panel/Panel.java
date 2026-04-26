package ru.voboost.components.panel;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.section.Section;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/** Vertical LinearLayout container for Section views. Scrolling is provided by surrounding ScreenView. */
public class Panel extends LinearLayout implements IThemable, ILocalizable {

    private Theme currentTheme;
    private Language currentLanguage;
    private Bitmap image;
    private boolean compact = false;

    /** Returns the compact panel background image, or null. */
    public Bitmap getImage() {
        return image;
    }

    /** Sets the compact panel background image drawn behind transparent gaps between sections. */
    public void setImage(Bitmap image) {
        this.image = image;
    }

    /** Returns the width (px) of the compact panel background image. */
    public int getImageWidth() {
        return PanelTheme.IMAGE_WIDTH;
    }

    /** Returns the right margin (px) of the compact panel background image. Negative = extends beyond edge. */
    public int getImageMarginRight() {
        return PanelTheme.IMAGE_MARGIN_RIGHT;
    }

    /** Returns whether this panel is in compact mode. */
    public boolean isCompact() {
        return compact;
    }

    /**
     * Enables/disables compact mode. In compact mode the Panel applies a top padding of
     * {@link PanelTheme#COMPACT_TOP_MARGIN}, so the wrapper above can reveal
     * the screen background image behind the gap.
     */
    public void setCompact(boolean compact) {
        this.compact = compact;
        setPadding(0, compact ? PanelTheme.COMPACT_TOP_MARGIN : 0, 0, PanelTheme.PADDING_BOTTOM);
    }

    public Panel(Context context) {
        super(context);
        init();
    }

    public Panel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Panel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setPadding(0, 0, 0, PanelTheme.PADDING_BOTTOM);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateChildMargins();
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        updateChildMargins();
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        updateChildMargins();
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
    }

    /** Updates includeBottomMargin flag on Section children: only the last Section omits it. */
    private void updateChildMargins() {
        int lastSectionIndex = -1;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            if (getChildAt(i) instanceof Section) {
                lastSectionIndex = i;
                break;
            }
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof Section) {
                ((Section) child).setIncludeBottomMargin(i != lastSectionIndex);
            }
        }
    }

    @Override
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }
        this.currentTheme = theme;
        propagateTheme(theme);
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    @Override
    public void setLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
        this.currentLanguage = language;
        propagateLanguage(language);
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    @Override
    public void propagateTheme(Theme theme) {
        if (theme == null) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof IThemable) {
                ((IThemable) child).setTheme(theme);
                ((IThemable) child).propagateTheme(theme);
            } else if (child instanceof ViewGroup) {
                propagateThemeToViewGroup((ViewGroup) child, theme);
            }
        }
    }

    private void propagateThemeToViewGroup(ViewGroup viewGroup, Theme theme) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof IThemable) {
                ((IThemable) child).setTheme(theme);
                ((IThemable) child).propagateTheme(theme);
            } else if (child instanceof ViewGroup) {
                propagateThemeToViewGroup((ViewGroup) child, theme);
            }
        }
    }

    @Override
    public void propagateLanguage(Language language) {
        if (language == null) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ILocalizable) {
                ((ILocalizable) child).setLanguage(language);
                ((ILocalizable) child).propagateLanguage(language);
            } else if (child instanceof ViewGroup) {
                propagateLanguageToViewGroup((ViewGroup) child, language);
            }
        }
    }

    private void propagateLanguageToViewGroup(ViewGroup viewGroup, Language language) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ILocalizable) {
                ((ILocalizable) child).setLanguage(language);
                ((ILocalizable) child).propagateLanguage(language);
            } else if (child instanceof ViewGroup) {
                propagateLanguageToViewGroup((ViewGroup) child, language);
            }
        }
    }
}

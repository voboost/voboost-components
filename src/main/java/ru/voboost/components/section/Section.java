package ru.voboost.components.section;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.voboost.components.button.Button;
import ru.voboost.components.button.ButtonStyle;
import ru.voboost.components.buttons.Buttons;
import ru.voboost.components.buttons.ButtonConfig;
import ru.voboost.components.checkbox.Checkbox;
import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.radio.Radio;
import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.select.Select;
import ru.voboost.components.select.SelectOption;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * Section component — titled container with rounded corners and gradient
 * header.
 *
 * <p>
 * ViewGroup that draws a title bar with optional gradient overlay
 * and a content area for child views (e.g. Radio).
 *
 * <p>
 * Usage:
 *
 * <pre>
 * Section section = new Section(context);
 * section.setTheme(Theme.FREE_DARK);
 * section.setLanguage(Language.EN);
 * section.setTitle(Map.of("en", "Language Selection", "ru", "Выбор языка"));
 * section.addRadio(buttons, selectedValue, theme, language);
 * </pre>
 */
public class Section extends ViewGroup implements IThemable, ILocalizable {

    /**
     * Callback for title-checkbox checked state changes.
     */
    public interface OnTitleCheckedChangeListener {
        void onCheckedChange(boolean isChecked);
    }

    /**
     * A single content block for the section info popup.
     * Contains an optional title and required text.
     */
    public static class PopupBlock {
        private final Map<String, String> title;
        private final Map<String, String> text;

        public PopupBlock(@Nullable Map<String, String> title, Map<String, String> text) {
            this.title = title;
            this.text = text;
        }

        @Nullable
        public Map<String, String> getTitle() {
            return title;
        }

        public Map<String, String> getText() {
            return text;
        }
    }

    // Data
    private Map<String, String> title;

    // Theme and Language
    private Theme currentTheme;
    private Language currentLanguage;

    // Children management
    private final List<View> children = new ArrayList<>();

    // Title-checkbox (collapsible mode)
    private Checkbox titleCheckbox;
    private OnTitleCheckedChangeListener onTitleCheckedChangeListener;

    // Info popup + (i) icon
    private List<PopupBlock> popupBlocks;
    private final RectF infoIconHitRect = new RectF();
    private SectionPopup popup;

    // Info icon: bitmap loaded from classpath, plus an optional tint paint for light themes
    private static volatile Bitmap infoIconBitmap;
    private Paint infoIconBitmapPaint;

    // Section-level padding (content area inside title bar)
    private Integer paddingTop;
    private Integer paddingBottom;
    private boolean includeBottomMargin = true;

    // Compact width mode (narrower sections for side-image panels)
    private boolean compactWidth = false;

    // Paints
    private Paint backgroundPaint;
    private Paint titleGradientPaint;
    private Paint titlePaint;

    // Drawing paths for rounded corners
    private Path titleBarPath;

    // Reusable drawing objects (performance optimization)
    private final RectF drawRectF = new RectF();
    private final RectF sectionRect = new RectF(); // Reusable for onDraw
    private final float[] titleRadii = new float[8];

    // Cached gradient (performance optimization)
    private LinearGradient titleGradient;
    private float cachedGradientLeft = Float.NaN;
    private float cachedGradientRight = Float.NaN;

    // Calculated dimensions
    private int titleBarHeight;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public Section(Context context) {
        super(context);
        init(context);
    }

    public Section(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Section(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Section(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    private void init(Context context) {
        // Enable drawing for ViewGroup
        setWillNotDraw(false);

        // Enable hardware acceleration for better performance
        setLayerType(LAYER_TYPE_HARDWARE, null);

        // Disable clipping to allow children (like Radio) to be shifted via translation
        // without being cut off by Section padding
        setClipChildren(false);
        setClipToPadding(false);

        // Initialize paints
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titleGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextSize(SectionTheme.TITLE_TEXT_SIZE);
        titlePaint.setTextAlign(Paint.Align.LEFT);
        // Font.getBold() never returns null, throws RuntimeException if font file not found
        titlePaint.setTypeface(Font.getBold(context, ""));

        // Initialize paths
        titleBarPath = new Path();

        infoIconBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    /**
     * Sets the title for the section with localized labels.
     *
     * @param title Map of language code to localized title text
     * @throws IllegalArgumentException if title is null or empty
     */
    public void setTitle(Map<String, String> title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        // Equality check to avoid unnecessary work on recomposition
        if (java.util.Objects.equals(this.title, title)) {
            return;
        }

        this.title = title;
        calculateTitleBarHeight();
        requestLayout();
        invalidate();

        if (titleCheckbox != null) {
            titleCheckbox.setLabel(title);
            titleCheckbox.setLabelBold(true);
        }
    }

    /**
     * Returns the Map of localized titles.
     *
     * @return Map of language code to title text
     */
    public Map<String, String> getTitle() {
        return title;
    }

    /**
     * Returns the title text for the current language.
     *
     * @return the title text, or empty string if no title is set
     */
    public String getTitleText() {
        if (title == null || currentLanguage == null) {
            return "";
        }

        String languageCode = currentLanguage.getCode();
        if (languageCode == null) {
            return "";
        }

        String text = title.get(languageCode);
        return text != null ? text : "";
    }

    // ============================================================
    // TITLE CHECKBOX API
    // ============================================================

    /**
     * Enables checkbox-as-title mode and sets the initial checked state.
     * The Section title is used as the checkbox label.
     * When unchecked, the section is collapsed (only the title bar is shown,
     * content children are hidden).
     *
     * @param checked initial checked state
     */
    public void setTitleCheckbox(boolean checked) {
        setTitleCheckbox(checked, this.onTitleCheckedChangeListener);
    }

    /**
     * Enables checkbox-as-title mode and sets the initial checked state and listener.
     *
     * @param checked  initial checked state
     * @param listener callback invoked when the user toggles the checkbox
     */
    public void setTitleCheckbox(boolean checked, @Nullable OnTitleCheckedChangeListener listener) {
        if (titleCheckbox == null) {
            titleCheckbox = new Checkbox(getContext());
            if (currentTheme != null) {
                titleCheckbox.setTheme(currentTheme);
            }
            if (currentLanguage != null) {
                titleCheckbox.setLanguage(currentLanguage);
            }
            if (title != null) {
                titleCheckbox.setLabel(title);
                titleCheckbox.setLabelBold(true);
            }
            super.addView(titleCheckbox, 0);
            titleCheckbox.setOnCheckedChangeListener(isChecked -> {
                applyCollapsedVisibility();
                requestLayout();
                invalidate();
                if (onTitleCheckedChangeListener != null) {
                    onTitleCheckedChangeListener.onCheckedChange(isChecked);
                }
            });
        }
        this.onTitleCheckedChangeListener = listener;
        titleCheckbox.setChecked(checked);
        applyCollapsedVisibility();
        requestLayout();
        invalidate();
    }

    /**
     * Removes the title-checkbox and reverts to text-only title.
     */
    public void clearTitleCheckbox() {
        if (titleCheckbox != null) {
            super.removeView(titleCheckbox);
            titleCheckbox = null;
        }
        applyCollapsedVisibility();
        requestLayout();
        invalidate();
    }

    /**
     * Returns true when the section is in checkbox-as-title mode.
     */
    public boolean hasTitleCheckbox() {
        return titleCheckbox != null;
    }

    /**
     * Returns the current checked state of the title-checkbox.
     * Returns false when {@link #hasTitleCheckbox()} is false.
     */
    public boolean isTitleChecked() {
        return titleCheckbox != null && titleCheckbox.isChecked();
    }

    /**
     * Returns true when the section is collapsed (title-checkbox enabled and unchecked).
     */
    public boolean isCollapsed() {
        return hasTitleCheckbox() && !isTitleChecked();
    }

    /**
     * Sets the listener invoked when the title-checkbox state changes.
     */
    public void setOnTitleCheckedChangeListener(@Nullable OnTitleCheckedChangeListener listener) {
        this.onTitleCheckedChangeListener = listener;
    }

    // ============================================================
    // INFO POPUP API
    // ============================================================

    /**
     * Sets popup content as a list of blocks. Each block can have an optional title and text.
     * When non-null and non-empty, a small (i) icon is drawn after the title;
     * tapping it opens a popup with this content.
     *
     * @param blocks list of content blocks, or null/empty to remove
     */
    public void setPopupText(@Nullable List<PopupBlock> blocks) {
        if (blocks != null && blocks.isEmpty()) {
            this.popupBlocks = null;
        } else {
            this.popupBlocks = blocks;
        }
        invalidate();
    }

    /**
     * Convenience method for a single text block without a title.
     *
     * @param infoText localized info text, or null/empty to remove
     */
    public void setPopupText(@Nullable Map<String, String> infoText) {
        if (infoText == null || infoText.isEmpty()) {
            this.popupBlocks = null;
        } else {
            this.popupBlocks = java.util.Collections.singletonList(
                    new PopupBlock(null, infoText));
        }
        invalidate();
    }

    /**
     * Returns the list of popup content blocks (or null if not set).
     */
    @Nullable
    public List<PopupBlock> getPopupBlocks() {
        return popupBlocks;
    }

    /**
     * Returns true when popup content is set.
     */
    public boolean hasPopupText() {
        return popupBlocks != null && !popupBlocks.isEmpty();
    }

    /**
     * Removes the popup content and the (i) icon.
     */
    public void clearPopupText() {
        this.popupBlocks = null;
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
        }
        invalidate();
    }

    /**
     * Programmatically shows the info popup. Has no effect if popup content is not set.
     */
    public void showPopup() {
        if (!hasPopupText() || currentTheme == null || currentLanguage == null) {
            return;
        }
        if (popup == null) {
            popup = new SectionPopup(getContext());
        }
        popup.setTheme(currentTheme);
        popup.setLanguage(currentLanguage);
        popup.setTitle(title);
        popup.setBlocks(popupBlocks);
        popup.show();
    }

    /**
     * Returns the popup overlay root view when the info popup is showing,
     * or null when no popup is currently shown. Used by visual tests to
     * composite the popup on top of the screen bitmap (Dialog windows are
     * not captured by the regular {@code screen.draw()} path).
     */
    @Nullable
    public View getPopupOverlayView() {
        if (popup == null || !popup.isShowing()) {
            return null;
        }
        return popup.getOverlayView();
    }

    /**
     * Applies visibility to all content children based on collapsed state.
     * Called after toggling title-checkbox.
     */
    private void applyCollapsedVisibility() {
        boolean collapsed = isCollapsed();
        for (View child : children) {
            child.setVisibility(collapsed ? GONE : VISIBLE);
        }
    }

    /**
     * Sets the theme for the component.
     *
     * @param theme the theme to apply
     * @throws IllegalArgumentException if theme is null
     */
    @Override
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }

        this.currentTheme = theme;
        updateColors();
        propagateTheme(theme);
        invalidate();
    }

    /**
     * Returns the current theme.
     *
     * @return the current theme
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Sets the language for the component.
     *
     * @param language the language to apply
     * @throws IllegalArgumentException if language is null
     */
    @Override
    public void setLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }

        this.currentLanguage = language;
        calculateTitleBarHeight();
        requestLayout();
        invalidate();

        // Propagate language to all child views
        propagateLanguage(language);
    }

    /**
     * Returns the current language.
     *
     * @return the current language
     */
    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Propagates the theme to all child components.
     *
     * @param theme the theme to propagate
     */
    @Override
    public void propagateTheme(Theme theme) {
        if (theme == null) {
            return;
        }

        // Propagate theme to all child views recursively
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child instanceof IThemable) {
                IThemable themable = (IThemable) child;

                themable.setTheme(theme);
                themable.propagateTheme(theme);
            } else if (child instanceof ViewGroup) {
                // Recursively propagate theme to ViewGroup children
                propagateThemeToViewGroup((ViewGroup) child, theme);
            }
        }
    }

    /**
     * Recursively propagates theme to all IThemable descendants within a ViewGroup.
     *
     * @param viewGroup the ViewGroup to traverse
     * @param theme     the theme to propagate
     */
    private void propagateThemeToViewGroup(ViewGroup viewGroup, Theme theme) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            if (child instanceof IThemable) {
                IThemable themable = (IThemable) child;

                themable.setTheme(theme);
                themable.propagateTheme(theme);
            } else if (child instanceof ViewGroup) {
                // Recursive call for nested ViewGroups
                propagateThemeToViewGroup((ViewGroup) child, theme);
            }
        }
    }

    /**
     * Propagates the language to all child components.
     *
     * @param language the language to propagate
     */
    @Override
    public void propagateLanguage(Language language) {
        if (language == null) {
            return;
        }

        // Propagate language to all child views recursively
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child instanceof ILocalizable) {
                ILocalizable localizable = (ILocalizable) child;

                localizable.setLanguage(language);
                localizable.propagateLanguage(language);
            } else if (child instanceof ViewGroup) {
                // Recursively propagate language to ViewGroup children
                propagateLanguageToViewGroup((ViewGroup) child, language);
            }
        }
    }

    /**
     * Recursively propagates language to all ILocalizable descendants within a ViewGroup.
     *
     * @param viewGroup the ViewGroup to traverse
     * @param language  the language to propagate
     */
    private void propagateLanguageToViewGroup(ViewGroup viewGroup, Language language) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            if (child instanceof ILocalizable) {
                ILocalizable localizable = (ILocalizable) child;

                localizable.setLanguage(language);
                localizable.propagateLanguage(language);
            } else if (child instanceof ViewGroup) {
                // Recursive call for nested ViewGroups
                propagateLanguageToViewGroup((ViewGroup) child, language);
            }
        }
    }

    // ============================================================
    // MEASUREMENT
    // ============================================================

    private void calculateTitleBarHeight() {
        // Title bar height is fixed at 98px to accommodate:
        // - 32px text size (TITLE_TEXT_SIZE from SectionTheme)
        // - 25px top margin (TITLE_MARGIN_TOP)
        // - 25px bottom spacing for visual balance
        // - 16px additional padding for multiline text support
        // Total: 32 + 25 + 25 + 16 = 98px
        titleBarHeight = 98;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        // Section uses fixed width
        int sectionWidth = compactWidth ? SectionTheme.COMPACT_SECTION_WIDTH : SectionTheme.SECTION_WIDTH;

        // Calculate title bar height
        calculateTitleBarHeight();

        // Measure title-checkbox first if present (it's child index 0 but lives in the title bar)
        if (titleCheckbox != null) {
            int tcWidthSpec = MeasureSpec.makeMeasureSpec(sectionWidth, MeasureSpec.AT_MOST);
            int tcHeightSpec = MeasureSpec.makeMeasureSpec(titleBarHeight, MeasureSpec.AT_MOST);
            titleCheckbox.measure(tcWidthSpec, tcHeightSpec);
        }

        // Collapsed mode: only title bar + bottom margin
        if (isCollapsed()) {
            int totalHeight = titleBarHeight + (includeBottomMargin ? SectionTheme.BOTTOM_MARGIN : 0);
            setMeasuredDimension(
                    resolveSize(sectionWidth + 2 * SectionTheme.HORIZONTAL_MARGIN, widthMeasureSpec),
                    resolveSize(totalHeight, heightMeasureSpec));
            return;
        }

        // Total height starts with title bar + content padding top (or explicit padding top)
        int topSpacing = paddingTop != null ? paddingTop : SectionTheme.CONTENT_PADDING_TOP;
        int totalHeight = titleBarHeight + topSpacing;

        // Content width = section width minus internal horizontal padding
        int contentWidth = sectionWidth - 2 * SectionTheme.CONTENT_PADDING_HORIZONTAL;

        // Measure children
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == titleCheckbox) continue;
            if (child.getVisibility() == GONE)
                continue;

            // Give children the content width (section width minus horizontal padding)
            int childWidthSpec = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY);

            // Respect a child's explicit fixed height. When the child's
            // layoutParams.height is a concrete pixel value (not MATCH_PARENT
            // or WRAP_CONTENT), measure it at exactly that height. Otherwise
            // let the child wrap its content with an UNSPECIFIED height spec.
            // This is required so a fixed-height child (e.g. the daemon-status
            // diagnostic TextView with a 555px height) is measured at its
            // requested height instead of its text-content height.
            int childHeightSpec;
            ViewGroup.LayoutParams childLp = child.getLayoutParams();
            if (childLp != null && childLp.height > 0) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(childLp.height, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }

            child.measure(childWidthSpec, childHeightSpec);

            // Add child height plus margins (if layout params have margins)
            int childHeight = child.getMeasuredHeight();
            ViewGroup.LayoutParams params = child.getLayoutParams();
            if (params instanceof MarginLayoutParams) {
                MarginLayoutParams lp = (MarginLayoutParams) params;
                childHeight += lp.topMargin + lp.bottomMargin;
            }
            totalHeight += childHeight;
        }

        // Add content padding bottom (or explicit padding) + bottom margin (spacing after section)
        int bottomSpacing = paddingBottom != null
            ? paddingBottom
            : SectionTheme.CONTENT_PADDING_BOTTOM;
        totalHeight += bottomSpacing + (includeBottomMargin ? SectionTheme.BOTTOM_MARGIN : 0);

        // The measured width includes horizontal margins (so parent knows total space
        // needed)
        setMeasuredDimension(
                resolveSize(sectionWidth + 2 * SectionTheme.HORIZONTAL_MARGIN, widthMeasureSpec),
                resolveSize(totalHeight, heightMeasureSpec));
    }

    // ============================================================
    // LAYOUT
    // ============================================================

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int sectionLeft = SectionTheme.HORIZONTAL_MARGIN;

        // Layout title-checkbox inside the title bar (vertically centered)
        if (titleCheckbox != null) {
            int tcWidth = titleCheckbox.getMeasuredWidth();
            int tcHeight = titleCheckbox.getMeasuredHeight();
            int tcLeft = sectionLeft + SectionTheme.TITLE_MARGIN_START + 1;  // 1px right
            int tcTop = (titleBarHeight - tcHeight) / 2 - 1;  // 1px up
            titleCheckbox.layout(tcLeft, tcTop, tcLeft + tcWidth, tcTop + tcHeight);
        }

        // Collapsed: skip content children
        if (isCollapsed()) {
            return;
        }

        int topSpacing = paddingTop != null ? paddingTop : SectionTheme.CONTENT_PADDING_TOP;
        int currentTop = titleBarHeight + topSpacing;
        int childLeft = sectionLeft + SectionTheme.CONTENT_PADDING_HORIZONTAL;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == titleCheckbox) continue;
            if (child.getVisibility() == GONE)
                continue;

            int leftMargin = 0, topMargin = 0, bottomMargin = 0;
            ViewGroup.LayoutParams params = child.getLayoutParams();
            if (params instanceof MarginLayoutParams) {
                MarginLayoutParams lp = (MarginLayoutParams) params;
                leftMargin = lp.leftMargin;
                topMargin = lp.topMargin;
                bottomMargin = lp.bottomMargin;
            }

            int childRight = childLeft + child.getMeasuredWidth();
            int childBottom = currentTop + child.getMeasuredHeight();

            child.layout(childLeft + leftMargin, currentTop + topMargin, childRight + leftMargin, childBottom + topMargin);
            currentTop = childBottom + topMargin + bottomMargin;
        }
    }

    // ============================================================
    // DRAWING
    // ============================================================

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentTheme == null || currentLanguage == null) {
            return;
        }

        int height = getHeight();

        float sectionLeft = SectionTheme.HORIZONTAL_MARGIN;
        float sectionRight = sectionLeft + (compactWidth ? SectionTheme.COMPACT_SECTION_WIDTH : SectionTheme.SECTION_WIDTH);
        float sectionTop = 0;
        int bottomMargin = includeBottomMargin ? SectionTheme.BOTTOM_MARGIN : 0;
        float sectionBottom = height - bottomMargin;
        float radius = SectionTheme.CORNER_RADIUS;

        // Draw full section background with all corners rounded
        backgroundPaint.setColor(SectionTheme.getBackground(currentTheme));
        sectionRect.set(sectionLeft, sectionTop, sectionRight, sectionBottom);
        canvas.drawRoundRect(sectionRect, radius, radius, backgroundPaint);

        // Draw title gradient overlay (only for dark themes)
        if (SectionTheme.hasTitleGradient(currentTheme)) {
            drawTitleGradient(canvas, sectionLeft, sectionRight, sectionTop, radius);
        }

        // Draw title text
        drawTitle(canvas, sectionLeft);
    }

    private void drawTitle(Canvas canvas, float sectionLeft) {
        // When title-checkbox is enabled, the Checkbox child renders the label itself
        float trailingX;
        if (hasTitleCheckbox()) {
            trailingX = titleCheckbox.getRight();
        } else {
            String titleText = getTitleText();
            if (titleText.isEmpty()) {
                trailingX = sectionLeft + SectionTheme.TITLE_MARGIN_START;
            } else {
                titlePaint.setTypeface(Font.getBold(getContext(), titleText));
                float titleX = sectionLeft + SectionTheme.TITLE_MARGIN_START;
                float titleY = SectionTheme.TITLE_MARGIN_TOP + getTextVerticalOffset() + 4f;
                titlePaint.setColor(SectionTheme.getTitleTextColor(currentTheme));
                canvas.drawText(titleText, titleX, titleY, titlePaint);
                trailingX = titleX + titlePaint.measureText(titleText);
            }
        }

        if (hasPopupText()) {
            drawInfoIcon(canvas, trailingX);
        } else {
            infoIconHitRect.setEmpty();
        }
    }

    private void drawInfoIcon(Canvas canvas, float trailingX) {
        Bitmap bitmap = getInfoIconBitmap();
        if (bitmap == null) {
            infoIconHitRect.setEmpty();
            return;
        }

        float size = SectionTheme.INFO_ICON_SIZE;
        float left = trailingX + SectionTheme.INFO_ICON_GAP;
        float top = (titleBarHeight - size) / 2f;

        int tint = SectionTheme.getInfoIconTint(currentTheme);
        if (tint != 0) {
            infoIconBitmapPaint.setColorFilter(new PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN));
        } else {
            infoIconBitmapPaint.setColorFilter(null);
        }
        canvas.drawBitmap(bitmap, left, top, infoIconBitmapPaint);

        float inflate = SectionTheme.INFO_ICON_HIT_INFLATE;
        infoIconHitRect.set(
                left - inflate,
                top - inflate,
                left + size + inflate,
                top + size + inflate);
    }

    private static Bitmap getInfoIconBitmap() {
        Bitmap b = infoIconBitmap;
        if (b == null) {
            synchronized (Section.class) {
                b = infoIconBitmap;
                if (b == null) {
                    b = loadBitmap("SectionInfoIcon.png");
                    infoIconBitmap = b;
                }
            }
        }
        return b;
    }

    private static Bitmap loadBitmap(String name) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        try (InputStream in = Section.class.getResourceAsStream(name)) {
            if (in == null) {
                return null;
            }
            return BitmapFactory.decodeStream(in, null, opts);
        } catch (IOException e) {
            return null;
        }
    }

    private float getTextVerticalOffset() {
        // Position text baseline correctly
        Paint.FontMetrics fm = titlePaint.getFontMetrics();
        return -fm.ascent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (hasPopupText() && !infoIconHitRect.isEmpty()) {
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    && infoIconHitRect.contains(x, y)) {
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP
                    && infoIconHitRect.contains(x, y)) {
                showPopup();
                performClick();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private void drawTitleGradient(
            Canvas canvas, float sectionLeft, float sectionRight, float sectionTop, float radius) {
        // Horizontal gradient: darker on the left, fading to section background on the
        // right
        int gradientStart = SectionTheme.getTitleGradientStart(currentTheme);
        int gradientEnd = SectionTheme.getTitleGradientEnd(currentTheme);

        float gradientBottom = sectionTop + titleBarHeight;

        // Only recreate gradient if bounds changed
        if (titleGradient == null || cachedGradientLeft != sectionLeft || cachedGradientRight != sectionRight) {
            titleGradient = new LinearGradient(
                    sectionLeft,
                    0,
                    sectionRight,
                    0,
                    gradientStart,
                    gradientEnd,
                    Shader.TileMode.CLAMP);
            cachedGradientLeft = sectionLeft;
            cachedGradientRight = sectionRight;
        }
        titleGradientPaint.setShader(titleGradient);

        // Draw gradient only in the title area with top rounded corners
        titleBarPath.reset();
        titleRadii[0] = radius; titleRadii[1] = radius;
        titleRadii[2] = radius; titleRadii[3] = radius;
        titleRadii[4] = 0; titleRadii[5] = 0;
        titleRadii[6] = 0; titleRadii[7] = 0;
        drawRectF.set(sectionLeft, sectionTop, sectionRight, gradientBottom);
        titleBarPath.addRoundRect(drawRectF, titleRadii, Path.Direction.CW);
        canvas.drawPath(titleBarPath, titleGradientPaint);
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private void updateColors() {
        if (currentTheme == null) {
            return;
        }

        backgroundPaint.setColor(SectionTheme.getBackground(currentTheme));
        titlePaint.setColor(SectionTheme.getTitleTextColor(currentTheme));

        // Invalidate cached gradient when theme changes
        titleGradient = null;
        cachedGradientLeft = Float.NaN;
        cachedGradientRight = Float.NaN;
    }

    // ============================================================
    // ADD COMPONENT API - typed methods only, NO generic add(View)
    // ============================================================

    /**
     * Adds a Checkbox component to this section with spacing applied.
     */
    public Checkbox addCheckbox(Checkbox checkbox) {
        int left = checkbox.isMarginSet() ? checkbox.getMarginLeft() : 1;
        int top = checkbox.isMarginSet() ? checkbox.getMarginTop() : 3;
        int right = checkbox.isMarginSet() ? checkbox.getMarginRight() : 0;
        int bottom = checkbox.isMarginSet() ? checkbox.getMarginBottom() : (checkbox.hasDescription() ? 35 : 42);

        MarginLayoutParams params = new MarginLayoutParams(
                MarginLayoutParams.WRAP_CONTENT,
                MarginLayoutParams.WRAP_CONTENT);
        params.leftMargin = left;
        params.topMargin = top;
        params.rightMargin = right;
        params.bottomMargin = bottom;

        addView(checkbox, params);
        children.add(checkbox);
        return checkbox;
    }

    /**
     * Adds a Radio component to this section with spacing applied.
     */
    public Radio addRadio(Radio radio) {
        int left = radio.isMarginSet() ? radio.getMarginLeft() : 2;
        int top = radio.isMarginSet() ? radio.getMarginTop() : 0;
        int right = radio.isMarginSet() ? radio.getMarginRight() : 0;
        int bottom = radio.isMarginSet() ? radio.getMarginBottom() : 42;

        MarginLayoutParams params = new MarginLayoutParams(
                MarginLayoutParams.WRAP_CONTENT,
                MarginLayoutParams.WRAP_CONTENT);
        params.leftMargin = left;
        params.topMargin = top;
        params.rightMargin = right;
        params.bottomMargin = bottom;

        addView(radio, params);
        children.add(radio);
        return radio;
    }

    /**
     * Adds a Button component to this section with spacing applied.
     */
    public Button addButton(Button button) {
        int left = button.isMarginSet() ? button.getMarginLeft() : 0;
        int top = button.isMarginSet() ? button.getMarginTop() : 0;
        int right = button.isMarginSet() ? button.getMarginRight() : 0;
        int bottom = button.isMarginSet() ? button.getMarginBottom() : 38;

        MarginLayoutParams params = new MarginLayoutParams(
                MarginLayoutParams.WRAP_CONTENT,
                MarginLayoutParams.WRAP_CONTENT);
        params.leftMargin = left;
        params.topMargin = top;
        params.rightMargin = right;
        params.bottomMargin = bottom;

        addView(button, params);
        children.add(button);
        return button;
    }

    /**
     * Adds a Select component to this section with spacing applied.
     */
    public Select addSelect(Select select) {
        int left = select.isMarginSet() ? select.getMarginLeft() : 0;
        int top = select.isMarginSet() ? select.getMarginTop() : 0;
        int right = select.isMarginSet() ? select.getMarginRight() : 0;
        int bottom = select.isMarginSet() ? select.getMarginBottom() : 42;

        MarginLayoutParams params = new MarginLayoutParams(
                MarginLayoutParams.WRAP_CONTENT,
                MarginLayoutParams.WRAP_CONTENT);
        params.leftMargin = left;
        params.topMargin = top;
        params.rightMargin = right;
        params.bottomMargin = bottom;

        addView(select, params);
        children.add(select);
        return select;
    }

    /**
     * Adds a Buttons component to this section with spacing applied.
     */
    public Buttons addButtons(Buttons buttons) {
        int left = buttons.isMarginSet() ? buttons.getMarginLeft() : 0;
        int top = buttons.isMarginSet() ? buttons.getMarginTop() : 0;
        int right = buttons.isMarginSet() ? buttons.getMarginRight() : 0;
        int bottom = buttons.isMarginSet() ? buttons.getMarginBottom() : 42;

        MarginLayoutParams params = new MarginLayoutParams(
                MarginLayoutParams.WRAP_CONTENT,
                MarginLayoutParams.WRAP_CONTENT);
        params.leftMargin = left;
        params.topMargin = top;
        params.rightMargin = right;
        params.bottomMargin = bottom;

        addView(buttons, params);
        children.add(buttons);
        return buttons;
    }

    /**
     * Returns the list of children added to this section.
     */
    @NonNull
    public List<View> getChildren() {
        return new ArrayList<>(children);
    }

    /**
     * Sets the section-level top padding.
     *
     * @param top the top padding in pixels
     */
    public void setPaddingTop(Integer top) {
        this.paddingTop = top;
    }

    /**
     * Returns the section-level top padding.
     *
     * @return the top padding in pixels, or null if not set
     */
    public Integer getSectionPaddingTop() {
        return paddingTop;
    }

    /**
     * Sets the section-level bottom padding.
     *
     * @param bottom the bottom padding in pixels
     */
    public void setPaddingBottom(Integer bottom) {
        this.paddingBottom = bottom;
    }

    /**
     * Returns the section-level bottom padding.
     *
     * @return the bottom padding in pixels, or null if not set
     */
    public Integer getSectionPaddingBottom() {
        return paddingBottom;
    }

    /**
     * When false, the section omits {@link SectionTheme#BOTTOM_MARGIN} from its measured height
     * and draws its background to the full bottom edge. Used by Panel to prevent margin stacking
     * when the panel already has its own bottom padding.
     */
    public void setIncludeBottomMargin(boolean include) {
        if (this.includeBottomMargin != include) {
            this.includeBottomMargin = include;
            requestLayout();
            invalidate();
        }
    }

    /** Returns whether this section includes the bottom margin in its height. */
    public boolean isIncludeBottomMargin() {
        return includeBottomMargin;
    }

    public void setCompactWidth(boolean compactWidth) {
        this.compactWidth = compactWidth;
        requestLayout();
        invalidate();
    }

    public boolean isCompactWidth() {
        return compactWidth;
    }

    /**
     * Sets the section-level left padding.
     * Section uses fixed horizontal padding, this method has no effect.
     *
     * @param left the left padding (ignored)
     */
    public void setPaddingLeft(Integer left) {
        // Section uses fixed horizontal padding
    }

    /**
     * Sets the section-level right padding.
     * Section uses fixed horizontal padding, this method has no effect.
     *
     * @param right the right padding (ignored)
     */
    public void setPaddingRight(Integer right) {
        // Section uses fixed horizontal padding
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Cleanup gradient shader to prevent memory leak
        if (titleGradientPaint != null) {
            titleGradientPaint.setShader(null);
        }
        titleGradient = null;
    }

    // ============================================================
    // BUILDER API
    // ============================================================

    /**
     * Creates a new Builder for Section.
     *
     * @param context the Android context
     * @param theme the theme to apply
     * @param language the language to apply
     * @param title Map of language code to localized title text
     * @return a new Builder instance
     */
    @NonNull
    public static Builder create(@NonNull android.content.Context context,
                                 @NonNull Theme theme,
                                 @NonNull Language language,
                                 @NonNull java.util.Map<String, String> title) {
        return new Builder(context, theme, language, title);
    }

    /**
     * Builder for creating Section instances with a fluent API.
     */
    public static class Builder {
        private final android.content.Context context;
        private final Theme theme;
        private final Language language;
        private final java.util.Map<String, String> title;
        private Integer paddingTop;
        private Integer paddingBottom;
        private Integer paddingLeft;
        private Integer paddingRight;
        private Boolean titleCheckboxChecked;
        private OnTitleCheckedChangeListener onTitleCheckedChange;
        private java.util.Map<String, String> infoText;
        private java.util.List<PopupBlock> infoBlocks;

        private Builder(android.content.Context context, Theme theme, Language language,
                        java.util.Map<String, String> title) {
            this.context = context;
            this.theme = theme;
            this.language = language;
            this.title = title;
        }

        /**
         * Sets the section-level top padding.
         *
         * @param top the top padding in pixels
         * @return this Builder instance
         */
        @NonNull
        public Builder paddingTop(@Nullable Integer top) {
            this.paddingTop = top;
            return this;
        }

        /**
         * Sets the section-level bottom padding.
         *
         * @param bottom the bottom padding in pixels
         * @return this Builder instance
         */
        @NonNull
        public Builder paddingBottom(@Nullable Integer bottom) {
            this.paddingBottom = bottom;
            return this;
        }

        /**
         * Sets the section-level left padding.
         *
         * @param left the left padding in pixels
         * @return this Builder instance
         */
        @NonNull
        public Builder paddingLeft(@Nullable Integer left) {
            this.paddingLeft = left;
            return this;
        }

        /**
         * Sets the section-level right padding.
         *
         * @param right the right padding in pixels
         * @return this Builder instance
         */
        @NonNull
        public Builder paddingRight(@Nullable Integer right) {
            this.paddingRight = right;
            return this;
        }

        /**
         * Enables checkbox-as-title with the given initial checked state.
         */
        @NonNull
        public Builder titleCheckbox(boolean checked) {
            this.titleCheckboxChecked = checked;
            return this;
        }

        /**
         * Enables checkbox-as-title with the given initial checked state and listener.
         */
        @NonNull
        public Builder titleCheckbox(boolean checked, @Nullable OnTitleCheckedChangeListener listener) {
            this.titleCheckboxChecked = checked;
            this.onTitleCheckedChange = listener;
            return this;
        }

        /**
         * Sets the info text shown when the user taps the (i) icon (single block without title).
         */
        @NonNull
        public Builder infoText(@Nullable java.util.Map<String, String> text) {
            this.infoBlocks = null;
            this.infoText = text;
            return this;
        }

        /**
         * Sets the info content as a list of blocks (each with optional title and text).
         */
        @NonNull
        public Builder infoText(@Nullable java.util.List<PopupBlock> blocks) {
            this.infoText = null;
            this.infoBlocks = blocks;
            return this;
        }

        /**
         * Adds a single content block to the info popup.
         *
         * @param title localized block title (nullable)
         * @param text localized block text
         */
        @NonNull
        public Builder infoBlock(@Nullable java.util.Map<String, String> title, java.util.Map<String, String> text) {
            if (infoBlocks == null) {
                infoBlocks = new java.util.ArrayList<>();
            }
            infoBlocks.add(new PopupBlock(title, text));
            return this;
        }

        /**
         * Builds and returns the Section instance.
         *
         * @return a new Section instance
         */
        @NonNull
        public Section build() {
            Section section = new Section(context);
            section.setTitle(title);
            section.setTheme(theme);
            section.setLanguage(language);
            if (paddingTop != null) {
                section.setPaddingTop(paddingTop);
            }
            if (paddingBottom != null) {
                section.setPaddingBottom(paddingBottom);
            }
            if (paddingLeft != null) {
                section.setPaddingLeft(paddingLeft);
            }
            if (paddingRight != null) {
                section.setPaddingRight(paddingRight);
            }
            if (infoText != null) {
                section.setPopupText(infoText);
            } else if (infoBlocks != null) {
                section.setPopupText(infoBlocks);
            }
            if (titleCheckboxChecked != null) {
                section.setTitleCheckbox(titleCheckboxChecked, onTitleCheckedChange);
            }
            return section;
        }
    }
}

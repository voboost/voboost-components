package ru.voboost.components.screen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;
import ru.voboost.components.toast.Toast;
import ru.voboost.components.toast.ToastTheme;

/**
 * Screen component - A full-screen container for automotive applications.
 *
 * <p>
 * This component provides a full-screen container with:
 * <ul>
 * <li>Multi-theme support (Free/Dreamer, Light/Dark)</li>
 * <li>Container layout management for Tabs and Panel components</li>
 * <li>Screen lift functionality for automotive use</li>
 * </ul>
 *
 * <p>
 * Usage:
 *
 * <pre>
 * Screen screen = new Screen(context);
 * screen.setTheme(Theme.FREE_LIGHT);
 * </pre>
 */
public class Screen extends ViewGroup implements IThemable, ILocalizable {
    // Constants
    public static final int SCREEN_LOWERED = 1;
    public static final int SCREEN_RAISED = 2;

    // Theme & Language
    private Theme currentTheme;
    private Language currentLanguage;

    // Offset fields (using defaults from ScreenTheme)
    private int offsetX = ScreenTheme.DEFAULT_OFFSET_X;
    private int offsetY = ScreenTheme.DEFAULT_OFFSET_Y;
    private int gapX = ScreenTheme.DEFAULT_GAP_X;

    // Compact panel mode: full height, narrow width, side image
    private boolean compactPanel = false;

    // Screen lift state
    private int screenLiftState = SCREEN_RAISED;
    private OnScreenLiftListener onScreenLiftListener;

    // Component references
    private Tabs tabs;
    private Panel[] panels;
    private int activePanelIndex = -1;
    private ScreenView tabsScrollView;
    private ScreenView[] panelWrappers;
    private Toast currentToast;
    private Tabs.OnTabChangeListener previousTabChangeListener;

    private final Paint imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    // Animation state
    private AnimatorSet currentPanelTransition;
    private ScreenView animatingOutWrapper;

    // ============================================================
    // INTERFACES
    // ============================================================

    /**
     * Listener interface for screen lift state changes.
     */
    public interface OnScreenLiftListener {
        /**
         * Called when the screen lift state changes.
         *
         * @param state the new screen lift state (SCREEN_LOWERED or SCREEN_RAISED)
         */
        void onScreenLift(int state);
    }

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public Screen(Context context) {
        super(context);
        init(context);
    }

    public Screen(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Screen(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Screen(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    private Context savedContext;

    private void init(Context context) {
        this.savedContext = context;
        // Disable clip for panel transition animation and child overflow
        setClipChildren(false);
        setClipToPadding(false);
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    /**
     * Returns the available content height in pixels.
     * Currently fixed at 720px; will account for screen lift state in future.
     */
    public int getAvailableHeight() {
        return 720;
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
        setBackground(ScreenTheme.getBackgroundDrawable(savedContext, theme));
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
        propagateLanguage(language);
        invalidate();
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
     * Sets the X offset for the screen content.
     *
     * @param offsetX the X offset in pixels (must be non-negative)
     * @throws IllegalArgumentException if offsetX is negative
     */
    public void setOffsetX(int offsetX) {
        if (offsetX < 0) {
            throw new IllegalArgumentException("OffsetX cannot be negative");
        }

        this.offsetX = offsetX;
        requestLayout();
    }

    /**
     * Returns the current X offset.
     *
     * @return the X offset in pixels
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Sets the Y offset for the screen content.
     *
     * @param offsetY the Y offset in pixels (must be non-negative)
     * @throws IllegalArgumentException if offsetY is negative
     */
    public void setOffsetY(int offsetY) {
        if (offsetY < 0) {
            throw new IllegalArgumentException("OffsetY cannot be negative");
        }

        this.offsetY = offsetY;
        if (tabs != null) {
            tabs.setTopPadding(offsetY);
        }
        requestLayout();
    }

    /**
     * Returns the current Y offset.
     *
     * @return the Y offset in pixels
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Sets the horizontal gap between Tabs and Panel.
     *
     * @param gapX the gap in pixels (must be non-negative)
     * @throws IllegalArgumentException if gapX is negative
     */
    public void setGapX(int gapX) {
        if (gapX < 0) {
            throw new IllegalArgumentException("GapX cannot be negative");
        }

        this.gapX = gapX;
        requestLayout();
    }

    /**
     * Returns the current horizontal gap between Tabs and Panel.
     *
     * @return the gap in pixels
     */
    public int getGapX() {
        return gapX;
    }

    public void setCompactPanel(boolean compactPanel) {
        this.compactPanel = compactPanel;
        if (panels != null) {
            for (Panel p : panels) {
                if (p != null) {
                    p.setCompact(compactPanel);
                }
            }
        }
        requestLayout();
    }

    private ScreenView ensurePanelWrapper(int index) {
        if (panelWrappers == null || index < 0 || index >= panelWrappers.length) {
            return null;
        }
        ScreenView w = panelWrappers[index];
        if (w == null) {
            w = new ScreenView(getContext());
            w.setVerticalScrollBarEnabled(false);
            w.addView(panels[index]);
            panelWrappers[index] = w;
        }
        panels[index].setCompact(compactPanel);

        return w;
    }

    public boolean isCompactPanel() {
        return compactPanel;
    }

    /**
     * Sets the Tabs component for the screen.
     *
     * @param tabs the Tabs component to add
     */
    public void setTabs(Tabs tabs) {
        // Remove old listener if exists
        if (this.tabs != null && previousTabChangeListener != null) {
            this.tabs.setOnTabChangeListener(null);
        }

        // Remove old tabs and scroll view if they exist
        if (this.tabs != null) {
            if (tabsScrollView != null) {
                tabsScrollView.removeView(this.tabs);
                removeView(tabsScrollView);
                tabsScrollView = null;
            } else {
                removeView(this.tabs);
            }
        }

        this.tabs = tabs;

        // Add new tabs into a ScreenView wrapper (rubber-band stretch)
        if (tabs != null) {
            tabsScrollView = new ScreenView(getContext());
            tabsScrollView.setVerticalScrollBarEnabled(false);
            tabsScrollView.setClipChildren(false);
            tabsScrollView.setClipToPadding(false);

            // Sync Tabs internal top padding with Screen offsetY so the first
            // tab visually starts at offsetY from the screen top edge.
            tabs.setTopPadding(offsetY);

            tabsScrollView.addView(tabs);
            addView(tabsScrollView);

            // Set listener for tab changes (store reference for cleanup)
            Tabs.OnTabChangeListener newListener = newIndex -> setActivePanel(newIndex);
            previousTabChangeListener = newListener;
            tabs.setOnTabChangeListener(newListener);
        } else {
            previousTabChangeListener = null;
        }

        // Activate the panel for the initially selected tab
        if (tabs != null) {
            int selectedIndex = tabs.getSelectedIndex();
            if (selectedIndex >= 0) {
                setActivePanel(selectedIndex);
            }
        }

        requestLayout();
    }

    /**
     * Returns the current Tabs component.
     *
     * @return the Tabs component, or null if not set
     */
    public Tabs getTabs() {
        return tabs;
    }

    /**
     * Returns the ScrollView containing the Tabs component.
     *
     * @return the ScrollView containing tabs, or null if not set
     */
    public ScreenView getTabsScrollView() {
        return tabsScrollView;
    }

    /**
     * Returns the ScreenView wrapper around the panel at the given index, or
     * null if it has not been instantiated yet (panels are wrapped lazily on
     * first activation).
     */
    public ScreenView getPanelWrapper(int index) {
        if (panelWrappers == null || index < 0 || index >= panelWrappers.length) {
            return null;
        }
        return panelWrappers[index];
    }

    /**
     * Sets the array of panels for the screen.
     *
     * @param panels the array of Panel objects
     */
    public void setPanels(Panel[] panels) {
        // Detach previous wrappers if any
        if (panelWrappers != null) {
            for (ScreenView w : panelWrappers) {
                if (w != null && w.getParent() == this) {
                    super.removeView(w);
                }
            }
        }

        this.panels = panels;
        this.panelWrappers = panels != null ? new ScreenView[panels.length] : null;
        this.activePanelIndex = -1;
    }

    /**
     * Sets the active panel by index with slide animation.
     *
     * <p>Animation direction depends on relative position:
     * <ul>
     *   <li>If new index > old index: new panel slides UP from bottom, old slides UP and exits</li>
     *   <li>If new index < old index: new panel slides DOWN from top, old slides DOWN and exits</li>
     * </ul>
     *
     * <p>This matches the original Voyah implementation which uses 300ms vertical
     * translate animations (anim_slide_bottom_in/top_out and anim_slide_top_in/bottom_out).
     *
     * @param index the index of the panel to activate
     */
    public void setActivePanel(int index) {
        if (panels == null || index < 0 || index >= panels.length) {
            activePanelIndex = -1;
            return;
        }

        if (activePanelIndex == index) {
            // Ensure wrapper is created and padding is fresh
            ensurePanelWrapper(index);
            return;
        }

        int oldIndex = activePanelIndex;
        ScreenView oldWrapper = (oldIndex >= 0 && panelWrappers != null
                && oldIndex < panelWrappers.length) ? panelWrappers[oldIndex] : null;

        // Cancel current animation if any
        cancelPanelTransition();

        // Update index IMMEDIATELY (before animation) so getActivePanel() returns new panel
        activePanelIndex = index;

        // Lazy-create and attach the new wrapper
        ScreenView newWrapper = ensurePanelWrapper(index);
        if (newWrapper.getParent() != this) {
            addView(newWrapper);
        }

        // If no old wrapper (first call) - no animation
        if (oldWrapper == null || oldWrapper.getParent() != this) {
            requestLayout();
            return;
        }

        // Determine animation direction
        boolean goingDown = index > oldIndex;

        // Remember outgoing wrapper for onMeasure/onLayout
        animatingOutWrapper = oldWrapper;

        // Need layout before animation for correct sizes
        requestLayout();

        // Animation distance: wrapper slides by its own height
        int wrapperHeight = compactPanel ? getMeasuredHeight() : getMeasuredHeight() - offsetY;
        if (wrapperHeight <= 0) {
            wrapperHeight = compactPanel ? getHeight() : getHeight() - offsetY;
        }
        if (wrapperHeight <= 0) {
            super.removeView(oldWrapper);
            animatingOutWrapper = null;
            requestLayout();
            return;
        }

        // --- Animate old wrapper (exits) ---
        float oldEndY = goingDown ? -wrapperHeight : wrapperHeight;
        ObjectAnimator oldAnim = ObjectAnimator.ofFloat(
                oldWrapper, "translationY", 0, oldEndY);
        oldAnim.setDuration(ScreenTheme.PANEL_TRANSITION_DURATION);

        // --- Animate new wrapper (enters) ---
        float newStartY = goingDown ? wrapperHeight : -wrapperHeight;
        newWrapper.setTranslationY(newStartY);
        ObjectAnimator newAnim = ObjectAnimator.ofFloat(
                newWrapper, "translationY", newStartY, 0);
        newAnim.setDuration(ScreenTheme.PANEL_TRANSITION_DURATION);

        // --- Start both animations ---
        final ScreenView oldRef = oldWrapper;
        final ScreenView newRef = newWrapper;
        currentPanelTransition = new AnimatorSet();
        currentPanelTransition.playTogether(oldAnim, newAnim);
        currentPanelTransition.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (oldRef.getParent() == Screen.this) {
                    Screen.super.removeView(oldRef);
                }
                oldRef.setTranslationY(0);
                newRef.setTranslationY(0);

                animatingOutWrapper = null;
                currentPanelTransition = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (oldRef.getParent() == Screen.this) {
                    Screen.super.removeView(oldRef);
                }
                oldRef.setTranslationY(0);
                newRef.setTranslationY(0);

                animatingOutWrapper = null;
                currentPanelTransition = null;
            }
        });
        currentPanelTransition.start();
    }

    /**
     * Cancels any running panel transition animation.
     * The old panel is removed and translationY reset.
     */
    private void cancelPanelTransition() {
        if (currentPanelTransition != null && currentPanelTransition.isRunning()) {
            currentPanelTransition.cancel();
            // onAnimationCancel will handle cleanup
        }
    }

    /**
     * Returns the currently active panel.
     *
     * @return the active panel, or null if no panel is active
     */
    public Panel getActivePanel() {
        if (panels == null || activePanelIndex < 0 || activePanelIndex >= panels.length) {
            return null;
        }
        return panels[activePanelIndex];
    }

    /**
     * Returns the array of panels.
     *
     * @return the array of Panel objects
     */
    public Panel[] getPanels() {
        return panels;
    }

    /**
     * Sets the screen lift state.
     *
     * @param state the screen lift state (SCREEN_LOWERED or SCREEN_RAISED)
     * @throws IllegalArgumentException if state is invalid
     */
    public void onScreenLift(int state) {
        if (state != SCREEN_LOWERED && state != SCREEN_RAISED) {
            throw new IllegalArgumentException("Invalid screen lift state: " + state);
        }

        if (this.screenLiftState != state) {
            this.screenLiftState = state;

            // Notify listener if set
            if (onScreenLiftListener != null) {
                onScreenLiftListener.onScreenLift(state);
            }

            // Request layout to reflect state changes
            requestLayout();
        }
    }

    /**
     * Returns the current screen lift state.
     *
     * @return the screen lift state (SCREEN_LOWERED or SCREEN_RAISED)
     */
    public int getScreenLiftState() {
        return screenLiftState;
    }

    /**
     * Sets the screen lift listener.
     *
     * @param listener the listener to set
     */
    public void setOnScreenLiftListener(OnScreenLiftListener listener) {
        this.onScreenLiftListener = listener;
    }

    // ============================================================
    // TOAST MANAGEMENT
    // ============================================================

    /**
     * Shows a toast message at the top of the screen.
     *
     * @param text the message text
     * @param duration auto-dismiss duration in milliseconds (use ToastTheme.DURATION_SHORT or DURATION_LONG)
     */
    public void showToast(String text, long duration) {
        // Dismiss existing toast if any
        if (currentToast != null && currentToast.isShowing()) {
            currentToast.dismiss();
        }

        // Create and configure toast
        currentToast = new Toast(getContext());
        if (currentTheme != null) {
            currentToast.setTheme(currentTheme);
        }
        currentToast.setContent(text);
        currentToast.setDuration(duration);
        currentToast.setOnDismissListener(() -> {
            // Remove toast view only if still attached to this Screen
            if (currentToast != null && currentToast.getParent() == Screen.this) {
                Screen.this.removeToastView(currentToast);
            }
            // Clear listener to prevent memory leak
            if (currentToast != null) {
                currentToast.setOnDismissListener(null);
            }
            currentToast = null;
        });

        // Add toast on top of other children (using super.addView to bypass internal logic)
        super.addView(currentToast);
        requestLayout();
        currentToast.show();
    }

    /**
     * Dismisses the currently shown toast, if any.
     */
    public void dismissToast() {
        if (currentToast != null && currentToast.isShowing()) {
            currentToast.dismiss();
        }
    }

    /**
     * Returns the currently shown toast, or null.
     *
     * @return the current toast, or null if no toast is showing
     */
    public Toast getCurrentToast() {
        return currentToast;
    }

    /**
     * Private helper to remove toast view after dismiss animation completes.
     *
     * @param toast the toast to remove
     */
    private void removeToastView(Toast toast) {
        super.removeView(toast);
    }

    // ============================================================
    // THEME & LANGUAGE PROPAGATION
    // ============================================================

    /**
     * Propagates the theme to all child components recursively.
     * This method updates the theme for tabs, panels, and all nested components.
     *
     * @param theme the theme to propagate
     */
    @Override
    public void propagateTheme(Theme theme) {
        if (theme == null) {
            return;
        }

        // Update tabs theme (setTheme will propagate to children)
        if (tabs != null) {
            tabs.setTheme(theme);
        }

        // Update all panels theme (setTheme will propagate to children)
        if (panels != null) {
            for (Panel panel : panels) {
                if (panel != null) {
                    panel.setTheme(theme);
                }
            }
        }

        // Update toast theme
        if (currentToast != null) {
            currentToast.setTheme(theme);
        }
    }

    /**
     * Propagates the language to all child components recursively.
     * This method updates the language for tabs, panels, and all nested components.
     *
     * @param language the language to propagate
     */
    @Override
    public void propagateLanguage(Language language) {
        if (language == null) {
            return;
        }

        // Update tabs language (setLanguage will propagate to children)
        if (tabs != null) {
            tabs.setLanguage(language);
        }

        // Update all panels language (setLanguage will propagate to children)
        if (panels != null) {
            for (Panel panel : panels) {
                if (panel != null) {
                    panel.setLanguage(language);
                }
            }
        }
    }

    // ============================================================
    // MEASUREMENT
    // ============================================================

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int availableHeight = getAvailableHeight();

        // Measure Tabs with AT_MOST to allow natural height
        if (tabs != null) {
            int tabsWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            int tabsHeightSpec = MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.AT_MOST);
            tabs.measure(tabsWidthSpec, tabsHeightSpec);
        }

        // Tabs ScreenView: full available height
        if (tabsScrollView != null) {
            int scrollViewWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            int scrollViewHeightSpec = MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.EXACTLY);
            tabsScrollView.measure(scrollViewWidthSpec, scrollViewHeightSpec);
        }

        int tabsWidth = (tabs != null) ? tabs.getMeasuredWidth() : 0;

        // Panel ScreenView: compact → availableHeight, wide → availableHeight - offsetY
        int panelWidth = compactPanel ? ScreenTheme.PANEL_WIDTH : (width - offsetX - tabsWidth - gapX);
        int wrapperHeight = compactPanel ? availableHeight : availableHeight - offsetY;

        // Measure active panel-wrapper
        ScreenView activeWrapper = (panelWrappers != null && activePanelIndex >= 0
                && activePanelIndex < panelWrappers.length) ? panelWrappers[activePanelIndex] : null;
        if (activeWrapper != null) {
            int wSpec = MeasureSpec.makeMeasureSpec(panelWidth, MeasureSpec.EXACTLY);
            int hSpec = MeasureSpec.makeMeasureSpec(wrapperHeight, MeasureSpec.EXACTLY);
            activeWrapper.measure(wSpec, hSpec);
        }

        // Measure animating-out wrapper (during transition it's still a child)
        if (animatingOutWrapper != null && animatingOutWrapper.getParent() == this) {
            int wSpec = MeasureSpec.makeMeasureSpec(panelWidth, MeasureSpec.EXACTLY);
            int hSpec = MeasureSpec.makeMeasureSpec(wrapperHeight, MeasureSpec.EXACTLY);
            animatingOutWrapper.measure(wSpec, hSpec);
        }

        // Measure toast if visible
        if (currentToast != null && currentToast.getVisibility() == View.VISIBLE) {
            int toastWidthSpec = MeasureSpec.makeMeasureSpec(
                    ToastTheme.WIDTH, MeasureSpec.EXACTLY);
            int toastHeightSpec = MeasureSpec.makeMeasureSpec(
                    ToastTheme.HEIGHT, MeasureSpec.EXACTLY);
            currentToast.measure(toastWidthSpec, toastHeightSpec);
        }

        setMeasuredDimension(width, height);
    }

    // ============================================================
    // LAYOUT
    // ============================================================

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int availableHeight = getAvailableHeight();
        int tabsWidth = 0;

        // Layout ScreenView for tabs at offsetX, full available height
        if (tabsScrollView != null) {
            int scrollViewLeft = offsetX;
            int scrollViewRight = scrollViewLeft + tabsScrollView.getMeasuredWidth();
            tabsScrollView.layout(scrollViewLeft, 0, scrollViewRight, availableHeight);

            tabsWidth = tabsScrollView.getMeasuredWidth();
        }

        // Layout active panel-wrapper: compact at Y=0, wide at Y=offsetY
        int panelTop = compactPanel ? 0 : offsetY;
        ScreenView activeWrapper = (panelWrappers != null && activePanelIndex >= 0
                && activePanelIndex < panelWrappers.length) ? panelWrappers[activePanelIndex] : null;
        if (activeWrapper != null && activeWrapper.getParent() == this) {
            int panelLeft = offsetX + tabsWidth + gapX;
            int panelRight = panelLeft + activeWrapper.getMeasuredWidth();
            activeWrapper.layout(panelLeft, panelTop, panelRight, panelTop + activeWrapper.getMeasuredHeight());
        }

        // Layout animating-out wrapper at the same position
        if (animatingOutWrapper != null && animatingOutWrapper.getParent() == this) {
            int panelLeft = offsetX + tabsWidth + gapX;
            int panelRight = panelLeft + animatingOutWrapper.getMeasuredWidth();
            animatingOutWrapper.layout(panelLeft, panelTop, panelRight, panelTop + animatingOutWrapper.getMeasuredHeight());
        }

        // Layout toast at top center if visible
        if (currentToast != null && currentToast.getVisibility() == View.VISIBLE) {
            int toastWidth = currentToast.getMeasuredWidth();
            int toastHeight = currentToast.getMeasuredHeight();
            int toastLeft = (width - toastWidth) / 2;
            int toastTop = ToastTheme.TOP_OFFSET;
            currentToast.layout(toastLeft, toastTop,
                    toastLeft + toastWidth, toastTop + toastHeight);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Draw compact panel image BEFORE children so it appears behind the ScreenView.
        // Gaps between sections (margins) let the image show through.
        Panel activePanel = getActivePanel();
        if (activePanel != null && activePanel.getImage() != null) {
            float imageRight = getWidth() - activePanel.getImageMarginRight();
            float imageLeft = imageRight - activePanel.getImageWidth();
            canvas.drawBitmap(activePanel.getImage(), imageLeft, 0, imagePaint);
        }

        super.dispatchDraw(canvas);
    }

    // ============================================================
    // BUILDER API
    // ============================================================

    /**
     * Creates a new Builder for Screen.
     *
     * @param context the Android context
     * @param theme the theme to apply
     * @return a new Builder instance
     */
    @NonNull
    public static Builder create(@NonNull android.content.Context context,
                                 @NonNull Theme theme) {
        return new Builder(context, theme);
    }

    /**
     * Builder for creating Screen instances with a fluent API.
     */
    public static class Builder {
        private final android.content.Context context;
        private final Theme theme;
        private Integer backgroundColor;
        private Tabs tabs;
        private Panel[] panels;
        private OnScreenLiftListener onScreenLiftListener;
        private boolean compactPanel = false;

        private Builder(android.content.Context context, Theme theme) {
            this.context = context;
            this.theme = theme;
        }

        /**
         * Sets the background color for the screen.
         *
         * @param color the background color
         * @return this Builder instance
         */
        @NonNull
        public Builder backgroundColor(@NonNull int color) {
            this.backgroundColor = color;
            return this;
        }

        /**
         * Sets the background color for the screen from hex string.
         *
         * @param hexColor the hex color string (e.g. "#000000")
         * @return this Builder instance
         */
        @NonNull
        public Builder backgroundColorHex(@NonNull String hexColor) {
            this.backgroundColor = android.graphics.Color.parseColor(hexColor);
            return this;
        }

        /**
         * Sets the Tabs component for the screen.
         *
         * @param tabs the Tabs component
         * @return this Builder instance
         */
        @NonNull
        public Builder tabs(@NonNull Tabs tabs) {
            this.tabs = tabs;
            return this;
        }

        /**
         * Sets the array of panels for the screen.
         *
         * @param panels the array of Panel objects
         * @return this Builder instance
         */
        @NonNull
        public Builder panels(@NonNull Panel[] panels) {
            this.panels = panels;
            return this;
        }

        /**
         * Sets the screen lift listener.
         *
         * @param listener the listener to set
         * @return this Builder instance
         */
        @NonNull
        public Builder onScreenLift(@Nullable OnScreenLiftListener listener) {
            this.onScreenLiftListener = listener;
            return this;
        }

        @NonNull
        public Builder compactPanel(boolean compactPanel) {
            this.compactPanel = compactPanel;
            return this;
        }

        /**
         * Builds and returns the Screen instance.
         *
         * @return a new Screen instance
         */
        @NonNull
        public Screen build() {
            Screen screen = new Screen(context);
            screen.setTheme(theme);
            if (backgroundColor != null) {
                screen.setBackgroundColor(backgroundColor);
            }
            // Set panels BEFORE tabs so setActivePanel() can find them
            if (panels != null) {
                screen.setPanels(panels);
            }
            if (tabs != null) {
                screen.setTabs(tabs);
            }
            if (onScreenLiftListener != null) {
                screen.setOnScreenLiftListener(onScreenLiftListener);
            }
            if (compactPanel) {
                screen.setCompactPanel(true);
            }
            return screen;
        }
    }
}


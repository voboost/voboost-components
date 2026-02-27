package ru.voboost.components.screen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

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
    private static final int DEFAULT_OFFSET_X = 145;
    private static final int DEFAULT_OFFSET_Y = 50;
    private static final int DEFAULT_GAP_X = 0;
    public static final int SCREEN_LOWERED = 1;
    public static final int SCREEN_RAISED = 2;

    // Theme & Language
    private Theme currentTheme;
    private Language currentLanguage;

    // Offset fields
    private int offsetX = DEFAULT_OFFSET_X;
    private int offsetY = DEFAULT_OFFSET_Y;
    private int gapX = DEFAULT_GAP_X;

    // Screen lift state
    private int screenLiftState = SCREEN_RAISED;
    private OnScreenLiftListener onScreenLiftListener;

    // Component references
    private Tabs tabs;
    private Panel[] panels;
    private int activePanelIndex = -1;
    private android.widget.ScrollView tabsScrollView;

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

    private void init(Context context) {
        // Initialization logic - hardware acceleration not needed as this is a
        // container ViewGroup
        // without custom drawing operations
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

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

    /**
     * Sets the Tabs component for the screen.
     *
     * @param tabs the Tabs component to add
     */
    public void setTabs(Tabs tabs) {
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

        // Add new tabs to the ViewGroup with ScrollView
        if (tabs != null) {
            // Create ScrollView for tabs
            tabsScrollView = new ScrollView(getContext());
            tabsScrollView.setVerticalScrollBarEnabled(false);
            tabsScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

            // Add tabs to ScrollView
            tabsScrollView.addView(tabs);

            // Add ScrollView to Screen
            addView(tabsScrollView);

            // Set listener for tab changes
            tabs.setOnTabChangeListener(
                    newIndex -> {
                        setActivePanel(newIndex);
                    });
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
    public android.widget.ScrollView getTabsScrollView() {
        return tabsScrollView;
    }

    /**
     * Sets the array of panels for the screen.
     *
     * @param panels the array of Panel objects
     */
    public void setPanels(Panel[] panels) {
        this.panels = panels;
    }

    /**
     * Sets the active panel by index.
     *
     * @param index the index of the panel to activate
     */
    public void setActivePanel(int index) {
        if (panels == null || index < 0 || index >= panels.length) {
            activePanelIndex = -1;
            return;
        }

        if (activePanelIndex != index) {
            // Remove old active panel from this ViewGroup
            if (activePanelIndex >= 0 && activePanelIndex < panels.length) {
                Panel oldPanel = panels[activePanelIndex];
                if (oldPanel.getParent() == this) {
                    removeView(oldPanel);
                }
            }

            activePanelIndex = index;

            // Add new active panel to this ViewGroup
            Panel newPanel = panels[activePanelIndex];
            if (newPanel.getParent() != null && newPanel.getParent() != this) {
                ((ViewGroup) newPanel.getParent()).removeView(newPanel);
            }
            if (newPanel.getParent() != this) {
                addView(newPanel);
            }

            requestLayout();
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

        // Update tabs theme
        if (tabs != null) {
            tabs.setTheme(theme);
            tabs.propagateTheme(theme);
        }

        // Update all panels theme
        if (panels != null) {
            for (Panel panel : panels) {
                if (panel != null) {
                    panel.setTheme(theme);
                    panel.propagateTheme(theme);
                }
            }
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

        // Update tabs language
        if (tabs != null) {
            tabs.setLanguage(language);
            tabs.propagateLanguage(language);
        }

        // Update all panels language
        if (panels != null) {
            for (Panel panel : panels) {
                if (panel != null) {
                    panel.setLanguage(language);
                    panel.propagateLanguage(language);
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

        // Measure Tabs with AT_MOST to allow natural height
        if (tabs != null) {
            int tabsWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            int tabsHeightSpec = MeasureSpec.makeMeasureSpec(height - offsetY, MeasureSpec.AT_MOST);
            tabs.measure(tabsWidthSpec, tabsHeightSpec);
        }

        // Measure ScrollView for tabs
        if (tabsScrollView != null) {
            int scrollViewWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            int scrollViewHeightSpec = MeasureSpec.makeMeasureSpec(height - offsetY, MeasureSpec.EXACTLY);
            tabsScrollView.measure(scrollViewWidthSpec, scrollViewHeightSpec);
        }

        int tabsWidth = (tabs != null) ? tabs.getMeasuredWidth() : 0;

        // Get active panel
        Panel activePanel = getActivePanel();

        // Calculate Panel dimensions considering offsets
        int panelWidth = width - offsetX - tabsWidth - gapX;
        int panelHeight = height - offsetY;

        // Measure active Panel with remaining width and height minus offsetY
        if (activePanel != null) {
            int panelWidthSpec = MeasureSpec.makeMeasureSpec(panelWidth, MeasureSpec.EXACTLY);
            int panelHeightSpec = MeasureSpec.makeMeasureSpec(panelHeight, MeasureSpec.EXACTLY);
            activePanel.measure(panelWidthSpec, panelHeightSpec);
        }

        setMeasuredDimension(width, height);
    }

    // ============================================================
    // LAYOUT
    // ============================================================

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        int tabsWidth = 0;

        // Layout ScrollView for tabs at offsetX with full height
        if (tabsScrollView != null) {
            int scrollViewLeft = offsetX;
            int scrollViewTop = offsetY;
            int scrollViewRight = scrollViewLeft + tabsScrollView.getMeasuredWidth();
            int scrollViewBottom = height;
            tabsScrollView.layout(scrollViewLeft, scrollViewTop, scrollViewRight, scrollViewBottom);

            tabsWidth = tabsScrollView.getMeasuredWidth();
        }

        // Layout active Panel after Tabs with offsetY from top
        Panel activePanel = getActivePanel();
        if (activePanel != null && activePanel.getParent() == this) {
            int panelLeft = offsetX + tabsWidth + gapX;
            int panelTop = offsetY;
            int panelRight = panelLeft + activePanel.getMeasuredWidth();
            int panelBottom = panelTop + activePanel.getMeasuredHeight();
            activePanel.layout(panelLeft, panelTop, panelRight, panelBottom);
        }
    }
}

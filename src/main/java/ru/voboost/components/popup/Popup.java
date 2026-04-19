package ru.voboost.components.popup;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import ru.voboost.components.theme.Theme;

/**
 * Popup — universal overlay container for Dialog, SelectPopup, SectionPopup.
 *
 * <p>
 * Provides gradient overlay (matching original Voyah dialog_window.xml),
 * rounded content panel, and scale+fade enter/exit animations.
 * Does not define its own content — consumers set the content view
 * via {@link #setPopupContentView(View)}.
 *
 * <p>
 * Used through aggregation:
 * <ul>
 * <li>Dialog — shows title, message, buttons</li>
 * <li>SelectPopup — shows wheel picker with Save/Cancel</li>
 * <li>SectionPopup — shows info text with close button</li>
 * </ul>
 */
public class Popup extends Dialog {
    private Theme currentTheme = null;
    private PopupColors colors;
    private FrameLayout contentContainer;
    private FrameLayout rootLayout;
    private boolean dismissOnTouchOutside = true;
    private DefaultLifecycleObserver lifecycleObserver;
    private int customAnimationDuration = -1; // -1 means use default

    public Popup(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Root layout — full-screen overlay
        rootLayout = new FrameLayout(getContext());
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // Touch outside → dismiss
        rootLayout.setOnClickListener(v -> {
            if (dismissOnTouchOutside) {
                dismissWithAnimation();
            }
        });

        // Content container — centered panel
        contentContainer = new FrameLayout(getContext());
        FrameLayout.LayoutParams panelParams = new FrameLayout.LayoutParams(
                (int) PopupDimensions.PANEL_WIDTH_PX,
                (int) PopupDimensions.PANEL_HEIGHT_PX);
        panelParams.gravity = Gravity.CENTER;
        contentContainer.setLayoutParams(panelParams);

        // Prevent click-through to overlay
        contentContainer.setClickable(true);

        int padding = (int) PopupDimensions.PADDING_PX;
        contentContainer.setPadding(padding, padding, padding, padding);

        rootLayout.addView(contentContainer);

        super.setContentView(rootLayout);

        // Make window full-screen and transparent
        Window window = getWindow();
        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Disable standard Android dim — overlay is drawn as gradient
            // (matches original QGVoyahDialog.setDimAmount(0.0f))
            window.setDimAmount(0.0f);
        }
    }

    /**
     * Sets the visual theme.
     */
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }
        this.currentTheme = theme;
        this.colors = PopupTheme.getColors(theme);
        applyThemeColors();
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    private void applyThemeColors() {
        if (colors == null) return;

        View rootView = getRootView();
        if (rootView != null) {
            rootView.setBackground(PopupOverlayGradient.createOverlayDrawable());
        }

        if (colors.panelBackgroundDrawable != null) {
            contentContainer.setBackground(colors.panelBackgroundDrawable.getConstantState().newDrawable().mutate());
        } else {
            GradientDrawable panelBg = new GradientDrawable();
            panelBg.setColor(colors.panelBackground);
            panelBg.setCornerRadius(PopupDimensions.CORNER_RADIUS_PX);
            contentContainer.setBackground(panelBg);
        }
    }

    private View getRootView() {
        return rootLayout;
    }

    /**
     * Sets the content view inside the popup panel.
     * This is what Dialog, Select etc. use to inject their content.
     *
     * @param view the content view
     */
    public void setPopupContentView(View view) {
        contentContainer.removeAllViews();
        if (view != null) {
            contentContainer.addView(view);
        }
    }

    /**
     * Returns the content container for subclasses.
     */
    protected FrameLayout getContentContainer() {
        return contentContainer;
    }

    /**
     * Visible-for-testing accessor that returns the full overlay view
     * (scrim background + centered panel). Used by Robolectric visual tests
     * to composite the popup on top of the activity's screen bitmap.
     * Resets enter-animation state (alpha/scale) so the panel is fully
     * visible even though ObjectAnimator does not run synchronously in
     * Robolectric.
     */
    public View getOverlayViewForTest() {
        contentContainer.setAlpha(1f);
        contentContainer.setScaleX(1f);
        contentContainer.setScaleY(1f);
        return rootLayout;
    }

    /**
     * Sets whether popup dismisses on touch outside the panel.
     */
    public void setDismissOnTouchOutside(boolean dismiss) {
        this.dismissOnTouchOutside = dismiss;
        setCanceledOnTouchOutside(dismiss);
    }

    /**
     * Sets custom animation duration in milliseconds.
     * Use -1 to restore default duration (300ms).
     *
     * @param durationMs Animation duration in milliseconds
     */
    public void setAnimationDuration(int durationMs) {
        this.customAnimationDuration = durationMs;
    }

    /**
     * Sets a dismiss listener using a simple Runnable.
     * Convenience wrapper over {@link android.app.Dialog#setOnDismissListener}.
     *
     * @param listener callback invoked when the popup is dismissed, or null to clear
     */
    public void setOnDismissListener(Runnable listener) {
        super.setOnDismissListener(dialog -> {
            if (listener != null) {
                listener.run();
            }
        });
    }

    @Override
    public void show() {
        super.show();

        // Register lifecycle observer to dismiss popup when Activity is destroyed
        Activity activity = getOwnerActivity();
        if (activity == null && getContext() instanceof Activity) {
            activity = (Activity) getContext();
            setOwnerActivity(activity);
        }

        // Store reference to LifecycleObserver to remove it on dismiss
        lifecycleObserver = new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(LifecycleOwner owner) {
                if (isShowing()) {
                    dismiss();
                }
                owner.getLifecycle().removeObserver(this);
                lifecycleObserver = null;
            }
        };

        if (activity instanceof LifecycleOwner) {
            ((LifecycleOwner) activity).getLifecycle().addObserver(lifecycleObserver);
        }

        animateIn();
    }

    /**
     * Dismiss with exit animation.
     */
    public void dismissWithAnimation() {
        animateOut(() -> {
            if (isShowing()) {
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        // Remove lifecycle observer to prevent memory leak
        if (lifecycleObserver != null) {
            Activity activity = getOwnerActivity();
            if (activity == null && getContext() instanceof Activity) {
                activity = (Activity) getContext();
            }
            if (activity instanceof LifecycleOwner) {
                ((LifecycleOwner) activity).getLifecycle().removeObserver(lifecycleObserver);
            }
            lifecycleObserver = null;
        }
        super.dismiss();
    }

    private void animateIn() {
        contentContainer.setScaleX(PopupDimensions.SCALE_FROM);
        contentContainer.setScaleY(PopupDimensions.SCALE_FROM);
        contentContainer.setAlpha(0f);

        int duration = customAnimationDuration > 0 ? customAnimationDuration : PopupDimensions.ANIMATION_DURATION;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(contentContainer, "scaleX",
                        PopupDimensions.SCALE_FROM, PopupDimensions.SCALE_TO),
                ObjectAnimator.ofFloat(contentContainer, "scaleY",
                        PopupDimensions.SCALE_FROM, PopupDimensions.SCALE_TO),
                ObjectAnimator.ofFloat(contentContainer, "alpha", 0f, 1f));
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new OvershootInterpolator(0.8f));
        animatorSet.start();
    }

    private void animateOut(Runnable onEnd) {
        int duration = customAnimationDuration > 0
            ? customAnimationDuration / 2
            : PopupDimensions.ANIMATION_DURATION / 2;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(contentContainer, "scaleX",
                        PopupDimensions.SCALE_TO, PopupDimensions.SCALE_FROM),
                ObjectAnimator.ofFloat(contentContainer, "scaleY",
                        PopupDimensions.SCALE_TO, PopupDimensions.SCALE_FROM),
                ObjectAnimator.ofFloat(contentContainer, "alpha", 1f, 0f));
        animatorSet.setDuration(duration);
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (onEnd != null) {
                    onEnd.run();
                }
            }
        });
        animatorSet.start();
    }
}

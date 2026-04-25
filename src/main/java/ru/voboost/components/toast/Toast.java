package ru.voboost.components.toast;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import ru.voboost.components.font.Font;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Toast component — notification message that appears at the top of the screen.
 *
 * <p>
 * Features:
 * <ul>
 *   <li>Slide-in animation from top with fade effect</li>
 *   <li>Auto-dismiss after configurable duration</li>
 *   <li>Optional close button</li>
 *   <li>Theme support (FREE_DARK, FREE_LIGHT, DREAMER_DARK, DREAMER_LIGHT)</li>
 * </ul>
 *
 * <p>
 * Usage:
 * <pre>
 * Toast toast = new Toast(context);
 * toast.setTheme(Theme.FREE_DARK);
 * toast.setContent("Settings saved");
 * toast.setDuration(ToastTheme.DURATION_SHORT);
 * toast.show();
 * </pre>
 */
public class Toast extends View implements IThemable {

    // Theme
    private Theme currentTheme;

    // Content
    private String text = "";

    // State
    private volatile boolean showing = false;
    private boolean showCloseButton = false;
    private long duration = ToastAnimation.DURATION_SHORT;
    private final AtomicBoolean dismissInProgress = new AtomicBoolean(false);

    // Animation
    private AnimatorSet currentAnimator;
    private Handler autoDismissHandler;
    private Runnable autoDismissRunnable;

    // Drawing
    private Paint backgroundPaint;
    private TextPaint textPaint;
    private Paint closeIconPaint;
    private RectF backgroundRect;

    // Callback
    private OnDismissListener onDismissListener;
    private DefaultLifecycleObserver lifecycleObserver;

    /**
     * Callback interface for toast dismissal events.
     */
    public interface OnDismissListener {
        void onDismiss();
    }

    public Toast(Context context) {
        super(context);
        init(context);
    }

    public Toast(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Toast(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Toast(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        // Hardware acceleration for smooth animation
        setLayerType(LAYER_TYPE_HARDWARE, null);

        // Paints
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(ToastDimensions.TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.CENTER);

        closeIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        closeIconPaint.setStyle(Paint.Style.STROKE);
        closeIconPaint.setStrokeWidth(ToastDimensions.CLOSE_ICON_STROKE_WIDTH);
        closeIconPaint.setStrokeCap(Paint.Cap.ROUND);

        backgroundRect = new RectF();

        // Handler for auto-dismiss
        autoDismissHandler = new Handler(Looper.getMainLooper());

        // Initially invisible
        setVisibility(View.GONE);
        setAlpha(0f);
    }

    // --- Theme ---

    @Override
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }
        this.currentTheme = theme;
        updateColors();
        invalidate();
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    @Override
    public void propagateTheme(Theme theme) {
        // Leaf component — no children
    }

    // --- Content ---

    /**
     * Sets the text content of the toast.
     *
     * @param text the message text (null will be converted to empty string)
     */
    public void setContent(String text) {
        this.text = text != null ? text : "";
        // Update font: ASCII or Unicode
        textPaint.setTypeface(Font.getBold(getContext(), this.text));
        invalidate();
    }

    public String getContent() {
        return text;
    }

    // --- Settings ---

    /**
     * Sets the auto-dismiss duration in milliseconds.
     * Use 0 to disable auto-dismiss.
     *
     * @param millis duration in milliseconds (must be >= 0)
     * @throws IllegalArgumentException if duration is negative
     */
    public void setDuration(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration cannot be negative: " + millis);
        }
        this.duration = millis;
    }

    public long getDuration() {
        return duration;
    }

    /**
     * Sets whether to show the close button.
     *
     * @param show true to show close button, false to hide
     */
    public void setShowCloseButton(boolean show) {
        this.showCloseButton = show;
        invalidate();
    }

    public boolean isShowCloseButton() {
        return showCloseButton;
    }

    // --- Callback ---

    /**
     * Sets the listener to be called when the toast is dismissed.
     *
     * @param listener the dismiss listener
     */
    public void setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
    }

    // --- State ---

    /**
     * Returns whether the toast is currently showing.
     *
     * @return true if showing, false otherwise
     */
    public boolean isShowing() {
        return showing;
    }

    // --- Show/Dismiss ---

    /**
     * Shows the toast with slide-in animation from top.
     * If already showing, this call is ignored.
     */
    public void show() {
        if (showing) {
            return; // already showing
        }
        showing = true;

        // Register lifecycle observer to dismiss toast when Activity is destroyed
        // Skip in Robolectric tests
        if (!isRobolectric()) {
            Activity activity = findActivity();
            if (activity != null && activity instanceof LifecycleOwner) {
                if (lifecycleObserver != null) {
                    ((LifecycleOwner) activity).getLifecycle().removeObserver(lifecycleObserver);
                }
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
                ((LifecycleOwner) activity).getLifecycle().addObserver(lifecycleObserver);
            }
        }

        // Cancel current animation if any
        cancelCurrentAnimation();

        // Make visible
        setVisibility(View.VISIBLE);

        // Start entrance animation:
        // translationY: -height → 0 (slide down from top)
        // alpha: 0 → 1 (fade in)
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(this, "translationY",
                -ToastDimensions.HEIGHT, 0);
        slideIn.setDuration(ToastAnimation.SLIDE_DURATION);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        fadeIn.setDuration(ToastAnimation.FADE_DURATION);

        currentAnimator = new AnimatorSet();
        currentAnimator.playTogether(slideIn, fadeIn);
        currentAnimator.setInterpolator(new DecelerateInterpolator(ToastAnimation.DECELERATE_FACTOR));
        currentAnimator.start();

        // Schedule auto-dismiss
        scheduleAutoDismiss();
    }

    /**
     * Dismisses the toast with slide-out animation to top.
     * If not showing, this call is ignored.
     */
    public void dismiss() {
        if (!showing) {
            return; // already hidden
        }

        // Prevent multiple dismiss calls
        if (!dismissInProgress.compareAndSet(false, true)) {
            return; // dismiss already in progress
        }

        // Remove lifecycle observer
        if (lifecycleObserver != null) {
            Activity activity = findActivity();
            if (activity instanceof LifecycleOwner) {
                ((LifecycleOwner) activity).getLifecycle().removeObserver(lifecycleObserver);
            }
            lifecycleObserver = null;
        }

        // Cancel auto-dismiss
        autoDismissHandler.removeCallbacksAndMessages(null);

        // Cancel current animation if any
        cancelCurrentAnimation();

        // In Robolectric, animations don't work properly, so dismiss immediately
        if (isRobolectric()) {
            showing = false;
            dismissInProgress.set(false);
            setVisibility(View.GONE);
            if (onDismissListener != null) {
                onDismissListener.onDismiss();
            }
            return;
        }

        // Start exit animation:
        // translationY: 0 → -height (slide up to top)
        // alpha: 1 → 0 (fade out)
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(this, "translationY",
                0, -ToastDimensions.HEIGHT);
        slideOut.setDuration(ToastAnimation.SLIDE_DURATION);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
        fadeOut.setDuration(ToastAnimation.FADE_DURATION);

        currentAnimator = new AnimatorSet();
        currentAnimator.playTogether(slideOut, fadeOut);
        currentAnimator.setInterpolator(new DecelerateInterpolator(ToastAnimation.DECELERATE_FACTOR));
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                showing = false;
                dismissInProgress.set(false);
                setVisibility(View.GONE);
                if (onDismissListener != null) {
                    onDismissListener.onDismiss();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                dismissInProgress.set(false);
            }
        });
        currentAnimator.start();
    }

    private void scheduleAutoDismiss() {
        autoDismissHandler.removeCallbacksAndMessages(null);
        if (duration > 0) {
            // Cancel previous runnable if exists
            if (autoDismissRunnable != null) {
                autoDismissHandler.removeCallbacks(autoDismissRunnable);
            }
            // Create new runnable
            autoDismissRunnable = () -> dismiss();
            autoDismissHandler.postDelayed(autoDismissRunnable, duration);
        }
    }

    private void cancelCurrentAnimation() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
            currentAnimator = null;
        }
    }

    private boolean isRobolectric() {
        return "robolectric".equals(android.os.Build.FINGERPRINT);
    }

    private Activity findActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    // --- Drawing ---

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Fixed dimensions
        setMeasuredDimension(ToastDimensions.WIDTH, ToastDimensions.HEIGHT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 1. Background — rounded rectangle
        backgroundRect.set(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(backgroundRect,
                ToastDimensions.CORNER_RADIUS, ToastDimensions.CORNER_RADIUS,
                backgroundPaint);

        // 2. Close button (if enabled) — cross icon on the left
        if (showCloseButton) {
            drawCloseButton(canvas);
        }

        // 3. Text — centered
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // Vertical centering of text
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textY = centerY - (fm.ascent + fm.descent) / 2f;

        // If text is long — truncate with ellipsis
        float maxTextWidth = getWidth() - ToastDimensions.PADDING_HORIZONTAL * 2;
        if (showCloseButton) {
            // Account for close button space
            maxTextWidth -= (ToastDimensions.CLOSE_BUTTON_MARGIN + ToastDimensions.CLOSE_BUTTON_SIZE);
        }

        CharSequence ellipsized = TextUtils.ellipsize(
                text, textPaint, maxTextWidth, TextUtils.TruncateAt.END);

        canvas.drawText(ellipsized.toString(), centerX, textY, textPaint);
    }

    private void drawCloseButton(Canvas canvas) {
        // Button position — left side, vertically centered
        float cx = ToastDimensions.CLOSE_BUTTON_MARGIN + ToastDimensions.CLOSE_BUTTON_SIZE / 2f;
        float cy = getHeight() / 2f;
        float halfSize = ToastDimensions.CLOSE_ICON_SIZE / 2f;

        // Draw cross (×) with two lines
        canvas.drawLine(cx - halfSize, cy - halfSize,
                cx + halfSize, cy + halfSize, closeIconPaint);
        canvas.drawLine(cx + halfSize, cy - halfSize,
                cx - halfSize, cy + halfSize, closeIconPaint);
    }

    // --- Touch handling (for close button) ---

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!showCloseButton) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Check if touch is within close button bounds
                return isInCloseButtonBounds(event.getX(), event.getY());
            case MotionEvent.ACTION_UP:
                if (isInCloseButtonBounds(event.getX(), event.getY())) {
                    dismiss();
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                // Touch was canceled - reset any state if needed
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isInCloseButtonBounds(float x, float y) {
        float buttonCenterX = ToastDimensions.CLOSE_BUTTON_MARGIN + ToastDimensions.CLOSE_BUTTON_SIZE / 2f;
        float buttonCenterY = getHeight() / 2f;
        float halfTouchArea = ToastDimensions.CLOSE_BUTTON_SIZE / 2f;

        return Math.abs(x - buttonCenterX) <= halfTouchArea
                && Math.abs(y - buttonCenterY) <= halfTouchArea;
    }

    // --- Color updates ---

    private void updateColors() {
        if (currentTheme == null) {
            // Return early if theme not set - use default colors
            backgroundPaint.setColor(ToastColorSchemes.get(Theme.FREE_DARK).background);
            textPaint.setColor(ToastColorSchemes.get(Theme.FREE_DARK).text);
            closeIconPaint.setColor(ToastColorSchemes.get(Theme.FREE_DARK).closeIcon);
            return;
        }
        ToastColors colors = ToastTheme.getColors(currentTheme);
        backgroundPaint.setColor(colors.background);
        textPaint.setColor(colors.text);
        closeIconPaint.setColor(colors.closeIcon);
    }

    // --- Resource cleanup ---

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // Cancel any running animation
        cancelCurrentAnimation();

        // Remove all handler callbacks
        autoDismissHandler.removeCallbacksAndMessages(null);
        autoDismissRunnable = null;

        // Remove lifecycle observer to prevent memory leak
        if (lifecycleObserver != null) {
            Activity activity = findActivity();
            if (activity instanceof LifecycleOwner) {
                ((LifecycleOwner) activity).getLifecycle().removeObserver(lifecycleObserver);
            }
            lifecycleObserver = null;
        }

        // Clear dismiss listener to prevent memory leak
        onDismissListener = null;
    }
}

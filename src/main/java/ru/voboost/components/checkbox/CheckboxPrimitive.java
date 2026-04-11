package ru.voboost.components.checkbox;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * CheckboxPrimitive — internal canvas-based toggle switch.
 * Package-private. Use Checkbox (LinearLayout container) for public API.
 */
class CheckboxPrimitive extends View implements IThemable {
    // State
    private Theme currentTheme = null;
    private boolean checked = false;
    private boolean isEnabled = true;
    private OnCheckedChangeListener onCheckedChangeListener;

    // Theme colors
    private CheckboxColors colors;

    // Paint objects cached
    private Paint trackPaint;
    private Paint maskPaint;
    private Paint knobPaint;
    private RectF trackRect;
    private RectF maskRect;

    // Animation state
    private ValueAnimator toggleAnimator;
    private float animationProgress = 0f; // 0 = OFF, 1 = ON
    private float testMaskAlpha = -1f; // -1 = use animationProgress

    /**
     * Callback for checked state changes.
     */
    public interface OnCheckedChangeListener {
        void onCheckedChange(boolean isChecked);
    }

    public CheckboxPrimitive(Context context) {
        super(context);
        init();
    }

    public CheckboxPrimitive(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckboxPrimitive(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CheckboxPrimitive(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        knobPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackRect = new RectF();
        maskRect = new RectF();
    }

    private void updateColors() {
        if (currentTheme != null) {
            colors = CheckboxTheme.getColors(currentTheme);
        }
    }

    // --- Public API ---

    @Override
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }
        if (!theme.equals(this.currentTheme)) {
            this.currentTheme = theme;
            updateColors();
            invalidate();
        }
    }

    @Override
    public void propagateTheme(Theme theme) {
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            cancelAnimation();
            animationProgress = checked ? 1f : 0f;
            invalidate();
        }
    }

    public void setCheckedAnimated(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            animateToggle(checked);
        }
    }

    public boolean isChecked() {
        return checked;
    }

    void setVisualStateForTest(float knobPosition, float maskAlpha) {
        cancelAnimation();
        this.animationProgress = knobPosition;
        this.testMaskAlpha = maskAlpha;
        invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.isEnabled != enabled) {
            this.isEnabled = enabled;
            super.setEnabled(enabled);
            invalidate();
        }
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    // --- Animation ---

    private void animateToggle(boolean toChecked) {
        cancelAnimation();

        float target = toChecked ? 1f : 0f;
        toggleAnimator = ValueAnimator.ofFloat(animationProgress, target);
        toggleAnimator.setDuration(CheckboxDimensions.ANIMATION_DURATION);
        toggleAnimator.setInterpolator(
                new OvershootInterpolator(CheckboxDimensions.OVERSHOOT_TENSION));
        toggleAnimator.addUpdateListener(animation -> {
            animationProgress = (Float) animation.getAnimatedValue();
            invalidate();
        });
        toggleAnimator.start();
    }

    private void cancelAnimation() {
        if (toggleAnimator != null) {
            toggleAnimator.cancel();
        }
    }

    // --- Measurement ---

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int) Math.ceil(CheckboxDimensions.TRACK_WIDTH_PX);
        int height = (int) Math.ceil(CheckboxDimensions.TRACK_HEIGHT_PX);
        setMeasuredDimension(width, height);
    }

    // --- Drawing ---

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentTheme == null || colors == null)
            return;

        float width = getWidth();
        float height = getHeight();
        float disabledAlpha = (!isEnabled) ? colors.disabledAlpha : 1f;

        // 1. Draw track (Bitmap-based background from original)
        boolean isDark = currentTheme != null &&
                (currentTheme == Theme.FREE_DARK || currentTheme == Theme.DREAMER_DARK);
        android.graphics.Bitmap bgBitmap = CheckboxPrimitiveBackground.getBackground(isDark);

        if (bgBitmap != null) {
            float bgLeft = (width - CheckboxPrimitiveBackground.WIDTH) / 2f;
            float bgTop = (height - CheckboxPrimitiveBackground.HEIGHT) / 2f;
            trackPaint.setAlpha((int) (255 * disabledAlpha));
            canvas.drawBitmap(bgBitmap, bgLeft, bgTop, trackPaint);
        }

        // 2. Draw mask (animated fill for ON state)
        // Mask height is 40px (1px inset top/bottom from 42px track).
        // Mask width is 82px (1px inset from right) to match original shadow effect.
        if (animationProgress > 0f) {
            float maskInset = (height - CheckboxDimensions.MASK_HEIGHT_PX) / 2f;
            float maskCornerRadius = CheckboxDimensions.MASK_HEIGHT_PX / 2f;
            float maskWidth = 82f;
            maskRect.set(0, maskInset, maskWidth, height - maskInset);

            int solidMaskColor = colors.trackOnEnd;
            float rawAlpha = (testMaskAlpha >= 0) ? testMaskAlpha : animationProgress;
            float effectiveMaskAlpha = Math.max(0f, Math.min(rawAlpha, 1f));
            int maskAlpha = (int) (255 * effectiveMaskAlpha * disabledAlpha);

            maskPaint.setShader(null);
            maskPaint.setColor(solidMaskColor);
            maskPaint.setAlpha(maskAlpha);

            canvas.drawRoundRect(maskRect, maskCornerRadius, maskCornerRadius, maskPaint);
        }

        // 3. Draw knob (Bitmap-based with shadows/glows from extract)
        float padding = CheckboxDimensions.KNOB_PADDING_PX;
        float knobDiameter = CheckboxDimensions.KNOB_SIZE_PX;
        float knobMinX = padding;
        float knobMaxX = width - padding - knobDiameter - 1f; // Shift 1px left in checked state

        float knobX = knobMinX + (knobMaxX - knobMinX) * animationProgress;
        float knobCenterX = knobX + knobDiameter / 2f;
        float knobCenterY = height / 2f;

        android.graphics.Bitmap knobBitmap = CheckboxPrimitiveKnob.getBitmap(isDark);

        if (knobBitmap != null) {
            float bW = knobBitmap.getWidth();
            float bH = knobBitmap.getHeight();
            float knobLeft = knobCenterX - bW / 2f;
            float knobTop = knobCenterY - bH / 2f;

            // In transition state, apply testMaskAlpha to dim the knob
            float knobAlphaMultiplier = disabledAlpha;
            if (testMaskAlpha >= 0) {
                knobAlphaMultiplier *= testMaskAlpha;
            }

            knobPaint.setAlpha((int) (255 * knobAlphaMultiplier));
            canvas.drawBitmap(knobBitmap, knobLeft, knobTop, knobPaint);
        }
    }

    // --- Touch ---

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();
            if (x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight()) {
                toggle();
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    private void toggle() {
        checked = !checked;
        animateToggle(checked);
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChange(checked);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimation();
        toggleAnimator = null;
    }
}

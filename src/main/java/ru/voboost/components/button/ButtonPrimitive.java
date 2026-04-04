package ru.voboost.components.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import ru.voboost.components.font.Font;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * ButtonPrimitive — internal canvas-based pill button.
 * Package-private. Use Button (LinearLayout container) for public API.
 */
class ButtonPrimitive extends View implements IThemable {
    // State
    private Theme currentTheme = null;
    private ButtonStyle currentStyle = ButtonStyle.PRIMARY;
    private String text = "";
    private boolean isEnabled = true;
    private boolean isPressed = false;
    private boolean boldText = false;
    private android.view.View.OnClickListener onClickListener;
    private Integer paddingTop = null;
    private Integer paddingBottom = null;
    private Integer paddingLeft = null;
    private Integer paddingRight = null;

    // Theme colors
    private ButtonColors colors;

    // Paint objects
    private Paint backgroundPaint;
    private Paint textPaint;

    // Reusable drawing objects (performance optimization)
    private final RectF drawRect = new RectF();

    public ButtonPrimitive(Context context) {
        super(context);
        init();
    }

    public ButtonPrimitive(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonPrimitive(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ButtonPrimitive(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(ButtonDimensions.TEXT_SIZE_PX);

        setLayerType(LAYER_TYPE_HARDWARE, null);
        loadFont();
    }

    private void loadFont() {
        if (boldText && text != null && !text.isEmpty()) {
            textPaint.setTypeface(Font.getSemiBold(getContext()));
        } else {
            textPaint.setTypeface(Font.getRegular(getContext()));
        }
    }

    private void updateColors() {
        if (currentTheme != null) {
            colors = ButtonTheme.getColors(currentTheme, currentStyle);
        }
    }

    // --- Public API ---

    /**
     * Sets the button text.
     *
     * @param text display text
     */
    public void setText(String text) {
        this.text = text != null ? text : "";
        loadFont(); // Reload font to ensure correct bold variant is used
        requestLayout();
        invalidate();
    }

    /**
     * Returns the current button text.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the button visual style.
     *
     * @param style PRIMARY or SECONDARY
     */
    public void setStyle(ButtonStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("ButtonStyle cannot be null");
        }
        if (!style.equals(this.currentStyle)) {
            this.currentStyle = style;
            updateColors();
            invalidate();
        }
    }

    /**
     * Returns the current style.
     */
    public ButtonStyle getStyle() {
        return currentStyle;
    }

    /**
     * Sets the visual theme.
     *
     * @param theme theme enum value
     */
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
        // Leaf component, no children
    }

    /**
     * Returns the current theme.
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Sets the padding for all sides.
     *
     * @param left   left padding in pixels
     * @param top    top padding in pixels
     * @param right  right padding in pixels
     * @param bottom bottom padding in pixels
     */
    public void padding(int left, int top, int right, int bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        requestLayout();
        invalidate();
    }

    /**
     * Sets the enabled state.
     *
     * @param enabled true to enable, false to disable
     */
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

    /**
     * Sets the click listener.
     *
     * @param listener callback for click events
     */
    public void setOnClickListener(android.view.View.OnClickListener listener) {
        this.onClickListener = listener;
    }

    /**
     * Sets whether the button text should use bold font.
     *
     * @param bold true for bold text, false for regular
     */
    public void setBoldText(boolean bold) {
        if (this.boldText != bold) {
            this.boldText = bold;
            loadFont();
            requestLayout();
            invalidate();
        }
    }

    // --- Measurement ---

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float textWidth = textPaint != null ? textPaint.measureText(text) : 0;

        float paddingL = paddingLeft != null ? paddingLeft : ButtonDimensions.PADDING_HORIZONTAL_PX;
        float paddingR = paddingRight != null ? paddingRight : ButtonDimensions.PADDING_HORIZONTAL_PX;

        float desiredWidth = textWidth + paddingL + paddingR;
        float desiredHeight = ButtonDimensions.HEIGHT_PX;

        int width = resolveSize((int) Math.ceil(desiredWidth), widthMeasureSpec);
        int height = resolveSize((int) Math.ceil(desiredHeight), heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    // --- Drawing ---

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentTheme == null || colors == null) {
            return;
        }

        float width = getWidth();
        float height = getHeight();

        // Background
        int bgColor;
        if (!isEnabled) {
            bgColor = colors.backgroundDisabled;
        } else if (isPressed) {
            bgColor = colors.backgroundPressed;
        } else {
            bgColor = colors.backgroundNormal;
        }

        backgroundPaint.setColor(bgColor);
        drawRect.set(0, 0, width, height);
        canvas.drawRoundRect(drawRect,
                ButtonDimensions.CORNER_RADIUS_PX,
                ButtonDimensions.CORNER_RADIUS_PX,
                backgroundPaint);

        // Text
        int textColor = isEnabled ? colors.text : colors.textDisabled;
        textPaint.setColor(textColor);
        textPaint.setTextSize(boldText ? ButtonDimensions.TEXT_SIZE_PX - 1f : ButtonDimensions.TEXT_SIZE_PX);

        if (boldText) {
            textPaint.setLetterSpacing(0.5f / textPaint.getTextSize());
        } else {
            textPaint.setLetterSpacing(0f);
        }

        float paddingL = paddingLeft != null ? paddingLeft : ButtonDimensions.PADDING_HORIZONTAL_PX;
        float paddingR = paddingRight != null ? paddingRight : ButtonDimensions.PADDING_HORIZONTAL_PX;
        float paddingT = paddingTop != null ? paddingTop : 0f;
        float paddingB = paddingBottom != null ? paddingBottom : 0f;

        float textWidth = textPaint.measureText(text);
        float textX = paddingL + textWidth / 2;

        // Text Y position with padding
        float textY;
        if (paddingTop != null || paddingBottom != null) {
            // Shift text by padding from center
            float centerY = ButtonDimensions.HEIGHT_PX / 2f;
            float shift = (paddingTop != null ? paddingTop : 0f) - (paddingBottom != null ? paddingBottom : 0f);
            textY = centerY + shift - (textPaint.descent() + textPaint.ascent()) / 2f;
        } else {
            textY = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
        }

        canvas.drawText(text, textX, textY + (boldText ? 2f : 0f), textPaint);
    }

    // --- Touch ---

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPressed = true;
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
                if (isPressed) {
                    isPressed = false;
                    invalidate();

                    // Check if touch is still within bounds
                    float x = event.getX();
                    float y = event.getY();
                    if (x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight()) {
                        if (onClickListener != null) {
                            onClickListener.onClick(this);
                        }
                    }
                }
                return true;

            case MotionEvent.ACTION_CANCEL:
                if (isPressed) {
                    isPressed = false;
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                // Update pressed state based on whether finger is still inside
                float mx = event.getX();
                float my = event.getY();
                boolean inside = mx >= 0 && mx <= getWidth() && my >= 0 && my <= getHeight();
                if (isPressed != inside) {
                    isPressed = inside;
                    invalidate();
                }
                return true;
        }

        return super.onTouchEvent(event);
    }
}

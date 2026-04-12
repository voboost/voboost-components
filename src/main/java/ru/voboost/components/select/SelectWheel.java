package ru.voboost.components.select;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

import ru.voboost.components.font.Font;
import ru.voboost.components.theme.Theme;

/**
 * SelectWheel — 3D cylindrical scrolling picker.
 *
 * <p>
 * Exactly matches the original WheelView implementation:
 * Camera/Matrix 3D transforms, Scroller with inertia,
 * VelocityTracker for fling, cyclic scrolling,
 * atmospheric fade, indicator lines, and curtain highlight.
 */
public class SelectWheel extends View implements Runnable {
    // Data
    private List<String> data = new ArrayList<>();
    private int currentPosition = 0;
    private int defaultItemPosition = 0;

    // Configuration
    private int visibleItemCount = SelectDimensions.WHEEL_VISIBLE_ITEMS;
    private boolean cyclicEnabled = false;
    private boolean atmosphericEnabled = true;
    private boolean curvedEnabled = true;
    private boolean indicatorEnabled = true;
    private boolean curtainEnabled = true;
    private boolean selectedTextBold = true;
    private int curvedMaxAngle = SelectDimensions.WHEEL_CURVED_MAX_ANGLE;

    // Colors
    private int textColor = Color.GRAY;
    private int selectedTextColor = Color.WHITE;
    private int indicatorColor = Color.GRAY;
    private int curtainColor = Color.WHITE;
    private float curtainRadius = SelectDimensions.WHEEL_CURTAIN_RADIUS_PX;

    // Text
    private float textSize = SelectDimensions.WHEEL_TEXT_SIZE_PX;
    private float selectedTextSize = SelectDimensions.WHEEL_SELECTED_TEXT_SIZE_PX;
    private float itemSpace = SelectDimensions.WHEEL_ITEM_SPACE_PX;
    private float indicatorSize = SelectDimensions.WHEEL_INDICATOR_SIZE_PX;

    // 3D transforms
    private final Camera camera = new Camera();
    private final Matrix matrixRotate = new Matrix();
    private final Matrix matrixDepth = new Matrix();

    // Scroll
    private final Scroller scroller;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private VelocityTracker tracker;
    private final int minimumVelocity;
    private final int maximumVelocity;
    private final int touchSlop;

    // Drawing
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private final Rect rectDrawn = new Rect();
    private final Rect rectCurrentItem = new Rect();
    private final Rect rectIndicatorHead = new Rect();
    private final Rect rectIndicatorFoot = new Rect();

    // Reusable drawing objects (performance optimization)
    private final Path curtainPath = new Path();
    private final float[] curtainRadii = new float[8];
    private final RectF curtainRect = new RectF();

    // Calculated values
    private int drawnItemCount;
    private int halfDrawnItemCount;
    private int itemHeight;
    private int halfItemHeight;
    private int halfWheelHeight;
    private int wheelCenterXCoordinate;
    private int wheelCenterYCoordinate;
    private int drawnCenterXCoordinate;
    private int drawnCenterYCoordinate;
    private int scrollOffsetYCoordinate;
    private int textMaxWidth;
    private int textMaxHeight;

    // Touch state
    private int lastPointYCoordinate;
    private int downPointYCoordinate;
    private int lastScrollPosition;
    private boolean isClick;
    private boolean isForceFinishScroll;
    private int minFlingYCoordinate;
    private int maxFlingYCoordinate;

    // Listener
    private OnWheelChangedListener onWheelChangedListener;

    // Touch handling constants
    private static final int VELOCITY_TRACKER_UNITS = 1000;
    private static final int ANIMATION_FRAME_DELAY_MS = 16; // ~60fps

    // Drawing constants
    private static final float CURTAIN_ALPHA = 128f; // 50% transparent
    private static final float ATMOSPHERIC_ALPHA_RATIO = 1.0f; // Full alpha ratio

    /**
     * Callback interface for wheel selection changes.
     */
    public interface OnWheelChangedListener {
        void onWheelSelected(SelectWheel wheel, int position);
        void onWheelScrolled(SelectWheel wheel, int scrollOffset);
        void onWheelScrollStateChanged(SelectWheel wheel, int state);
    }

    public SelectWheel(Context context) {
        this(context, null);
    }

    public SelectWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateVisibleItemCount();
        this.scroller = new Scroller(context);
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.minimumVelocity = vc.getScaledMinimumFlingVelocity();
        this.maximumVelocity = vc.getScaledMaximumFlingVelocity();
        this.touchSlop = vc.getScaledTouchSlop();
        initPaint();
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    private void initPaint() {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setFakeBoldText(false);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Font.getRegular(getContext()));
    }

    private void updateVisibleItemCount() {
        if (visibleItemCount < 2) {
            visibleItemCount = 2;
        }
        if (visibleItemCount % 2 == 0) {
            visibleItemCount++;
        }
        drawnItemCount = visibleItemCount + 2;
        halfDrawnItemCount = drawnItemCount / 2;
    }

    // --- Public API ---

    public void setData(List<String> data) {
        this.data = data != null ? new ArrayList<>(data) : new ArrayList<>();
        notifyDataSetChanged(0);
    }

    public void setData(List<String> data, int defaultPosition) {
        this.data = data != null ? new ArrayList<>(data) : new ArrayList<>();
        notifyDataSetChanged(defaultPosition);
    }

    public List<String> getData() {
        return data;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public String getCurrentItem() {
        return getItem(currentPosition);
    }

    public void setDefaultPosition(int position) {
        notifyDataSetChanged(position);
    }

    public void setCyclicEnabled(boolean enabled) {
        this.cyclicEnabled = enabled;
        computeFlingLimitYCoordinate();
        invalidate();
    }

    public void setAtmosphericEnabled(boolean enabled) {
        this.atmosphericEnabled = enabled;
        invalidate();
    }

    public void setCurvedEnabled(boolean enabled) {
        this.curvedEnabled = enabled;
        requestLayout();
        invalidate();
    }

    public void setIndicatorEnabled(boolean enabled) {
        this.indicatorEnabled = enabled;
        computeIndicatorRect();
        invalidate();
    }

    public void setCurtainEnabled(boolean enabled) {
        this.curtainEnabled = enabled;
        if (enabled) {
            indicatorEnabled = false;
        }
        computeCurrentItemRect();
        invalidate();
    }

    public void setSelectedTextBold(boolean bold) {
        this.selectedTextBold = bold;
        invalidate();
    }

    public void setTextColor(int color) {
        this.textColor = color;
        invalidate();
    }

    public void setSelectedTextColor(int color) {
        this.selectedTextColor = color;
        computeCurrentItemRect();
        invalidate();
    }

    public void setIndicatorColor(int color) {
        this.indicatorColor = color;
        invalidate();
    }

    public void setCurtainColor(int color) {
        this.curtainColor = color;
        invalidate();
    }

    public void setOnWheelChangedListener(OnWheelChangedListener listener) {
        this.onWheelChangedListener = listener;
    }

    /**
     * Applies theme colors.
     */
    public void applyColors(SelectColors colors) {
        if (colors == null) return;
        setTextColor(colors.wheelText);
        setSelectedTextColor(colors.wheelSelectedText);
        setIndicatorColor(colors.wheelIndicator);
        setCurtainColor(colors.wheelCurtain);
    }

    // --- Data ---

    private int getItemCount() {
        return data.size();
    }

    private String getItem(int position) {
        int size = data.size();
        if (size == 0) return "";
        int idx = (position % size + size) % size;
        return data.get(idx);
    }

    private void notifyDataSetChanged(int position) {
        int pos = Math.max(Math.min(position, getItemCount() - 1), 0);
        scrollOffsetYCoordinate = 0;
        defaultItemPosition = pos;
        currentPosition = pos;
        computeTextWidthAndHeight();
        computeFlingLimitYCoordinate();
        computeIndicatorRect();
        computeCurrentItemRect();
        requestLayout();
        invalidate();
    }

    // --- Measurement ---

    private void computeTextWidthAndHeight() {
        textMaxHeight = 0;
        textMaxWidth = 0;

        paint.setTextSize(Math.max(textSize, selectedTextSize));
        for (int i = 0; i < getItemCount(); i++) {
            textMaxWidth = Math.max(textMaxWidth, (int) paint.measureText(getItem(i)));
        }

        Paint.FontMetrics fm = paint.getFontMetrics();
        textMaxHeight = (int) (fm.bottom - fm.top);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        computeTextWidthAndHeight();

        int contentWidth = textMaxWidth + getPaddingLeft() + getPaddingRight();
        int contentHeight = (textMaxHeight * visibleItemCount)
                + ((int) (itemSpace * (visibleItemCount - 1)));
        if (curvedEnabled) {
            contentHeight = (int) ((contentHeight * 2) / Math.PI);
        }
        contentHeight += getPaddingTop() + getPaddingBottom();

        int width = resolveSize(contentWidth, widthMeasureSpec);
        int height = resolveSize(contentHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        rectDrawn.set(getPaddingLeft(), getPaddingTop(),
                w - getPaddingRight(), h - getPaddingBottom());
        wheelCenterXCoordinate = rectDrawn.centerX();
        wheelCenterYCoordinate = rectDrawn.centerY();
        drawnCenterXCoordinate = wheelCenterXCoordinate;
        drawnCenterYCoordinate = (int) (wheelCenterYCoordinate
                - (paint.ascent() + paint.descent()) / 2f);
        halfWheelHeight = rectDrawn.height() / 2;
        itemHeight = rectDrawn.height() / visibleItemCount;
        halfItemHeight = itemHeight / 2;
        computeFlingLimitYCoordinate();
        computeIndicatorRect();
        computeCurrentItemRect();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
        scroller.abortAnimation();
        cancelTracker();
    }

    private void computeFlingLimitYCoordinate() {
        int defaultOffset = defaultItemPosition * itemHeight;
        minFlingYCoordinate = cyclicEnabled
                ? Integer.MIN_VALUE
                : (-itemHeight * (getItemCount() - 1)) + defaultOffset;
        maxFlingYCoordinate = cyclicEnabled
                ? Integer.MAX_VALUE
                : defaultOffset;
    }

    private void computeIndicatorRect() {
        if (!indicatorEnabled) return;
        int halfInd = (int) (indicatorSize / 2f);
        int cy = wheelCenterYCoordinate;
        int hi = halfItemHeight;
        rectIndicatorHead.set(rectDrawn.left, cy + hi - halfInd, rectDrawn.right, cy + hi + halfInd);
        rectIndicatorFoot.set(rectDrawn.left, cy - hi - halfInd, rectDrawn.right, cy - hi + halfInd);
    }

    private void computeCurrentItemRect() {
        if (curtainEnabled || selectedTextColor != 0) {
            rectCurrentItem.set(
                    rectDrawn.left,
                    wheelCenterYCoordinate - halfItemHeight,
                    rectDrawn.right,
                    wheelCenterYCoordinate + halfItemHeight);
        }
    }

    // --- Drawing ---

    @Override
    protected void onDraw(Canvas canvas) {
        if (onWheelChangedListener != null) {
            onWheelChangedListener.onWheelScrolled(this, scrollOffsetYCoordinate);
        }
        if (itemHeight - halfDrawnItemCount <= 0) return;

        drawCurtain(canvas);
        drawIndicator(canvas);
        drawAllItems(canvas);
    }

    private void drawAllItems(Canvas canvas) {
        int scrolledItems = (-scrollOffsetYCoordinate) / itemHeight;
        int startIndex = defaultItemPosition + scrolledItems - halfDrawnItemCount;
        int counter = -halfDrawnItemCount;

        for (int i = startIndex; i < startIndex + drawnItemCount; i++) {
            // Reset paint
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setFakeBoldText(false);

            boolean isCenterItem = (i == startIndex + drawnItemCount / 2);

            int yOffset = drawnCenterYCoordinate
                    + (counter * itemHeight)
                    + (scrollOffsetYCoordinate % itemHeight);
            int distFromCenter = Math.abs(drawnCenterYCoordinate - yOffset);

            float ratio = ((drawnCenterYCoordinate - distFromCenter) - rectDrawn.top) * 1.0f
                    / (drawnCenterYCoordinate - rectDrawn.top);
            float degree = computeDegree(yOffset, ratio);
            float yAtAngle = computeYCoordinateAtAngle(degree);

            // 3D curved transform
            if (curvedEnabled) {
                float pivotY = wheelCenterYCoordinate - yAtAngle;
                camera.save();
                camera.rotateX(degree);
                camera.getMatrix(matrixRotate);
                camera.restore();
                matrixRotate.preTranslate(-wheelCenterXCoordinate, -pivotY);
                matrixRotate.postTranslate(wheelCenterXCoordinate, pivotY);

                camera.save();
                camera.translate(0, 0, computeDepth(degree));
                camera.getMatrix(matrixDepth);
                camera.restore();
                matrixDepth.preTranslate(-wheelCenterXCoordinate, -pivotY);
                matrixDepth.postTranslate(wheelCenterXCoordinate, pivotY);
                matrixRotate.postConcat(matrixDepth);
            }

            // Atmospheric fade
            if (atmosphericEnabled) {
                int alpha = Math.max(
                        (int) (((drawnCenterYCoordinate - distFromCenter) * 1.0f
                                / drawnCenterYCoordinate) * 255f), 0);
                paint.setAlpha(alpha);
            }

            // Draw item
            float drawY = curvedEnabled
                    ? drawnCenterYCoordinate - yAtAngle
                    : yOffset;
            drawItemText(canvas, i, isCenterItem, drawY);

            counter++;
        }
    }

    private void drawItemText(Canvas canvas, int index, boolean isCenter, float y) {
        String text = obtainItemText(index);

        if (selectedTextColor == 0) {
            // No special selected color — draw all same
            canvas.save();
            canvas.clipRect(rectDrawn);
            if (curvedEnabled) {
                canvas.concat(matrixRotate);
            }
            canvas.drawText(text, drawnCenterXCoordinate, y, paint);
            canvas.restore();
        } else if (textSize == selectedTextSize && !selectedTextBold) {
            // Same size, diff color — clip approach
            canvas.save();
            if (curvedEnabled) canvas.concat(matrixRotate);
            canvas.clipOutRect(rectCurrentItem);
            canvas.drawText(text, drawnCenterXCoordinate, y, paint);
            canvas.restore();

            paint.setColor(selectedTextColor);
            canvas.save();
            if (curvedEnabled) canvas.concat(matrixRotate);
            canvas.clipRect(rectCurrentItem);
            canvas.drawText(text, drawnCenterXCoordinate, y, paint);
            canvas.restore();
        } else {
            // Different size/bold for center item
            if (!isCenter) {
                canvas.save();
                if (curvedEnabled) canvas.concat(matrixRotate);
                canvas.drawText(text, drawnCenterXCoordinate, y, paint);
                canvas.restore();
            } else {
                paint.setColor(selectedTextColor);
                paint.setTextSize(selectedTextSize);
                paint.setFakeBoldText(selectedTextBold);
                canvas.save();
                if (curvedEnabled) canvas.concat(matrixRotate);
                canvas.drawText(text, drawnCenterXCoordinate, y, paint);
                canvas.restore();
            }
        }
    }

    private String obtainItemText(int index) {
        int count = getItemCount();
        if (cyclicEnabled) {
            if (count == 0) return "";
            int idx = index % count;
            if (idx < 0) idx += count;
            return data.get(idx);
        } else {
            if (index >= 0 && index < count) {
                return data.get(index);
            }
            return "";
        }
    }

    private float computeDegree(int itemY, float ratio) {
        int center = drawnCenterYCoordinate;
        int direction = itemY > center ? 1 : (itemY < center ? -1 : 0);
        float deg = -(1f - ratio) * curvedMaxAngle * direction;
        return clamp(deg, -curvedMaxAngle, curvedMaxAngle);
    }

    private float computeYCoordinateAtAngle(float deg) {
        return (sinDegree(deg) / sinDegree(curvedMaxAngle)) * halfWheelHeight;
    }

    private float sinDegree(float deg) {
        return (float) Math.sin(Math.toRadians(deg));
    }

    private int computeDepth(float deg) {
        return (int) (halfWheelHeight - (Math.cos(Math.toRadians(deg)) * halfWheelHeight));
    }

    private float clamp(float val, float min, float max) {
        return val < min ? min : Math.min(val, max);
    }

    private void drawCurtain(Canvas canvas) {
        if (!curtainEnabled) return;
        paint.setColor(Color.argb((int) CURTAIN_ALPHA,
                Color.red(curtainColor), Color.green(curtainColor), Color.blue(curtainColor)));
        paint.setStyle(Paint.Style.FILL);
        if (curtainRadius > 0) {
            curtainPath.reset();
            float r = curtainRadius;
            curtainRadii[0] = r; curtainRadii[1] = r;
            curtainRadii[2] = r; curtainRadii[3] = r;
            curtainRadii[4] = r; curtainRadii[5] = r;
            curtainRadii[6] = r; curtainRadii[7] = r;
            curtainRect.set(rectCurrentItem);
            curtainPath.addRoundRect(curtainRect, curtainRadii, Path.Direction.CCW);
            canvas.drawPath(curtainPath, paint);
        } else {
            canvas.drawRect(rectCurrentItem, paint);
        }
    }

    private void drawIndicator(Canvas canvas) {
        if (!indicatorEnabled) return;
        paint.setColor(indicatorColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectIndicatorHead, paint);
        canvas.drawRect(rectIndicatorFoot, paint);
    }

    // --- Touch handling ---

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                handleActionCancel(event);
                break;
        }
        if (isClick) {
            performClick();
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void handleActionDown(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        obtainOrClearTracker();
        tracker.addMovement(event);
        if (!scroller.isFinished()) {
            scroller.abortAnimation();
            isForceFinishScroll = true;
        }
        int y = (int) event.getY();
        lastPointYCoordinate = y;
        downPointYCoordinate = y;
    }

    private void handleActionMove(MotionEvent event) {
        int endDist = computeDistanceToEndPoint(scroller.getFinalY() % itemHeight);
        if (Math.abs(downPointYCoordinate - event.getY()) < touchSlop && endDist > 0) {
            isClick = true;
            return;
        }
        isClick = false;
        if (tracker != null) {
            tracker.addMovement(event);
        }
        if (onWheelChangedListener != null) {
            onWheelChangedListener.onWheelScrollStateChanged(this, 1);
        }
        float dy = event.getY() - lastPointYCoordinate;
        if (Math.abs(dy) < 1f) return;

        scrollOffsetYCoordinate += (int) dy;
        lastPointYCoordinate = (int) event.getY();
        invalidate();
    }

    private void handleActionUp(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        if (isClick) return;

        int yVelocity = 0;
        if (tracker != null) {
            tracker.addMovement(event);
            tracker.computeCurrentVelocity(VELOCITY_TRACKER_UNITS, maximumVelocity);
            yVelocity = (int) tracker.getYVelocity();
        }
        isForceFinishScroll = false;

        if (Math.abs(yVelocity) > minimumVelocity) {
            scroller.fling(0, scrollOffsetYCoordinate, 0, yVelocity,
                    0, 0, minFlingYCoordinate, maxFlingYCoordinate);
            int dist = computeDistanceToEndPoint(scroller.getFinalY() % itemHeight);
            scroller.setFinalY(scroller.getFinalY() + dist);
        } else {
            scroller.startScroll(0, scrollOffsetYCoordinate, 0,
                    computeDistanceToEndPoint(scrollOffsetYCoordinate % itemHeight));
        }

        if (!cyclicEnabled) {
            int finalY = scroller.getFinalY();
            if (finalY > maxFlingYCoordinate) {
                scroller.setFinalY(maxFlingYCoordinate);
            } else if (finalY < minFlingYCoordinate) {
                scroller.setFinalY(minFlingYCoordinate);
            }
        }

        handler.post(this);
        cancelTracker();
    }

    private void handleActionCancel(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        cancelTracker();
    }

    private void obtainOrClearTracker() {
        if (tracker == null) {
            tracker = VelocityTracker.obtain();
        } else {
            tracker.clear();
        }
    }

    private void cancelTracker() {
        if (tracker != null) {
            tracker.recycle();
            tracker = null;
        }
    }

    private int computeDistanceToEndPoint(int remainder) {
        if (Math.abs(remainder) <= halfItemHeight) {
            return -remainder;
        }
        return (scrollOffsetYCoordinate < 0 ? -itemHeight : itemHeight) - remainder;
    }

    // --- Runnable (scroll animation loop) ---

    @Override
    public void run() {
        if (itemHeight == 0) return;
        int count = getItemCount();
        if (count == 0) {
            if (onWheelChangedListener != null) {
                onWheelChangedListener.onWheelScrollStateChanged(this, 0);
            }
            return;
        }

        if (scroller.isFinished() && !isForceFinishScroll) {
            int pos = computePosition(count);
            if (pos < 0) pos += count;
            currentPosition = pos;
            if (onWheelChangedListener != null) {
                onWheelChangedListener.onWheelSelected(this, pos);
                onWheelChangedListener.onWheelScrollStateChanged(this, 0);
            }
            postInvalidate();
            return;
        }

        if (scroller.computeScrollOffset()) {
            if (onWheelChangedListener != null) {
                onWheelChangedListener.onWheelScrollStateChanged(this, 2);
            }
            scrollOffsetYCoordinate = scroller.getCurrY();
            int newPos = computePosition(count);
            if (lastScrollPosition != newPos) {
                lastScrollPosition = newPos;
            }
            postInvalidate();
            handler.postDelayed(this, ANIMATION_FRAME_DELAY_MS);
        }
    }

    private int computePosition(int itemCount) {
        return (((-scrollOffsetYCoordinate) / itemHeight) + defaultItemPosition) % itemCount;
    }

    /**
     * Smooth scroll to position with animation.
     */
    public void smoothScrollTo(int position) {
        int distance = currentPosition - position;
        int startY = scrollOffsetYCoordinate;
        ValueAnimator anim = ValueAnimator.ofInt(startY, (distance * itemHeight) + startY);
        anim.setDuration(300);
        anim.addUpdateListener(animation -> {
            scrollOffsetYCoordinate = (int) animation.getAnimatedValue();
            invalidate();
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                notifyDataSetChanged(position);
            }
        });
        anim.start();
    }
}

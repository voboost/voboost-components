package ru.voboost.components.screen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * ScrollView wrapper used inside {@link Screen} for both the tabs sidebar and
 * each panel. Implements rubber-band stretch on over-scroll, matching the
 * com.pateo.material.widgets.OverScrollView behavior used by the Voyah Setting
 * and VehicleSetting applications.
 *
 * <p>Behavior:
 * <ul>
 *   <li>On ACTION_MOVE while scrolled to top with downward drag, or scrolled
 *       to bottom with upward drag — the inner child view is translated by
 *       half the drag distance (damping factor of 2).</li>
 *   <li>On ACTION_UP — the inner view animates back to its original position
 *       via a 200ms TranslateAnimation.</li>
 *   <li>System over-scroll glow is disabled via setOverScrollMode(NEVER).</li>
 * </ul>
 */
public class ScreenView extends ScrollView {

    private static final int ANIM_DURATION = 200;
    private static final int DAMPING_NUM = 2;

    private boolean canScroll = true;
    private View mInnerView;
    private float mLastY;
    private Rect mRect;
    private final Object syncObj = new Object();

    public ScreenView(Context context) {
        super(context);
        init();
    }

    public ScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRect = new Rect();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        captureInnerView();
    }

    @Override public void addView(View child) {
        super.addView(child);
        captureInnerView();
    }

    @Override public void addView(View child, int index) {
        super.addView(child, index);
        captureInnerView();
    }

    @Override public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        captureInnerView();
    }

    @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        captureInnerView();
    }

    private void captureInnerView() {
        if (getChildCount() > 0) {
            mInnerView = getChildAt(0);
        }
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    @Override
    public void draw(Canvas canvas) {
        int saved = canvas.save();
        int sy = getScrollY();
        canvas.clipRect(0, sy, getWidth(), sy + getHeight());
        super.draw(canvas);
        canvas.restoreToCount(saved);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!canScroll) {
            return true;
        }
        if (mInnerView == null) {
            captureInnerView();
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_UP) {
            synchronized (syncObj) {
                if (!mRect.isEmpty()) {
                    rebound();
                    mRect.setEmpty();
                }
            }
            mLastY = 0.0f;
        } else if (actionMasked == MotionEvent.ACTION_MOVE) {
            float y = motionEvent.getY();
            float f = mLastY;
            int i = (int) (f - y);
            if (mInnerView != null
                    && ((f != 0.0f && isToTop() && i < 0) || (isToBottom() && i > 0))) {
                synchronized (syncObj) {
                    int left = mInnerView.getLeft();
                    int top = mInnerView.getTop();
                    int right = mInnerView.getRight();
                    int bottom = mInnerView.getBottom();
                    if (mRect.isEmpty()) {
                        mRect.set(left, top, right, bottom);
                    }
                    mInnerView.layout(
                            left,
                            top - (i / DAMPING_NUM),
                            right,
                            bottom - (i / DAMPING_NUM));
                }
            }
            mLastY = y;
        } else if (actionMasked == MotionEvent.ACTION_POINTER_UP) {
            mLastY = 0.0f;
        }
        return super.onTouchEvent(motionEvent);
    }

    private void rebound() {
        if (mInnerView == null) {
            return;
        }
        TranslateAnimation anim = new TranslateAnimation(
                0.0f, 0.0f, mInnerView.getTop(), mRect.top);
        anim.setDuration(ANIM_DURATION);
        mInnerView.startAnimation(anim);
        mInnerView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
    }

    private boolean isToBottom() {
        return mInnerView != null
                && getScrollY() == mInnerView.getMeasuredHeight() - getHeight() + getPaddingTop() + getPaddingBottom();
    }

    private boolean isToTop() {
        return getScrollY() == 0;
    }
}

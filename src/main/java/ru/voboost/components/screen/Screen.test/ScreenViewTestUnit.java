package ru.voboost.components.screen;

import static org.junit.Assert.*;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.GraphicsMode;

@RunWith(RobolectricTestRunner.class)
// NATIVE graphics mode is required for testDrawClipsToOwnBounds: the legacy
// ShadowCanvas does not paint pixels into a Bitmap-backed canvas, so drawColor
// would be a no-op and the pixel assertions would fail.
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class ScreenViewTestUnit {

    private ScreenView screenView;

    @Before
    public void setUp() {
        screenView = new ScreenView(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testOverScrollModeNever() {
        assertEquals(View.OVER_SCROLL_NEVER, screenView.getOverScrollMode());
    }

    @Test
    public void testCanScrollDefaultsTrue() {
        assertTrue(screenView.isCanScroll());
    }

    @Test
    public void testSetCanScrollFalseConsumesTouches() {
        screenView.setCanScroll(false);
        MotionEvent down = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0f, 0f, 0);
        boolean handled = screenView.onTouchEvent(down);
        down.recycle();
        assertTrue(handled);
    }

    @Test
    public void testAddViewCapturesInnerView() {
        LinearLayout child = new LinearLayout(ApplicationProvider.getApplicationContext());
        screenView.addView(child);
        MotionEvent move = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 0f, 0f, 0);
        screenView.onTouchEvent(move);
        move.recycle();
        assertEquals(child, screenView.getChildAt(0));
    }

    @Test
    public void testDrawClipsToOwnBounds() {
        View child = new View(ApplicationProvider.getApplicationContext()) {
            @Override
            protected void onDraw(Canvas c) {
                c.drawColor(Color.RED);
            }
        };
        child.setLayoutParams(new ViewGroup.LayoutParams(200, 300));
        screenView.addView(child);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);
        screenView.measure(widthSpec, heightSpec);
        screenView.layout(0, 0, 200, 100);
        child.layout(0, -100, 200, 200);

        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        screenView.draw(canvas);

        assertEquals(Color.RED, bitmap.getPixel(0, 0));
        assertEquals(0, bitmap.getPixel(0, 150));
    }
}

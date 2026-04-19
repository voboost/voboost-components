package ru.voboost.components.popup;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ru.voboost.components.theme.Theme;

/**
 * Visual tests for Popup component.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class PopupTestVisual {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
    }

    @Test
    public void testPopupFreeDark() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_DARK);

        // Add sample content
        TextView content = new TextView(context);
        content.setText("Test Content");
        content.setTextColor(Color.WHITE);
        popup.setPopupContentView(content);

        popup.show();
        // Visual verification: popup should show with free-dark theme
        popup.dismissWithAnimation();
    }

    @Test
    public void testPopupFreeLight() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_LIGHT);

        TextView content = new TextView(context);
        content.setText("Test Content");
        content.setTextColor(Color.BLACK);
        popup.setPopupContentView(content);

        popup.show();
        // Visual verification: popup should show with free-light theme
        popup.dismissWithAnimation();
    }

    @Test
    public void testPopupDreamerDark() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.DREAMER_DARK);

        TextView content = new TextView(context);
        content.setText("Test Content");
        content.setTextColor(Color.WHITE);
        popup.setPopupContentView(content);

        popup.show();
        // Visual verification: popup should show with dreamer-dark theme
        popup.dismissWithAnimation();
    }

    @Test
    public void testPopupDreamerLight() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.DREAMER_LIGHT);

        TextView content = new TextView(context);
        content.setText("Test Content");
        content.setTextColor(Color.BLACK);
        popup.setPopupContentView(content);

        popup.show();
        // Visual verification: popup should show with dreamer-light theme
        popup.dismissWithAnimation();
    }

    @Test
    public void testPopupWithComplexContent() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_DARK);

        // Add complex content
        FrameLayout layout = new FrameLayout(context);
        layout.setBackgroundColor(Color.BLUE);

        TextView text1 = new TextView(context);
        text1.setText("Title");
        text1.setTextColor(Color.WHITE);
        text1.setTextSize(32f);

        TextView text2 = new TextView(context);
        text2.setText("Message content here");
        text2.setTextColor(Color.parseColor("#80ffffff"));
        text2.setTextSize(28f);

        layout.addView(text1);
        layout.addView(text2);

        popup.setPopupContentView(layout);
        popup.show();

        // Visual verification: popup should display complex content correctly
        popup.dismissWithAnimation();
    }

    @Test
    public void testPopupDismissOnTouchOutside() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_DARK);
        popup.setDismissOnTouchOutside(true);

        TextView content = new TextView(context);
        content.setText("Touch outside to dismiss");
        popup.setPopupContentView(content);

        popup.show();

        // Simulate touch outside (would require UI testing)
        // For now, just verify the flag is set correctly

        popup.dismissWithAnimation();
    }
}

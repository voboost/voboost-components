package ru.voboost.components.section;

import static com.github.takahirom.roborazzi.RoborazziKt.captureRoboImage;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.github.takahirom.roborazzi.RoborazziOptions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = {33}, qualifiers = "w1920dp-h720dp-land-mdpi")
public class SectionPopupTestVisual {

    private static final String SCREENSHOT_BASE_PATH =
            "src/main/java/ru/voboost/components/section/Section.screenshots";

    private Activity activity;

    @Before
    public void setUp() {
        ActivityController<Activity> controller = Robolectric.buildActivity(Activity.class);
        controller.create().start().resume();
        activity = controller.get();
        activity.setContentView(new FrameLayout(activity));
    }

    private void capturePopup(Theme theme, String name) {
        SectionPopup popup = new SectionPopup(activity);
        popup.setTheme(theme);
        popup.setLanguage(Language.EN);
        Map<String, String> title = new HashMap<>();
        title.put("en", "Power mode");
        Map<String, String> text = new HashMap<>();
        text.put("en", "Generally, the range extender is not started. When the battery is high, the Pure EV mode is used. When the battery is low, the energy mode is HEV.");
        popup.setTitle(title);
        popup.setBlocks(java.util.Collections.singletonList(
                new Section.PopupBlock(null, text)));
        popup.show();
        View decor = activity.getWindow().getDecorView();
        captureRoboImage(decor, SCREENSHOT_BASE_PATH + "/" + name + ".png", new RoborazziOptions());
        popup.dismiss();
    }

    @Test
    public void popup_freeLight() {
        capturePopup(Theme.FREE_LIGHT, "popup_freeLight");
    }

    @Test
    public void popup_freeDark() {
        capturePopup(Theme.FREE_DARK, "popup_freeDark");
    }

    @Test
    public void popup_dreamerLight() {
        capturePopup(Theme.DREAMER_LIGHT, "popup_dreamerLight");
    }

    @Test
    public void popup_dreamerDark() {
        capturePopup(Theme.DREAMER_DARK, "popup_dreamerDark");
    }

    @Test
    public void popup_longTextScroll() {
        SectionPopup popup = new SectionPopup(activity);
        popup.setTheme(Theme.FREE_DARK);
        popup.setLanguage(Language.EN);
        Map<String, String> title = new HashMap<>();
        title.put("en", "Power mode");
        Map<String, String> text = new HashMap<>();
        text.put("en", "Generally, the range extender is not started. When the battery is high, the Pure EV mode is used. When the battery is low, the energy mode is HEV. This is a longer text to test scrolling functionality in the popup window.");
        popup.setTitle(title);
        popup.setBlocks(java.util.Collections.singletonList(
                new Section.PopupBlock(null, text)));
        popup.show();
        View decor = activity.getWindow().getDecorView();
        captureRoboImage(decor, SCREENSHOT_BASE_PATH + "/popup_longTextScroll.png", new RoborazziOptions());
        popup.dismiss();
    }

    @Test
    public void popup_multipleBlocks() {
        SectionPopup popup = new SectionPopup(activity);
        popup.setTheme(Theme.FREE_DARK);
        popup.setLanguage(Language.EN);
        Map<String, String> title = new HashMap<>();
        title.put("en", "Power mode");

        Map<String, String> block1Title = new HashMap<>();
        block1Title.put("en", "Pure EV");
        Map<String, String> block1Text = new HashMap<>();
        block1Text.put("en", "When the battery is high, the Pure EV mode is used.");

        Map<String, String> block2Title = new HashMap<>();
        block2Title.put("en", "HEV");
        Map<String, String> block2Text = new HashMap<>();
        block2Text.put("en", "When the battery is low, the energy mode is HEV.");

        popup.setTitle(title);
        popup.setBlocks(java.util.Arrays.asList(
                new Section.PopupBlock(block1Title, block1Text),
                new Section.PopupBlock(block2Title, block2Text)
        ));
        popup.show();
        View decor = activity.getWindow().getDecorView();
        captureRoboImage(decor, SCREENSHOT_BASE_PATH + "/popup_multipleBlocks.png", new RoborazziOptions());
        popup.dismiss();
    }
}

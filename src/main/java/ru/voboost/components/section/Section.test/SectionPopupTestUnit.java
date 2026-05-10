package ru.voboost.components.section;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

@RunWith(RobolectricTestRunner.class)
public class SectionPopupTestUnit {

    private Activity activity;

    @Before
    public void setUp() {
        ActivityController<Activity> controller = Robolectric.buildActivity(Activity.class);
        controller.create().start().resume();
        activity = controller.get();
    }

    @Test
    public void testCreate() {
        SectionPopup popup = new SectionPopup(activity);
        assertNotNull(popup);
        assertFalse(popup.isShowing());
    }

    @Test
    public void testShow() {
        SectionPopup popup = new SectionPopup(activity);
        popup.setTheme(Theme.FREE_DARK);
        popup.setLanguage(Language.EN);
        Map<String, String> title = new HashMap<>();
        title.put("en", "Power mode");
        Map<String, String> text1 = new HashMap<>();
        text1.put("en", "Help text");
        popup.setTitle(title);
        popup.setBlocks(java.util.Collections.singletonList(
                new Section.PopupBlock(null, text1)));
        popup.show();
        assertTrue(popup.isShowing());
        popup.dismiss();
    }

    @Test
    public void testShowMultipleBlocks() {
        SectionPopup popup = new SectionPopup(activity);
        popup.setTheme(Theme.FREE_DARK);
        popup.setLanguage(Language.EN);
        Map<String, String> title = new HashMap<>();
        title.put("en", "Power mode");

        Map<String, String> block1Title = new HashMap<>();
        block1Title.put("en", "Pure EV");
        Map<String, String> block1Text = new HashMap<>();
        block1Text.put("en", "Description...");

        Map<String, String> block2Title = new HashMap<>();
        block2Title.put("en", "HEV");
        Map<String, String> block2Text = new HashMap<>();
        block2Text.put("en", "Description...");

        popup.setTitle(title);
        popup.setBlocks(java.util.Arrays.asList(
                new Section.PopupBlock(block1Title, block1Text),
                new Section.PopupBlock(block2Title, block2Text)
        ));
        popup.show();
        assertTrue(popup.isShowing());
        popup.dismiss();
    }

    @Test
    public void testShowWithoutThemeIsNoOp() {
        SectionPopup popup = new SectionPopup(activity);
        popup.show();
        assertFalse(popup.isShowing());
    }
}

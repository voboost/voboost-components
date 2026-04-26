package ru.voboost.components.panel;

import static org.junit.Assert.*;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.section.Section;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

@RunWith(RobolectricTestRunner.class)
public class PanelTestUnit {

    private Context context;
    private Panel panel;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        panel = new Panel(context);
    }

    @Test
    public void testIsLinearLayout() {
        assertTrue(panel instanceof LinearLayout);
    }

    @Test
    public void testOrientationVertical() {
        assertEquals(LinearLayout.VERTICAL, panel.getOrientation());
    }

    @Test
    public void testAddViewAppearsAsDirectChild() {
        View child = new View(context);
        panel.addView(child);
        assertEquals(1, panel.getChildCount());
        assertEquals(child, panel.getChildAt(0));
    }

    @Test
    public void testRemoveView() {
        View child = new View(context);
        panel.addView(child);
        panel.removeView(child);
        assertEquals(0, panel.getChildCount());
    }

    @Test
    public void testRemoveAllViews() {
        panel.addView(new View(context));
        panel.addView(new View(context));
        panel.removeAllViews();
        assertEquals(0, panel.getChildCount());
    }

    @Test
    public void testSetTheme() {
        panel.setTheme(Theme.FREE_LIGHT);
        assertEquals(Theme.FREE_LIGHT, panel.getCurrentTheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetThemeNullThrows() {
        panel.setTheme(null);
    }

    @Test
    public void testSetLanguage() {
        panel.setLanguage(Language.EN);
        assertEquals(Language.EN, panel.getCurrentLanguage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLanguageNullThrows() {
        panel.setLanguage(null);
    }

    @Test
    public void testThemePropagatesToThemableChild() {
        ThemableLanguageStub stub = new ThemableLanguageStub(context);
        panel.addView(stub);
        panel.setTheme(Theme.DREAMER_DARK);
        assertEquals(Theme.DREAMER_DARK, stub.lastTheme);
    }

    @Test
    public void testLanguagePropagatesToLocalizableChild() {
        ThemableLanguageStub stub = new ThemableLanguageStub(context);
        panel.addView(stub);
        panel.setLanguage(Language.RU);
        assertEquals(Language.RU, stub.lastLanguage);
    }

    @Test
    public void testGetImageWidthReturnsPanelThemeImageWidth() {
        assertEquals(PanelTheme.IMAGE_WIDTH, panel.getImageWidth());
        assertEquals(800, panel.getImageWidth());
    }

    @Test
    public void testGetImageMarginRightReturnsPanelThemeImageMarginRight() {
        assertEquals(PanelTheme.IMAGE_MARGIN_RIGHT, panel.getImageMarginRight());
        assertEquals(-30, panel.getImageMarginRight());
    }

    @Test
    public void testCompactDefaultsToFalse() {
        assertFalse(panel.isCompact());
    }

    @Test
    public void testSetCompactTrueSetsTopPadding50() {
        panel.setCompact(true);

        assertTrue(panel.isCompact());
        assertEquals(PanelTheme.COMPACT_TOP_MARGIN, panel.getPaddingTop());
        assertEquals(50, panel.getPaddingTop());
    }

    @Test
    public void testSetCompactFalseResetsTopPadding() {
        panel.setCompact(true);
        panel.setCompact(false);

        assertFalse(panel.isCompact());
        assertEquals(0, panel.getPaddingTop());
    }

    @Test
    public void testInitSetsBottomPadding() {
        assertEquals(PanelTheme.PADDING_BOTTOM, panel.getPaddingBottom());
        assertEquals(25, panel.getPaddingBottom());
    }

    @Test
    public void testLastSectionOmitsBottomMargin() {
        Section s0 = new Section(context);
        Section s1 = new Section(context);
        Section s2 = new Section(context);
        panel.addView(s0);
        panel.addView(s1);
        panel.addView(s2);

        assertTrue(s0.isIncludeBottomMargin());
        assertTrue(s1.isIncludeBottomMargin());
        assertFalse(s2.isIncludeBottomMargin());
    }

    @Test
    public void testRemoveLastSectionUpdatesMargins() {
        Section s0 = new Section(context);
        Section s1 = new Section(context);
        panel.addView(s0);
        panel.addView(s1);

        assertFalse(s1.isIncludeBottomMargin());

        panel.removeView(s1);

        assertFalse(s0.isIncludeBottomMargin());
    }

    @Test
    public void testSingleSectionOmitsBottomMargin() {
        Section s = new Section(context);
        panel.addView(s);
        assertFalse(s.isIncludeBottomMargin());
    }

    private static class ThemableLanguageStub extends View implements IThemable, ILocalizable {
        Theme lastTheme;
        Language lastLanguage;
        ThemableLanguageStub(Context ctx) { super(ctx); }
        @Override public void setTheme(Theme theme) { lastTheme = theme; }
        @Override public void propagateTheme(Theme theme) { }
        public Theme getCurrentTheme() { return lastTheme; }
        @Override public void setLanguage(Language language) { lastLanguage = language; }
        @Override public void propagateLanguage(Language language) { }
        @Override public Language getCurrentLanguage() { return lastLanguage; }
    }
}

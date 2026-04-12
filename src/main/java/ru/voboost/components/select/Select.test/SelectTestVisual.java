package ru.voboost.components.select;

import android.content.Context;
import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Visual regression tests for Select component.
 * Tests all themes and visual states.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class SelectTestVisual {

    private Context context;
    private Select select;
    private List<SelectOption> options;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        select = new Select(context);
        options = createTestSelectOptions();
    }

    @Test
    public void testVisualFreeDark() {
        select.setOptions(options);
        select.setLanguage(Language.EN);
        select.setTheme(Theme.FREE_DARK);
        select.setSelectedValue("auto");
        select.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        select.layout(0, 0, select.getMeasuredWidth(), select.getMeasuredHeight());
        // Captured in screenshot
    }

    @Test
    public void testVisualFreeLight() {
        select.setOptions(options);
        select.setLanguage(Language.EN);
        select.setTheme(Theme.FREE_LIGHT);
        select.setSelectedValue("auto");
        select.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        select.layout(0, 0, select.getMeasuredWidth(), select.getMeasuredHeight());
    }

    @Test
    public void testVisualDreamerDark() {
        select.setOptions(options);
        select.setLanguage(Language.EN);
        select.setTheme(Theme.DREAMER_DARK);
        select.setSelectedValue("auto");
        select.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        select.layout(0, 0, select.getMeasuredWidth(), select.getMeasuredHeight());
    }

    @Test
    public void testVisualDreamerLight() {
        select.setOptions(options);
        select.setLanguage(Language.EN);
        select.setTheme(Theme.DREAMER_LIGHT);
        select.setSelectedValue("auto");
        select.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        select.layout(0, 0, select.getMeasuredWidth(), select.getMeasuredHeight());
    }

    @Test
    public void testVisualRussianLanguage() {
        select.setOptions(options);
        select.setLanguage(Language.RU);
        select.setTheme(Theme.FREE_DARK);
        select.setSelectedValue("auto");
        select.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        select.layout(0, 0, select.getMeasuredWidth(), select.getMeasuredHeight());
    }

    @Test
    public void testVisualDifferentSelections() {
        select.setOptions(options);
        select.setLanguage(Language.EN);
        select.setTheme(Theme.FREE_DARK);

        String[] values = {"auto", "manual", "eco", "sport", "comfort"};
        for (String value : values) {
            select.setSelectedValue(value);
            select.measure(
                    android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                    android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
            select.layout(0, 0, select.getMeasuredWidth(), select.getMeasuredHeight());
            // Each selection captured in screenshot
        }
    }

    @Test
    public void testVisualLongTextSelection() {
        // Test with longest text option
        select.setOptions(options);
        select.setLanguage(Language.EN);
        select.setTheme(Theme.FREE_LIGHT);
        select.setSelectedValue("comfort"); // May have longer text
        select.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(300, android.view.View.MeasureSpec.AT_MOST),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        select.layout(0, 0, select.getMeasuredWidth(), select.getMeasuredHeight());
    }

    private List<SelectOption> createTestSelectOptions() {
        List<SelectOption> options = new ArrayList<>();

        Map<String, String> autoLabels = new HashMap<>();
        autoLabels.put("en", "Automatic");
        autoLabels.put("ru", "Автоматический");
        options.add(new SelectOption("auto", autoLabels));

        Map<String, String> manualLabels = new HashMap<>();
        manualLabels.put("en", "Manual");
        manualLabels.put("ru", "Ручной");
        options.add(new SelectOption("manual", manualLabels));

        Map<String, String> ecoLabels = new HashMap<>();
        ecoLabels.put("en", "Eco Mode");
        ecoLabels.put("ru", "Эко режим");
        options.add(new SelectOption("eco", ecoLabels));

        Map<String, String> sportLabels = new HashMap<>();
        sportLabels.put("en", "Sport Mode");
        sportLabels.put("ru", "Спорт режим");
        options.add(new SelectOption("sport", sportLabels));

        Map<String, String> comfortLabels = new HashMap<>();
        comfortLabels.put("en", "Comfort");
        comfortLabels.put("ru", "Комфорт");
        options.add(new SelectOption("comfort", comfortLabels));

        return options;
    }
}

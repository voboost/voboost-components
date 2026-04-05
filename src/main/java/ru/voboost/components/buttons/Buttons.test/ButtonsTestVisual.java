package ru.voboost.components.buttons;

import static org.junit.Assert.assertNotNull;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Visual regression tests for Buttons component.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = {33}, qualifiers = "w1920dp-h720dp-land-mdpi")
public class ButtonsTestVisual {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
    }

    @Test
    public void buttons_free_light() {
        Buttons buttons = new Buttons(context);
        buttons.setTheme(Theme.FREE_LIGHT);
        buttons.setLanguage(Language.EN);
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("lower", "Lower"),
                new ButtonConfig("restore", "Restore")
        );
        buttons.setButtons(configs);
        buttons.setSelectedValue("restore");
        buttons.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        buttons.layout(0, 0, 1000, 200);

        assertNotNull(buttons);
    }

    @Test
    public void buttons_free_dark() {
        Buttons buttons = new Buttons(context);
        buttons.setTheme(Theme.FREE_DARK);
        buttons.setLanguage(Language.EN);
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("lower", "Lower"),
                new ButtonConfig("restore", "Restore")
        );
        buttons.setButtons(configs);
        buttons.setSelectedValue("restore");
        buttons.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        buttons.layout(0, 0, 1000, 200);

        assertNotNull(buttons);
    }

    @Test
    public void buttons_dreamer_light() {
        Buttons buttons = new Buttons(context);
        buttons.setTheme(Theme.DREAMER_LIGHT);
        buttons.setLanguage(Language.EN);
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("lower", "Lower"),
                new ButtonConfig("restore", "Restore")
        );
        buttons.setButtons(configs);
        buttons.setSelectedValue("restore");
        buttons.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        buttons.layout(0, 0, 1000, 200);

        assertNotNull(buttons);
    }

    @Test
    public void buttons_dreamer_dark() {
        Buttons buttons = new Buttons(context);
        buttons.setTheme(Theme.DREAMER_DARK);
        buttons.setLanguage(Language.EN);
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("lower", "Lower"),
                new ButtonConfig("restore", "Restore")
        );
        buttons.setButtons(configs);
        buttons.setSelectedValue("restore");
        buttons.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        buttons.layout(0, 0, 1000, 200);

        assertNotNull(buttons);
    }

    @Test
    public void buttons_with_right_text() {
        Buttons buttons = new Buttons(context);
        buttons.setTheme(Theme.FREE_LIGHT);
        buttons.setLanguage(Language.EN);
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("lower", "Lower"),
                new ButtonConfig("restore", "Restore")
        );
        buttons.setButtons(configs);
        buttons.setSelectedValue("restore");
        Map<String, String> rightText = Map.of(
                "en", "Suspension level",
                "ru", "Уровень подвески"
        );
        buttons.setRightText(rightText);
        buttons.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        buttons.layout(0, 0, 1000, 200);

        assertNotNull(buttons);
    }

    @Test
    public void buttons_with_description() {
        Buttons buttons = new Buttons(context);
        buttons.setTheme(Theme.FREE_LIGHT);
        buttons.setLanguage(Language.EN);
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("eco", "ECO"),
                new ButtonConfig("comfort", "Comfort"),
                new ButtonConfig("sport", "Sport")
        );
        buttons.setButtons(configs);
        buttons.setSelectedValue("comfort");
        Map<String, String> description = Map.of(
                "en", "Select preferred driving mode",
                "ru", "Выберите предпочтительный режим вождения"
        );
        buttons.setDescription(description);
        buttons.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        buttons.layout(0, 0, 1000, 200);

        assertNotNull(buttons);
    }

    @Test
    public void buttons_with_right_text_and_description() {
        Buttons buttons = new Buttons(context);
        buttons.setTheme(Theme.FREE_LIGHT);
        buttons.setLanguage(Language.EN);
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("on", "ON"),
                new ButtonConfig("off", "OFF")
        );
        buttons.setButtons(configs);
        buttons.setSelectedValue("on");
        Map<String, String> rightText = Map.of(
                "en", "Auto headlamp",
                "ru", "Авто фары"
        );
        buttons.setRightText(rightText);
        Map<String, String> description = Map.of(
                "en", "Lower suspension when parked for easy entry",
                "ru", "Понизить подвеску при парковке для удобной посадки"
        );
        buttons.setDescription(description);
        buttons.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        buttons.layout(0, 0, 1000, 200);

        assertNotNull(buttons);
    }

    @Test
    public void buttons_russian() {
        Buttons buttons = new Buttons(context);
        buttons.setTheme(Theme.FREE_LIGHT);
        buttons.setLanguage(Language.RU);
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("lower", "Нижний"),
                new ButtonConfig("restore", "Восстановить")
        );
        buttons.setButtons(configs);
        buttons.setSelectedValue("restore");
        Map<String, String> rightText = Map.of(
                "en", "Suspension level",
                "ru", "Уровень подвески"
        );
        buttons.setRightText(rightText);
        buttons.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(200, android.view.View.MeasureSpec.EXACTLY));
        buttons.layout(0, 0, 1000, 200);

        assertNotNull(buttons);
    }
}

package ru.voboost.components.demo.pixel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import ru.voboost.components.button.Button;
import ru.voboost.components.button.ButtonStyle;
import ru.voboost.components.checkbox.Checkbox;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.radio.Radio;
import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.screen.Screen;
import ru.voboost.components.section.Section;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;

public class SectionInfoShort extends Activity {

    public static final String EXTRA_THEME = "theme";

    private Theme theme = Theme.FREE_DARK;
    private static final Language LANGUAGE = Language.EN;

    private Screen screen;
    private Section section2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) {
            // Ignore under Robolectric
        }

        if (getIntent() != null) {
            String themeValue = getIntent().getStringExtra(EXTRA_THEME);
            if (themeValue != null) {
                theme = Theme.fromValue(themeValue);
            }
        }

        setupComponentHierarchy();
    }

    private void setupComponentHierarchy() {
        List<TabItem> tabItems = new ArrayList<>();
        tabItems.add(TabItem.create("drv-pref", createMap("Drv pref")).selected(true).build());
        tabItems.add(TabItem.create("drv-assist", createMap("Drv assist")).build());
        tabItems.add(TabItem.create("sfty-maint", createMap("SFTY maint")).build());
        tabItems.add(TabItem.create("vehicle-health", createMap("Vehicle health")).build());
        tabItems.add(TabItem.create("settings", createMap("Settings")).marginTop(30).more(true).build());

        Panel[] panels = new Panel[] {
                createDrvPrefPanel(),
                new Panel(this),
                new Panel(this),
                new Panel(this),
                new Panel(this)
        };

        screen = Screen.create(this, theme)
                .tabs(Tabs.create(this, theme, LANGUAGE, tabItems).build())
                .panels(panels)
                .compactPanel(true)
                .build();
        setContentView(screen);

        loadCompactImage();

        screen.getTabs().setSelectedValue("drv-pref", false);
    }

    private void loadCompactImage() {
        String imageName = "drive_mode_individal_97c_80.png";
        String[] paths = {
            "java/ru/voboost/components/demo/pixel/MainActivity.screenshots/" + imageName,
            "src/demo-pixel/java/ru/voboost/components/demo/pixel/MainActivity.screenshots/" + imageName
        };

        System.out.println("[SectionInfoShort01] user.dir = " + System.getProperty("user.dir"));

        for (String path : paths) {
            java.io.File file = new java.io.File(path);
            System.out.println("[SectionInfoShort01] Trying path: " + file.getAbsolutePath() + " exists=" + file.exists());
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                System.out.println("[SectionInfoShort01] Decoded bitmap: " + (bitmap != null ? bitmap.getWidth() + "x" + bitmap.getHeight() : "null"));
                if (bitmap != null) {
                    screen.getActivePanel().setImage(bitmap);
                    return;
                }
            }
        }

        // Try classpath resource
        try {
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(imageName);
            System.out.println("[SectionInfoShort01] Classpath resource: " + (is != null ? "found" : "not found"));
            if (is != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                System.out.println("[SectionInfoShort01] Decoded classpath bitmap: " + (bitmap != null ? bitmap.getWidth() + "x" + bitmap.getHeight() : "null"));
                if (bitmap != null) {
                    screen.getActivePanel().setImage(bitmap);
                    is.close();
                    return;
                }
                is.close();
            }
        } catch (Exception e) {
            System.out.println("[SectionInfoShort01] Classpath error: " + e.getMessage());
        }

        System.out.println("[SectionInfoShort01] WARNING: Failed to load compact panel image");
    }

    private Panel createDrvPrefPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(theme);

        // Section 1: Drive mode
        Section section1 = new Section(this);
        section1.setTitle(createMap("Drive mode"));
        section1.setTheme(theme);
        section1.setLanguage(LANGUAGE);
        section1.setCompactWidth(true);

        List<RadioButton> driveModeButtons = new ArrayList<>();
        driveModeButtons.add(new RadioButton("eco", createMap("Eco")));
        driveModeButtons.add(new RadioButton("comf", createMap("Comf")));
        driveModeButtons.add(new RadioButton("perf", createMap("Perf")));
        driveModeButtons.add(new RadioButton("outing", createMap("Outing")));
        driveModeButtons.add(new RadioButton("snow", createMap("Snow")));
        driveModeButtons.add(new RadioButton("indiv", createMap("Indiv")));

        section1.addRadio(
                Radio.create(this, theme, LANGUAGE, driveModeButtons, "indiv")
                        .width(645)
                        .margin(0, 0, 0, 37)
                        .build()
        );

        section1.addButton(
                Button.create(this, theme, LANGUAGE, "Settings", ButtonStyle.SECONDARY)
                        .boldText(true)
                        .padding(49, 0, 49, 0)
                        .description(createMap(
                                "Press Settings to define your drive\nmode."))
                        .build()
        );

        panel.addView(section1);

        // Section 2: Power mode (with info icon)
        section2 = new Section(this);
        section2.setTitle(createMap("Power mode"));
        section2.setTheme(theme);
        section2.setLanguage(LANGUAGE);
        section2.setCompactWidth(true);
        java.util.List<Section.PopupBlock> popupBlocks = new ArrayList<>();
        popupBlocks.add(new Section.PopupBlock(
                createMap("Electric mode first"),
                createMap("Generally, the range extender is not started. When the battery is high, the Pure EV mode is used. When the battery is low, the energy mode is HEV, and the battery is recharged at the same time. In this mode, the battery power will be kept at a low level.")));
        popupBlocks.add(new Section.PopupBlock(
                createMap("Forced EV"),
                createMap("Running in Pure EV mode, able to use more power to the maximum extent, only triggering the range extender to start when the battery is extremely low. Recommended for use when approaching an energy replenishment station, so that the vehicle can travel the maximum mileage in Pure EV mode.")));
        section2.setPopupText(popupBlocks);

        List<RadioButton> powerModeButtons = new ArrayList<>();
        powerModeButtons.add(new RadioButton("electric", createMap("Electric mode")));
        powerModeButtons.add(new RadioButton("hybrid", createMap("Hybrid")));
        powerModeButtons.add(new RadioButton("save", createMap("Save Mode")));

        // Pre-create extra controls, initially hidden
        Checkbox forcedEv = new Checkbox(this);
        forcedEv.setTheme(theme);
        forcedEv.setLanguage(LANGUAGE);
        forcedEv.setChecked(false);
        forcedEv.setLabel(createMap("Forced EV"));
        forcedEv.setVisibility(View.GONE);

        List<RadioButton> chargeButtons = new ArrayList<>();
        chargeButtons.add(new RadioButton("25", createMap("25%")));
        chargeButtons.add(new RadioButton("50", createMap("50%")));
        chargeButtons.add(new RadioButton("80", createMap("80%")));

        Radio chargeRadio = Radio.create(this, theme, LANGUAGE, chargeButtons, "50")
                .title(createMap("Keep target electricity level"))
                .width(645)
                .build();
        chargeRadio.setVisibility(View.GONE);

        section2.addRadio(
                Radio.create(this, theme, LANGUAGE, powerModeButtons, "electric")
                        .width(645)
                        .margin(0, 0, 0, 0)
                        .on("electric", radio -> {
                            radio.setDescription(createMap(
                                    "Recommended for use when you need to consume the most\npower before going to a charging station to replenish energy."));
                            forcedEv.setVisibility(View.VISIBLE);
                            chargeRadio.setVisibility(View.GONE);
                        })
                        .on("hybrid", radio -> {
                            radio.setDescription(createMap(
                                    "Recommended for use when running long distances or on\nthe highway with high battery power to ensure longer range."));
                            forcedEv.setVisibility(View.GONE);
                            chargeRadio.setVisibility(View.GONE);
                        })
                        .on("save", radio -> {
                            radio.setDescription(createMap(
                                    "When the set critical value is reached, the range extender\nwill be started to replenish power to maintain the current set value."));
                            forcedEv.setVisibility(View.GONE);
                            chargeRadio.setVisibility(View.VISIBLE);
                        })
                        .build()
        );

        section2.addCheckbox(forcedEv);
        section2.addRadio(chargeRadio);

        panel.addView(section2);

        return panel;
    }

    private Map<String, String> createMap(String text) {
        Map<String, String> map = new HashMap<>();
        map.put("en", text);
        map.put("ru", text);
        return map;
    }

    public Screen getScreen() {
        return screen;
    }

    public Section getSection2() {
        return section2;
    }
}

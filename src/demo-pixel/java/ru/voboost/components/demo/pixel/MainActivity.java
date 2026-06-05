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

/**
 * Single pixel-demo activity. One Screen holds the content of every demo,
 * distributed across a single canonical tab list. Each screenshot is produced
 * by selecting a tab, scrolling and rendering. The "sound" tab carries the
 * compact drive-mode panel (with car image and a popup section); the Screen
 * reads compact mode from the active panel, so selecting it switches layout.
 *
 * Configured via Intent extra:
 *   EXTRA_THEME - theme value (Theme.fromValue), default FREE_DARK
 *
 * Tabs (canonical order):
 *   network, display, voice, sound, reminder, device, privacy, system, vehicle(more)
 */
public class MainActivity extends Activity {

    public static final String EXTRA_THEME = "theme";

    private static final Language LANGUAGE = Language.EN;

    private Theme theme = Theme.FREE_DARK;

    private Screen screen;
    private Panel drivePanel;
    private Section driveSection2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupFullScreenMode();

        if (getIntent() != null) {
            String themeValue = getIntent().getStringExtra(EXTRA_THEME);
            if (themeValue != null) {
                theme = Theme.fromValue(themeValue);
            }
        }

        setupComponentHierarchy();
    }

    private void setupFullScreenMode() {
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
            // Ignore under Robolectric where getDecorView() may return null
        }
    }

    private void setupComponentHierarchy() {
        List<TabItem> tabItems = createTabItems();
        Panel[] panels = createPanels();

        screen = Screen.create(this, theme)
                .tabs(Tabs.create(this, theme, LANGUAGE, tabItems).build())
                .panels(panels)
                .build();
        setContentView(screen);

        screen.getTabs().setSelectedValue("display", false);

        loadCompactImage();
    }

    private List<TabItem> createTabItems() {
        List<TabItem> items = new ArrayList<>();
        items.add(TabItem.create("network", createMap("Network")).build());
        items.add(TabItem.create("display", createMap("Display")).build());
        items.add(TabItem.create("voice", createMap("Voice")).build());
        items.add(TabItem.create("sound", createMap("Sound")).build());
        items.add(TabItem.create("reminder", createMap("Reminder")).build());
        items.add(TabItem.create("device", createMap("Device")).build());
        items.add(TabItem.create("privacy", createMap("Privacy")).build());
        items.add(TabItem.create("system", createMap("System")).build());
        items.add(TabItem.create("vehicle", createMap("Vehicle")).marginTop(30).more(true).build());
        return items;
    }

    private Panel[] createPanels() {
        Panel[] panels = new Panel[9];
        panels[0] = createNetworkCheckboxPanel();   // network
        panels[1] = createDisplayRadioPanel();       // display (validated A)
        panels[2] = createDisplayCheckboxPanel();    // voice
        panels[3] = createDrivePrefPanel();          // sound (compact)
        panels[4] = createEmptyPanel();              // reminder
        panels[5] = createSystemBcrPanel();          // device
        panels[6] = createPrivacyCheckboxPanel();    // privacy
        panels[7] = createSystemButtonsPanel();      // system (validated B)
        panels[8] = createEmptyPanel();              // vehicle
        drivePanel = panels[3];
        return panels;
    }

    private Panel createEmptyPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(theme);
        return panel;
    }

    private Panel createDisplayRadioPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(theme);

        for (int i = 0; i < 3; i++) {
            Section section = new Section(this);
            section.setTitle(createMap("Language"));
            section.setTheme(theme);
            section.setLanguage(LANGUAGE);

            List<RadioButton> radioButtons = new ArrayList<>();
            radioButtons.add(new RadioButton("zh", createMap("Chinese (Simplified)")));
            radioButtons.add(new RadioButton("en", createMap("English")));

            section.addRadio(
                    Radio.create(this, theme, LANGUAGE, radioButtons, "en").build());

            panel.addView(section);
        }

        return panel;
    }

    private Panel createSystemButtonsPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(theme);

        Section section1 = Section.create(this, theme, LANGUAGE, createMap("Factory reset"))
                .paddingTop(34)
                .paddingBottom(35)
                .build();
        section1.addButton(
                Button.create(this, theme, LANGUAGE, "Factory reset", ButtonStyle.SECONDARY)
                        .margin(0, 0, 0, 0)
                        .padding(45, 1, 44, 0)
                        .build());
        panel.addView(section1);

        Section section2 = Section.create(this, theme, LANGUAGE,
                createMap("Privacy Agreement and Service Agreement"))
                .paddingTop(30)
                .paddingBottom(30)
                .build();
        section2.addButton(
                Button.create(this, theme, LANGUAGE, "Check", ButtonStyle.SECONDARY)
                        .margin(0, 0, 0, 0)
                        .padding(37, 1, 36, 0)
                        .build());
        panel.addView(section2);

        Section section3 = Section.create(this, theme, LANGUAGE,
                createMap("Statement on Use of Open Source Code"))
                .paddingTop(30)
                .paddingBottom(30)
                .build();
        section3.addButton(
                Button.create(this, theme, LANGUAGE, "Check", ButtonStyle.SECONDARY)
                        .margin(0, 0, 0, 0)
                        .padding(37, 1, 36, 0)
                        .build());
        panel.addView(section3);

        return panel;
    }

    private Panel createDisplayCheckboxPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(theme);

        Section section = new Section(this);
        section.setTitle(createMap("Mobile"));
        section.setTheme(theme);
        section.setLanguage(LANGUAGE);

        section.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, false)
                        .label(createMap("Enable 5G"))
                        .description(createMap("When enabled, 5G will be automatically used in the 5G mobile network environment."))
                        .build());

        Checkbox cb2 = section.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, false)
                        .label(createMap("System key t"))
                        .build());
        cb2.setPrimitiveAlpha(0.5f);

        Checkbox cb3 = section.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, true)
                        .label(createMap("Reduce media volume during navigation br"))
                        .description(createMap("If too high during navigation br"))
                        .build());
        cb3.setTransitionState(1.0f, 0.45f);

        section.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, true)
                        .label(createMap("Speed compensated volume"))
                        .description(createMap("The volume increases or decr\nvolume frequently"))
                        .build());

        panel.addView(section);

        return panel;
    }

    private Panel createNetworkCheckboxPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(theme);

        Section section1 = new Section(this);
        section1.setTitle(createMap("Mobile data network"));
        section1.setTheme(theme);
        section1.setLanguage(LANGUAGE);

        section1.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, false)
                        .label(createMap("Enable 5G"))
                        .description(createMap("When enabled, 5G will be automatically used in the 5G mobile network environment."))
                        .build());
        panel.addView(section1);

        Section section2 = new Section(this);
        section2.setTitle(createMap("Mobile data network"));
        section2.setTheme(theme);
        section2.setLanguage(LANGUAGE);

        section2.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, false)
                        .label(createMap("Reduce media volume during navigation broadcast"))
                        .description(createMap("If too high during navigation broadcast, media volume will be automatically lowered."))
                        .build());

        section2.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, true)
                        .label(createMap("Speed compensated volume"))
                        .description(createMap("The volume increases or decreases as the vehicle goes faster or slower. You no longer have to adjust the\nvolume frequently, thus able to focus on driving."))
                        .build());

        panel.addView(section2);

        return panel;
    }

    private Panel createPrivacyCheckboxPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(theme);

        Section section = new Section(this);
        section.setTitle(createMap("Privacy protection"));
        section.setTheme(theme);
        section.setLanguage(LANGUAGE);
        section.setPaddingTop(23);
        section.setTitleCheckbox(true);

        section.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, true)
                        .label(createMap("Always remind me when system starts"))
                        .margin(1, 4, 0, 52)
                        .build());

        section.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, true)
                        .label(createMap("Bluetooth call privacy"))
                        .description(createMap(
                                "The incoming call number is encrypted, and will not be displayed in contact list and call history"))
                        .margin(1, 3, 0, 49)
                        .build());

        section.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, true)
                        .label(createMap("Schedule privacy"))
                        .description(createMap("Hide details when reminding of a schedule"))
                        .margin(1, 3, 0, 49)
                        .build());

        section.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, true)
                        .label(createMap("Whereabouts privacy"))
                        .description(createMap(
                                "Navigation footprints, favorites and search history disabled, and group location sharing not allowed on IVI"))
                        .margin(1, 3, 0, 49)
                        .build());

        section.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, true)
                        .label(createMap("Disable interior camera"))
                        .margin(1, 3, 0, 42)
                        .build());

        panel.addView(section);

        return panel;
    }

    private Panel createSystemBcrPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(theme);

        Section section1 = new Section(this);
        section1.setTitle(createMap("Factory reset"));
        section1.setTheme(theme);
        section1.setLanguage(LANGUAGE);

        section1.addButton(
                Button.create(this, theme, LANGUAGE, "Factory reset", ButtonStyle.SECONDARY)
                        .padding(45, 1, 44, 0)
                        .margin(0, 0, 0, 42)
                        .build());
        panel.addView(section1);

        Section section2 = new Section(this);
        section2.setTitle(createMap("Wireless Charging For Phone"));
        section2.setTheme(theme);
        section2.setLanguage(LANGUAGE);

        section2.addCheckbox(
                Checkbox.create(this, theme, LANGUAGE, true)
                        .label(createMap("Wireless Charging For Mobile Devices"))
                        .build());
        panel.addView(section2);

        Section section3 = new Section(this);
        section3.setTitle(createMap("Language"));
        section3.setTheme(theme);
        section3.setLanguage(LANGUAGE);

        List<RadioButton> radioButtons = new ArrayList<>();
        radioButtons.add(new RadioButton("zh", createMap("Chinese (Simplified)")));
        radioButtons.add(new RadioButton("en", createMap("English")));

        section3.addRadio(
                Radio.create(this, theme, LANGUAGE, radioButtons, "en").build());
        panel.addView(section3);

        return panel;
    }

    private Panel createDrivePrefPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(theme);
        panel.setCompact(true);

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
                        .build());

        section1.addButton(
                Button.create(this, theme, LANGUAGE, "Settings", ButtonStyle.SECONDARY)
                        .boldText(true)
                        .padding(49, 0, 49, 0)
                        .description(createMap(
                                "Press Settings to define your drive\nmode."))
                        .build());

        panel.addView(section1);

        driveSection2 = new Section(this);
        driveSection2.setTitle(createMap("Power mode"));
        driveSection2.setTheme(theme);
        driveSection2.setLanguage(LANGUAGE);
        driveSection2.setCompactWidth(true);
        List<Section.PopupBlock> popupBlocks = new ArrayList<>();
        popupBlocks.add(new Section.PopupBlock(
                createMap("Electric mode first"),
                createMap("Generally, the range extender is not started. When the battery is high, the Pure EV mode is used. When the battery is low, the energy mode is HEV, and the battery is recharged at the same time. In this mode, the battery power will be kept at a low level.")));
        popupBlocks.add(new Section.PopupBlock(
                createMap("Forced EV"),
                createMap("Running in Pure EV mode, able to use more power to the maximum extent, only triggering the range extender to start when the battery is extremely low. Recommended for use when approaching an energy replenishment station, so that the vehicle can travel the maximum mileage in Pure EV mode.")));
        driveSection2.setPopupText(popupBlocks);

        List<RadioButton> powerModeButtons = new ArrayList<>();
        powerModeButtons.add(new RadioButton("electric", createMap("Electric mode")));
        powerModeButtons.add(new RadioButton("hybrid", createMap("Hybrid")));
        powerModeButtons.add(new RadioButton("save", createMap("Save Mode")));

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

        driveSection2.addRadio(
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
                        .build());

        driveSection2.addCheckbox(forcedEv);
        driveSection2.addRadio(chargeRadio);

        panel.addView(driveSection2);

        return panel;
    }

    private void loadCompactImage() {
        if (drivePanel == null) {
            return;
        }
        String imageName = "drive_mode_individal_97c_80.png";
        String[] paths = {
                "java/ru/voboost/components/demo/pixel/MainActivity.screenshots/" + imageName,
                "src/demo-pixel/java/ru/voboost/components/demo/pixel/MainActivity.screenshots/" + imageName
        };

        for (String path : paths) {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    drivePanel.setImage(bitmap);
                    return;
                }
            }
        }

        try {
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(imageName);
            if (is != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                if (bitmap != null) {
                    drivePanel.setImage(bitmap);
                    is.close();
                    return;
                }
                is.close();
            }
        } catch (Exception e) {
            // Ignore - compact image is optional for rendering
        }
    }

    private Map<String, String> createMap(String text) {
        Map<String, String> map = new HashMap<>();
        map.put("en", text);
        map.put("ru", text);
        return map;
    }

    /** Returns the Screen component (for test access). */
    public Screen getScreen() {
        return screen;
    }

    /** Returns the Power mode section of the drive panel (for popup tests). */
    public Section getSection2() {
        return driveSection2;
    }
}

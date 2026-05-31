package ru.voboost.components.demo.pixel;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.voboost.components.button.Button;
import ru.voboost.components.button.ButtonStyle;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.screen.Screen;
import ru.voboost.components.section.Section;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;

public class Button01 extends Activity {

    private static final String TAG = "Button01";
    private static final Theme THEME = Theme.FREE_DARK;
    private static final Language LANGUAGE = Language.EN;

    private Screen screen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreenMode();
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
            // Ignore under Robolectric
        }
    }

    private void setupComponentHierarchy() {
        screen = Screen.create(this, THEME)
            .backgroundColorHex("#000000")
            .tabs(createTabs())
            .panels(createAllPanels())
            .build();
        setContentView(screen);

        screen.getTabs().setSelectedValue("system", false);
    }

    private Tabs createTabs() {
        List<TabItem> items = new ArrayList<>();
        items.add(TabItem.create("reminder", createMap("Reminder")).build());
        items.add(TabItem.create("device", createMap("Device")).build());
        items.add(TabItem.create("privacy", createMap("Privacy")).build());
        items.add(TabItem.create("system", createMap("System")).selected(true).build());
        items.add(TabItem.create("vehicle", createMap("Vehicle")).marginTop(30).more(true).build());

        return Tabs.create(this, THEME, LANGUAGE, items).build();
    }

    private Map<String, String> createMap(String text) {
        Map<String, String> map = new HashMap<>();
        map.put("en", text);
        map.put("ru", text);
        return map;
    }

    private Panel[] createAllPanels() {
        return new Panel[] {
            createEmptyPanel(),
            createEmptyPanel(),
            createEmptyPanel(),
            createSystemPanel(),
            createEmptyPanel()
        };
    }

    private Panel createEmptyPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(THEME);
        return panel;
    }

    private Panel createSystemPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(THEME);

        Section section1 = Section.create(this, THEME, LANGUAGE, createMap("Factory reset"))
            .paddingTop(34)
            .paddingBottom(35)
            .build();
        section1.addButton(
            Button.create(this, THEME, LANGUAGE, "Factory reset", ButtonStyle.SECONDARY)
                .margin(0, 0, 0, 0)
                .padding(45, 1, 44, 0)
                .build()
        );
        panel.addView(section1);

        Section section2 = Section.create(this, THEME, LANGUAGE,
                createMap("Privacy Agreement and Service Agreement"))
            .paddingTop(30)
            .paddingBottom(30)
            .build();
        section2.addButton(
            Button.create(this, THEME, LANGUAGE, "Check", ButtonStyle.SECONDARY)
                .margin(0, 0, 0, 0)
                .padding(37, 1, 36, 0)
                .build()
        );
        panel.addView(section2);

        Section section3 = Section.create(this, THEME, LANGUAGE,
                createMap("Statement on Use of Open Source Code"))
            .paddingTop(30)
            .paddingBottom(30)
            .build();
        section3.addButton(
            Button.create(this, THEME, LANGUAGE, "Check", ButtonStyle.SECONDARY)
                .margin(0, 0, 0, 0)
                .padding(37, 1, 36, 0)
                .build()
        );
        panel.addView(section3);

        return panel;
    }

    public Screen getScreen() {
        return screen;
    }
}

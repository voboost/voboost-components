package ru.voboost.components.demo.java.cunba;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ru.voboost.components.button.Button;
import ru.voboost.components.button.ButtonStyle;
import ru.voboost.components.buttons.Buttons;
import ru.voboost.components.buttons.ButtonConfig;
import ru.voboost.components.checkbox.Checkbox;
import ru.voboost.components.demo.shared.DemoHelpers;
import ru.voboost.components.demo.shared.DemoState;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.radio.Radio;
import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.screen.Screen;
import ru.voboost.components.section.Section;
import ru.voboost.components.select.Select;
import ru.voboost.components.select.SelectOption;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;

/**
 * Demo Activity for CunBA 3 interface.
 *
 * <p>Reproduces the CunBA 3.0.0 HTML prototype using voboost-components.
 * Five tabs: Launcher, Applications, Interface, Vehicle, Settings.
 * All content is inline.
 */
public class MainActivity extends Activity {

    private static final String TAG = "CunbaDemo";

    private static final List<String> TAB_VALUES =
            Arrays.asList("launcher", "applications", "interface", "vehicle", "settings");

    private static final String DEFAULT_LANGUAGE = "ru";
    private static final String DEFAULT_THEME = "dark";

    private Screen screen;
    private DemoState demoState;

    // Settings radios for state application in tests
    private Radio languageRadio;
    private Radio themeRadio;
    private Radio carTypeRadio;

    // ============================================================
    // Helpers
    // ============================================================

    private static Map<String, String> mapOf(String... keyValuePairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            map.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return map;
    }

    private SelectOption opt(String value, String en, String ru) {
        return new SelectOption(value, mapOf("en", en, "ru", ru));
    }

    private String getMappedCombinedTheme() {
        String theme = demoState.getCurrentTheme();
        String mapped = "auto".equals(theme) ? "dark" : theme;
        return demoState.getCurrentCarType() + "-" + mapped;
    }

    private Theme currentTheme() {
        return Theme.fromValue(getMappedCombinedTheme());
    }

    private Language currentLanguage() {
        return Language.fromCode(demoState.getCurrentLanguage());
    }

    // ============================================================
    // Lifecycle
    // ============================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        demoState = new DemoState();
        demoState.setCurrentLanguage(DEFAULT_LANGUAGE);
        demoState.setCurrentTheme(DEFAULT_THEME);

        setupFullScreenMode();
        setupComponentHierarchy();
        updateAllComponents();
    }

    private void setupFullScreenMode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController controller = getWindow().getInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.systemBars());
                    controller.setSystemBarsBehavior(
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            } else {
                View decorView = getWindow().getDecorView();
                if (decorView != null) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (NullPointerException e) {
            Log.d(TAG, "Ignoring NullPointerException in setupFullScreenMode (Robolectric)");
        }
    }

    private void setupComponentHierarchy() {
        Theme theme = currentTheme();
        Language language = currentLanguage();

        List<TabItem> tabItems = getTabItems();
        Panel[] panels = createAllPanels();

        Tabs tabs = Tabs.create(this, theme, language, tabItems).build();

        screen = Screen.create(this, theme)
                .tabs(tabs)
                .panels(panels)
                .build();
        setContentView(screen);

        tabs.setSelectedValue(demoState.getSelectedTab(), false);
        tabs.setOnValueChangeListener(
                selectedTab -> {
                    demoState.setSelectedTab(selectedTab);
                    updateAllComponents();
                });
    }

    private Panel[] createAllPanels() {
        Panel[] panels = new Panel[TAB_VALUES.size()];
        for (int i = 0; i < TAB_VALUES.size(); i++) {
            panels[i] = createPanelForTab(TAB_VALUES.get(i));
        }
        return panels;
    }

    private Panel createPanelForTab(String tabValue) {
        switch (tabValue) {
            case "launcher":
                return createLauncherPanel();
            case "applications":
                return createApplicationsPanel();
            case "interface":
                return createInterfacePanel();
            case "vehicle":
                return createVehiclePanel();
            case "settings":
                return createSettingsPanel();
            default:
                throw new IllegalStateException("Unknown tab: " + tabValue);
        }
    }

    private void updateAllComponents() {
        String combinedTheme = getMappedCombinedTheme();
        Language language = Language.fromCode(demoState.getCurrentLanguage());

        if (screen != null) {
            screen.setBackgroundColor(DemoHelpers.getBackgroundColor(combinedTheme));
            screen.setTheme(Theme.fromValue(combinedTheme));
            screen.setLanguage(language);
        }
    }

    /**
     * Applies a full settings state. Used by visual tests.
     */
    public void applyState(String language, String theme, String carType) {
        demoState.setCurrentLanguage(language);
        demoState.setCurrentTheme(theme);
        demoState.setCurrentCarType(carType);
        if (languageRadio != null) {
            languageRadio.setSelectedValue(language);
        }
        if (themeRadio != null) {
            themeRadio.setSelectedValue(theme);
        }
        if (carTypeRadio != null) {
            carTypeRadio.setSelectedValue(carType);
        }
        updateAllComponents();
    }

    // Getters for testing
    public Tabs getTabs() {
        return screen != null ? screen.getTabs() : null;
    }

    public Screen getScreen() {
        return screen;
    }

    public DemoState getDemoState() {
        return demoState;
    }

    public Panel getPanel() {
        Panel[] panels = screen != null ? screen.getPanels() : null;
        int tabIndex = TAB_VALUES.indexOf(demoState.getSelectedTab());
        return (panels != null && tabIndex >= 0 && tabIndex < panels.length) ? panels[tabIndex] : null;
    }

    // ============================================================
    // Tab items
    // ============================================================

    private List<TabItem> getTabItems() {
        List<TabItem> items = new ArrayList<>();
        items.add(TabItem.create("launcher", mapOf("en", "Launcher", "ru", "Лаунчер")).build());
        items.add(TabItem.create("applications", mapOf("en", "Applications", "ru", "Приложения")).build());
        items.add(TabItem.create("interface", mapOf("en", "Interface", "ru", "Интерфейс")).build());
        items.add(TabItem.create("vehicle", mapOf("en", "Vehicle", "ru", "Машина")).build());
        items.add(TabItem.create("settings", mapOf("en", "Settings", "ru", "Настройки")).build());
        return items;
    }

    // ============================================================
    // Panel: Settings
    // ============================================================

    private Panel createSettingsPanel() {
        Theme theme = currentTheme();
        Language language = currentLanguage();

        Panel panel = new Panel(this);
        panel.setTheme(theme);

        Section settingsSection = Section.create(this, theme, language,
                mapOf("en", "CunBA 3.0.0.2025012501, trial period until February 1, 2025 11:30",
                        "ru", "CunBA 3.0.0.2025012501, пробный период до 01 февраля 2025 11:30")).build();
        panel.addView(settingsSection);

        languageRadio = settingsSection.addRadio(
                Radio.create(this, theme, language,
                        Arrays.asList(
                                new RadioButton("ru", mapOf("en", "Russian", "ru", "Русский")),
                                new RadioButton("en", mapOf("en", "English", "ru", "English"))),
                        demoState.getCurrentLanguage())
                        .title(mapOf("en", "Language", "ru", "Язык"))
                        .onValueChange(newValue -> {
                            demoState.setCurrentLanguage(newValue);
                            updateAllComponents();
                        })
                        .build());

        themeRadio = settingsSection.addRadio(
                Radio.create(this, theme, language,
                        Arrays.asList(
                                new RadioButton("auto", mapOf("en", "Auto", "ru", "Авто")),
                                new RadioButton("dark", mapOf("en", "Dark", "ru", "Тёмная")),
                                new RadioButton("light", mapOf("en", "Light", "ru", "Светлая"))),
                        demoState.getCurrentTheme())
                        .title(mapOf("en", "Appearance", "ru", "Внешний вид"))
                        .onValueChange(newValue -> {
                            demoState.setCurrentTheme(newValue);
                            updateAllComponents();
                        })
                        .build());

        carTypeRadio = settingsSection.addRadio(
                Radio.create(this, theme, language,
                        Arrays.asList(
                                new RadioButton("free", mapOf("en", "Free", "ru", "Free")),
                                new RadioButton("dreamer", mapOf("en", "Dreamer", "ru", "Dreamer"))),
                        demoState.getCurrentCarType())
                        .title(mapOf("en", "Car Model", "ru", "Модель автомобиля"))
                        .onValueChange(newValue -> {
                            demoState.setCurrentCarType(newValue);
                            updateAllComponents();
                        })
                        .build());

        settingsSection.addButtons(
                Buttons.create(this, theme, language,
                        Arrays.asList(
                                new ButtonConfig("update_cunba", "Обновить CunBA"),
                                new ButtonConfig("update_components", "Обновить компоненты")),
                        "update_cunba")
                        .title(mapOf("en", "Update", "ru", "Обновление"))
                        .build());

        panel.addView(createActivationSection(theme, language));
        return panel;
    }

    // ============================================================
    // Activation section (custom Android Views, 3 horizontal columns)
    // ============================================================

    private Section createActivationSection(Theme theme, Language language) {
        Section section = Section.create(this, theme, language,
                mapOf("en", "Activation", "ru", "Активация")).build();
        section.setPaddingTop(30);
        section.setPaddingBottom(30);

        LinearLayout activation = new LinearLayout(this);
        activation.setOrientation(LinearLayout.HORIZONTAL);

        // Column 1: Photograph QR with VIN
        LinearLayout col1 = new LinearLayout(this);
        col1.setOrientation(LinearLayout.VERTICAL);
        col1.addView(titleView(mapOf("en", "Photograph QR code with VIN", "ru", "Сфотографируйте QR-код с VIN")));
        col1.addView(qrImageView(R.drawable.activation_qr1));
        col1.addView(hintView(mapOf("en", "LDP95H966PE302009", "ru", "LDP95H966PE302009")));

        // Column 2: Choose subscription (.activation__step_price width:100% -> fills remaining)
        LinearLayout col2 = new LinearLayout(this);
        col2.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams col2Params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        col2Params.leftMargin = 80;
        col2.addView(titleView(mapOf("en", "Choose subscription", "ru", "Выберите подписку")));
        col2.addView(priceTitleView(mapOf("en", "10,000 rubles per year", "ru", "10 000 рублей в год")));
        col2.addView(priceHintView(mapOf("en", "Annual subscription, full functionality without localization.",
                "ru", "Подписка на год, доступна полностью вся функциональность без русификации.")));
        col2.addView(priceTitleView(mapOf("en", "25,000 rubles, forever", "ru", "25 000 рублей, бессрочно")));
        col2.addView(priceHintView(mapOf("en", "Full functionality\nwithout localization.",
                "ru", "Доступна полностью вся функциональность\nбез русификации.")));
        col2.addView(priceTitleView(mapOf("en", "50,000 rubles, forever", "ru", "50 000 рублей, бессрочно")));
        col2.addView(priceHintView(mapOf("en", "Full functionality\nwith localization.",
                "ru", "Доступна полностью вся функциональность\nс русификацией.")));

        // Column 3: Send photo to @cunba_ru
        LinearLayout col3 = new LinearLayout(this);
        col3.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams col3Params = new LinearLayout.LayoutParams(
                300, LinearLayout.LayoutParams.WRAP_CONTENT);
        col3Params.leftMargin = 80;
        col3.addView(titleView(mapOf("en", "Send photo to @cunba_ru", "ru", "Отправьте фото @cunba_ru")));
        col3.addView(qrImageView(R.drawable.activation_qr2));
        col3.addView(hintView(mapOf("en", "Pay for activation\nafter sending QR",
                "ru", "Оплатите активацию\nпосле отправки QR")));

        activation.addView(col1, new LinearLayout.LayoutParams(
                300, LinearLayout.LayoutParams.WRAP_CONTENT));
        activation.addView(col2, col2Params);
        activation.addView(col3, col3Params);

        android.view.ViewGroup.MarginLayoutParams activationParams =
                new android.view.ViewGroup.MarginLayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        activationParams.bottomMargin = 0;
        section.addView(activation, activationParams);
        return section;
    }

    private LocalizableTextView titleView(Map<String, String> text) {
        LocalizableTextView tv = new LocalizableTextView(this, 28f, LocalizableTextView.ROLE_TITLE);
        tv.setData(text);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return tv;
    }

    private LocalizableTextView hintView(Map<String, String> text) {
        LocalizableTextView tv = new LocalizableTextView(this, 24f, LocalizableTextView.ROLE_HINT);
        tv.setData(text);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return tv;
    }

    private LocalizableTextView priceTitleView(Map<String, String> text) {
        LocalizableTextView tv = new LocalizableTextView(this, 28f, LocalizableTextView.ROLE_TITLE);
        tv.setData(text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 28;
        tv.setLayoutParams(params);
        return tv;
    }

    private LocalizableTextView priceHintView(Map<String, String> text) {
        LocalizableTextView tv = new LocalizableTextView(this, 24f, LocalizableTextView.ROLE_HINT);
        tv.setData(text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 20;
        tv.setLayoutParams(params);
        return tv;
    }

    private ImageView qrImageView(int resId) {
        ImageView iv = new ImageView(this);
        iv.setImageResource(resId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(240, 240);
        params.topMargin = 45;
        params.bottomMargin = 45;
        iv.setLayoutParams(params);
        return iv;
    }

    // ============================================================
    // Panel: Launcher
    // ============================================================

    private Panel createLauncherPanel() {
        Theme theme = currentTheme();
        Language language = currentLanguage();

        Panel panel = new Panel(this);
        panel.setTheme(theme);

        // Section: Stop Applications
        Section stopSection = Section.create(this, theme, language,
                mapOf("en", "Stop Applications", "ru", "Остановка приложений")).build();
        panel.addView(stopSection);

        stopSection.addButton(
                Button.create(this, theme, language, "Остановить приложения", ButtonStyle.SECONDARY)
                        .description(mapOf(
                                "en", "Stop all running applications to free memory.",
                                "ru", "Остановить все запущенные приложения для освобождения памяти."))
                        .build());

        // Section: Application Scaling
        Section scaleSection = Section.create(this, theme, language,
                mapOf("en", "Application Scaling", "ru", "Масштаб приложений")).build();
        panel.addView(scaleSection);

        scaleSection.addRadio(
                Radio.create(this, theme, language,
                        Arrays.asList(
                                new RadioButton("default", mapOf("en", "Default", "ru", "По умолчанию")),
                                new RadioButton("medium", mapOf("en", "Medium", "ru", "Средний")),
                                new RadioButton("large", mapOf("en", "Large", "ru", "Крупный")),
                                new RadioButton("maximum", mapOf("en", "Maximum", "ru", "Максимальный"))),
                        "default")
                        .descriptionAbove(mapOf(
                                "en", "Change scaling for all applications. It can be overridden per application in the launcher by long-pressing the application and choosing a different scale.",
                                "ru", "Изменить масштаб для всех приложений. Можно переопределить его для конкретного приложения\nв лаунчере, сделав долгое нажатие на приложении и выбрав другой масштаб."))
                        .build());

        // Section: Third-party Applications in Launcher
        Section thirdPartySection = Section.create(this, theme, language,
                mapOf("en", "Third-party Applications in Launcher", "ru", "Сторонние приложения в лаунчере")).build();
        panel.addView(thirdPartySection);

        thirdPartySection.addCheckbox(
                Checkbox.create(this, theme, language, false)
                        .label(mapOf("en", "Yandex Keyboard", "ru", "Яндекс Клавиатура"))
                        .build());
        thirdPartySection.addCheckbox(
                Checkbox.create(this, theme, language, true)
                        .label(mapOf("en", "Yandex Music", "ru", "Яндекс Музыка"))
                        .build());
        thirdPartySection.addCheckbox(
                Checkbox.create(this, theme, language, true)
                        .label(mapOf("en", "Yandex Navigator", "ru", "Яндекс Навигатор"))
                        .build());

        // Section: Original Applications in Launcher
        Section originalSection = Section.create(this, theme, language,
                mapOf("en", "Original Applications in Launcher", "ru", "Оригинальные приложения в лаунчере")).build();
        panel.addView(originalSection);

        originalSection.addCheckbox(
                Checkbox.create(this, theme, language, false)
                        .label(mapOf("en", "Android Settings", "ru", "Настройки Андроида"))
                        .build());
        originalSection.addCheckbox(
                Checkbox.create(this, theme, language, true)
                        .label(mapOf("en", "DVR", "ru", "Регистратор (DVR)"))
                        .build());
        originalSection.addCheckbox(
                Checkbox.create(this, theme, language, false)
                        .label(mapOf("en", "User Center", "ru", "Центр пользователя (User center)"))
                        .build());

        return panel;
    }

    // ============================================================
    // Panel: Applications
    // ============================================================

    private Panel createApplicationsPanel() {
        Theme theme = currentTheme();
        Language language = currentLanguage();

        Panel panel = new Panel(this);
        panel.setTheme(theme);

        List<SelectOption> centralApps = Arrays.asList(
                opt("yandex_navigator", "Yandex Navigator", "Яндекс Навигатор"),
                opt("original", "Original App", "Оригинальное приложение"),
                opt("none", "No App Selected", "Приложение не выбрано"));

        // Section: Central Screen
        Section centralSection = Section.create(this, theme, language,
                mapOf("en", "Central Screen", "ru", "Центральный экран")).build();
        panel.addView(centralSection);

        addAppSelect(centralSection, theme, language, centralApps, "yandex_navigator",
                mapOf("en", "Navigation, override left panel button.\nTap and select an application to launch.",
                        "ru", "Навигация, переопределить кнопку на левой панели.\nНажмите и выберите приложение для запуска."));
        addAppSelect(centralSection, theme, language, centralApps, "original",
                mapOf("en", "Music, override left panel button.\nTap and select an application to launch.",
                        "ru", "Музыка, переопределить кнопку на левой панели.\nНажмите и выберите приложение для запуска."));
        addAppSelect(centralSection, theme, language, centralApps, "none",
                mapOf("en", "Auto-start application on central screen.\nTap and select an application to launch.",
                        "ru", "Автоматический запуск приложения на центральном экране.\nНажмите и выберите приложение для запуска."));

        // Section: Passenger Screen
        Section passengerSection = Section.create(this, theme, language,
                mapOf("en", "Passenger Screen", "ru", "Экран пассажира")).build();
        panel.addView(passengerSection);

        List<SelectOption> passengerApps = Arrays.asList(
                opt("fmplay", "FMPlay", "FMPlay"),
                opt("rutube", "RUTUBE", "RUTUBE"),
                opt("efir", "Efir TV", "Эфир ТВ"),
                opt("kinopoisk", "KinoPoisk", "КиноПоиск"),
                opt("youtube", "YouTube", "YouTube"),
                opt("none", "No App Selected", "Приложение не выбрано"));
        String[][] passengerDescs = {
                {"Override first button on left panel.\nTap and select an application to launch.",
                        "Переопределить первую кнопку на левой панели.\nНажмите и выберите приложение для запуска."},
                {"Override second button on left panel.\nTap and select an application to launch.",
                        "Переопределить вторую кнопку на левой панели.\nНажмите и выберите приложение для запуска."},
                {"Override third button on left panel.\nTap and select an application to launch.",
                        "Переопределить третью кнопку на левой панели.\nНажмите и выберите приложение для запуска."},
                {"Override fourth button on left panel.\nTap and select an application to launch.",
                        "Переопределить четвёртую кнопку на левой панели.\nНажмите и выберите приложение для запуска."},
                {"Override fifth button on left panel.\nTap and select an application to launch.",
                        "Переопределить пятую кнопку на левой панели.\nНажмите и выберите приложение для запуска."}
        };
        String[] passengerKeys = {"fmplay", "rutube", "efir", "kinopoisk", "youtube"};
        for (int i = 0; i < passengerKeys.length; i++) {
            addAppSelect(passengerSection, theme, language, passengerApps, passengerKeys[i],
                    mapOf("en", passengerDescs[i][0], "ru", passengerDescs[i][1]));
        }
        addAppSelect(passengerSection, theme, language, passengerApps, "none",
                mapOf("en", "Auto-start application on passenger screen.\nTap and select an application to launch.",
                        "ru", "Автоматический запуск приложения на экране пассажира.\nНажмите и выберите приложение для запуска."));

        // Section: Disable Original Applications
        Section disableSection = Section.create(this, theme, language,
                mapOf("en", "Disable Original Applications", "ru", "Отключение оригинальных приложений")).build();
        panel.addView(disableSection);

        String[][] disableApps = {
                {"Voice Control", "Голосовое управление"},
                {"Video", "Видео"},
                {"Radio", "Радио"},
                {"Navigator", "Навигатор"},
                {"User Center", "Центр пользователя"},
                {"Other Applications", "Другие приложения"}
        };
        for (String[] app : disableApps) {
            disableSection.addCheckbox(
                    Checkbox.create(this, theme, language, false)
                            .label(mapOf("en", app[0], "ru", app[1]))
                            .build());
        }

        return panel;
    }

    private void addAppSelect(Section section, Theme theme, Language language,
            List<SelectOption> options, String selectedValue, Map<String, String> description) {
        section.addSelect(Select.create(this, theme, language, options, selectedValue)
                .description(description)
                .build());
    }

    // ============================================================
    // Panel: Interface
    // ============================================================

    private Panel createInterfacePanel() {
        Theme theme = currentTheme();
        Language language = currentLanguage();

        Panel panel = new Panel(this);
        panel.setTheme(theme);

        List<SelectOption> keyboardApps = Arrays.asList(
                opt("yandex_keyboard", "Yandex Keyboard", "Яндекс Клавиатура"),
                opt("original_keyboard", "Original Keyboard", "Оригинальная клавиатура"),
                opt("none", "No App Selected", "Приложение не выбрано"));

        // Section: Language
        Section langSection = Section.create(this, theme, language,
                mapOf("en", "Language", "ru", "Язык")).build();
        panel.addView(langSection);

        langSection.addCheckbox(
                Checkbox.create(this, theme, language, false)
                        .label(mapOf("en", "Car system localization to Russian", "ru", "Русификация системы автомобиля"))
                        .description(mapOf(
                                "en", "Translates the car system to Russian.\nAfter enabling this option, Chinese language must be enabled in settings.",
                                "ru", "Переводит на русский язык систему автомобиля.\nПосле включения этой опции надо включить китайский язык в настройках."))
                        .build());
        langSection.addCheckbox(
                Checkbox.create(this, theme, language, false)
                        .label(mapOf("en", "Remove hieroglyphs from car name", "ru", "Убрать иероглифы из имени автомобиля"))
                        .description(mapOf(
                                "en", "Removes hieroglyphs from the car name used in Bluetooth and AP Host.",
                                "ru", "Убирает иероглифы из имени автомобиля, которое используется в Bluetooth и AP Host."))
                        .build());
        langSection.addRadio(
                Radio.create(this, theme, language,
                        Arrays.asList(
                                new RadioButton("original", mapOf("en", "Original", "ru", "Оригинальный")),
                                new RadioButton("voyah_tweaks", mapOf("en", "VoyahTweaks", "ru", "VoyahTweaks")),
                                new RadioButton("alice", mapOf("en", "Yandex Alice", "ru", "Яндекс Алиса"))),
                        "original")
                        .title(mapOf("en", "Voice Assistant", "ru", "Голосовой помощник"))
                        .build());

        // Section: Central Screen
        Section centralSection = Section.create(this, theme, language,
                mapOf("en", "Central Screen", "ru", "Центральный экран")).build();
        panel.addView(centralSection);

        addAppSelect(centralSection, theme, language, keyboardApps, "yandex_keyboard",
                mapOf("en", "Override keyboard on central screen.\nTap and select a keyboard application.",
                        "ru", "Переопределить клавиатуру на центральном экране.\nНажмите и выберите приложение клавиатуры."));

        centralSection.addCheckbox(
                Checkbox.create(this, theme, language, true)
                        .label(mapOf("en", "Show Arrow", "ru", "Показать стрелку"))
                        .description(mapOf(
                                "en", "Tapping the arrow hides the side panel, long press opens CunBA settings.",
                                "ru", "Нажатие на стрелку скрывает боковую панель, долгое нажатие открывает настройки CunBA."))
                        .build());
        centralSection.addCheckbox(
                Checkbox.create(this, theme, language, true)
                        .label(mapOf("en", "Long press climate opens driving settings",
                                "ru", "Долгое нажатие на кнопку климата открывает настройки движения"))
                        .description(mapOf(
                                "en", "Open driving settings on long press of the climate button on the left panel.",
                                "ru", "Открывать настройки движения при длинном нажатии на кнопку климата на левой панели."))
                        .build());
        centralSection.addCheckbox(
                Checkbox.create(this, theme, language, true)
                        .label(mapOf("en", "Press Navigator moves it to dashboard",
                                "ru", "Нажатие на кнопку Навигатора переносит его на приборную панель"))
                        .description(mapOf(
                                "en", "1. Launch the original Navigator application once, agree to all terms.\n2. Enable the dashboard variant with fullscreen navigator display mode: Settings -> Display -> FS NAV.\n3. Enable fullscreen navigator display mode on the dashboard by pressing the \"two rectangles\" button on the steering wheel several times.\n4. Launch the navigator you selected on the left panel of the central screen.\n5. Press and hold the navigator button on the left panel, it will move to the dashboard.",
                                "ru", "1. Запустите один раз оригинальное приложение Навигатора, согласитесь со всеми пунктами.\n2. Включите вариант приборной панели с режимом отображения полноэкранного навигатора: Настройки -> Отображение -> FS NAV.\n3. Включите на приборной панели режим отображения полноэкранного навигатора, нажимая на руле несколько раз кнопку «два прямоугольника».\n4. Запустите навигатор, который вы выбрали на левой панели центрального экрана.\n5. Нажмите и удерживайте кнопку навигатора на левой панели, он перебросится на приборную панель."))
                        .build());

        centralSection.addRadio(
                Radio.create(this, theme, language,
                        Arrays.asList(
                                new RadioButton("cunba_original", mapOf("en", "CunBA / Original", "ru", "CunBA / Оригинальный")),
                                new RadioButton("original_cunba", mapOf("en", "Original / CunBA", "ru", "Оригинальный / CunBA"))),
                        "cunba_original")
                        .title(mapOf("en", "Launcher", "ru", "Лаунчер"))
                        .descriptionAbove(mapOf(
                                "en", "Normal and long press on the launcher button in the left panel.",
                                "ru", "Обычное и долгое нажатие на кнопку лаунчера в левой панели."))
                        .build());

        // Section: Passenger Screen
        Section passengerSection = Section.create(this, theme, language,
                mapOf("en", "Passenger Screen", "ru", "Экран пассажира")).build();
        panel.addView(passengerSection);

        addAppSelect(passengerSection, theme, language, keyboardApps, "original_keyboard",
                mapOf("en", "Override keyboard on passenger screen.\nTap and select a keyboard application.",
                        "ru", "Переопределить клавиатуру на экране пассажира.\nНажмите и выберите приложение клавиатуры."));

        passengerSection.addRadio(
                Radio.create(this, theme, language,
                        Arrays.asList(
                                new RadioButton("cunba_original", mapOf("en", "CunBA / Original", "ru", "CunBA / Оригинальный")),
                                new RadioButton("original_cunba", mapOf("en", "Original / CunBA", "ru", "Оригинальный / CunBA"))),
                        "original_cunba")
                        .title(mapOf("en", "Launcher", "ru", "Лаунчер"))
                        .descriptionAbove(mapOf(
                                "en", "Normal and long press on the launcher button in the left panel.",
                                "ru", "Обычное и долгое нажатие на кнопку лаунчера в левой панели."))
                        .build());

        // Section: Home Button
        Section homeSection = Section.create(this, theme, language,
                mapOf("en", "Home Button", "ru", "Домик")).build();
        panel.addView(homeSection);

        homeSection.addRadio(
                Radio.create(this, theme, language,
                        Arrays.asList(
                                new RadioButton("home", mapOf("en", "Home", "ru", "Домой")),
                                new RadioButton("back", mapOf("en", "Back", "ru", "Назад")),
                                new RadioButton("stop", mapOf("en", "Stop App", "ru", "Остановить приложение"))),
                        "back")
                        .title(mapOf("en", "Press Home Button in Left Panel",
                                "ru", "Нажатие на кнопку Домик в левой панели"))
                        .build());
        homeSection.addRadio(
                Radio.create(this, theme, language,
                        Arrays.asList(
                                new RadioButton("home", mapOf("en", "Home", "ru", "Домой")),
                                new RadioButton("back", mapOf("en", "Back", "ru", "Назад")),
                                new RadioButton("stop", mapOf("en", "Stop App", "ru", "Остановить приложение"))),
                        "stop")
                        .title(mapOf("en", "Long Press Home Button in Left Panel",
                                "ru", "Долгое нажатие на кнопку Домик в левой панели"))
                        .build());

        homeSection.addSelect(Select.create(this, theme, language,
                Arrays.asList(
                        opt("original_widget", "Original Widget", "Оригинальный виджет"),
                        opt("yandex_navigator", "Yandex Navigator", "Яндекс Навигатор"),
                        opt("none", "No App Selected", "Приложение не выбрано")),
                "original_widget")
                .title(mapOf("en", "Navigation", "ru", "Навигация"))
                .description(mapOf("en", "Select an application for the navigation widget.",
                        "ru", "Выберите приложение для навигационного виджета."))
                .build());

        homeSection.addSelect(Select.create(this, theme, language,
                Arrays.asList(
                        opt("original", "Original", "Оригинальный"),
                        opt("yandex_weather", "Yandex Weather", "Яндекс Погода"),
                        opt("none", "No App Selected", "Приложение не выбрано")),
                "original")
                .title(mapOf("en", "Weather", "ru", "Погода"))
                .description(mapOf("en", "Select a data source for the weather.",
                        "ru", "Выберите источник данных для погоды."))
                .build());

        homeSection.addButton(
                Button.create(this, theme, language, "Обновить погоду", ButtonStyle.SECONDARY)
                        .description(mapOf("en", "Update the weather widget data on the home screen.",
                                "ru", "Обновить данные погодного виджета на домашнем экране."))
                        .build());

        // Section: Time Zone
        Section tzSection = Section.create(this, theme, language,
                mapOf("en", "Time Zone", "ru", "Часовой пояс")).build();
        panel.addView(tzSection);

        tzSection.addSelect(Select.create(this, theme, language,
                Arrays.asList(
                        opt("gmt+02", "(GMT+02) Kaliningrad", "(GMT+02) Калининград"),
                        opt("gmt+03", "(GMT+03) Moscow", "(GMT+03) Москва"),
                        opt("gmt+04", "(GMT+04) Samara", "(GMT+04) Самара"),
                        opt("gmt+05", "(GMT+05) Yekaterinburg", "(GMT+05) Екатеринбург"),
                        opt("gmt+07", "(GMT+07) Krasnoyarsk", "(GMT+07) Красноярск"),
                        opt("gmt+09", "Petropavlovsk-Kamchatsky (GMT+09)", "Петропавловск-Камчатский (GMT+09)")),
                "gmt+09")
                .descriptionAbove(mapOf(
                        "en", "Select your time zone.\nThe dashboard time will change after a reboot;\npress and hold the star and two-rectangles buttons on the steering wheel.",
                        "ru", "Выберите свой часовой пояс.\nНа приборной панели время изменится после перезагрузки,\nзажмите и держите на руле звёздочка и два прямоугольника."))
                .build());

        return panel;
    }

    // ============================================================
    // Panel: Vehicle
    // ============================================================

    private Panel createVehiclePanel() {
        Theme theme = currentTheme();
        Language language = currentLanguage();

        Panel panel = new Panel(this);
        panel.setTheme(theme);

        // Section: Seats and Mirrors
        Section seatsSection = Section.create(this, theme, language,
                mapOf("en", "Seats and Mirrors", "ru", "Сиденья и зеркала")).build();
        panel.addView(seatsSection);

        String[][] seatDescs = {
                {"Driving, driver seat and mirror positions.",
                        "Вождение, положение сиденья водителя и зеркал."},
                {"Rest, driver seat and mirror positions.",
                        "Отдых, положение сиденья водителя и зеркал."},
                {"Second driver, driver seat and mirror positions.",
                        "Второй водитель, положение сиденья водителя и зеркал."}
        };
        for (String[] desc : seatDescs) {
            seatsSection.addButtons(
                    Buttons.create(this, theme, language,
                            Arrays.asList(
                                    new ButtonConfig("restore", "Восстановить"),
                                    new ButtonConfig("save", "Сохранить")),
                            "restore")
                            .rightText(mapOf("en", desc[0], "ru", desc[1]))
                            .build());
        }

        // Section: Driving
        Section drivingSection = Section.create(this, theme, language,
                mapOf("en", "Driving", "ru", "Движение")).build();
        panel.addView(drivingSection);

        drivingSection.addCheckbox(
                Checkbox.create(this, theme, language, true)
                        .label(mapOf("en", "Save Driving Mode", "ru", "Сохранять режим вождения"))
                        .description(mapOf(
                                "en", "Restores the previous driving mode when the car starts.\nUsed only for the guest account.",
                                "ru", "Восстанавливает предыдущий режим вождения при включении автомобиля.\nИспользуется только для гостевого аккаунта."))
                        .build());
        drivingSection.addCheckbox(
                Checkbox.create(this, theme, language, true)
                        .label(mapOf("en", "Save Energy Mode", "ru", "Сохранять режим энергии"))
                        .description(mapOf(
                                "en", "Restores the previous energy mode when the car starts.\nUsed only for the guest account.",
                                "ru", "Восстанавливает предыдущий режим энергии при включении автомобиля.\nИспользуется только для гостевого аккаунта."))
                        .build());
        drivingSection.addCheckbox(
                Checkbox.create(this, theme, language, false)
                        .label(mapOf("en", "Forced EV", "ru", "Forced EV"))
                        .description(mapOf(
                                "en", "Used only in electric driving mode.\nEnables reduced ICE usage in hybrid mode (do not enable below 10% traction battery\ndischarge, do not enable during sharp acceleration).",
                                "ru", "Используется только при включённом режиме движения от электричества.\nВключает режим меньшего использования ДВС на гибриде (не включать до 10% разряда\nтяговой батареи, не включать при резких ускорениях)."))
                        .build());
        drivingSection.addCheckbox(
                Checkbox.create(this, theme, language, true)
                        .label(mapOf("en", "Disable Pedestrian Warning Sound", "ru", "Отключить звук предупреждения пешеходов"))
                        .description(mapOf(
                                "en", "Disables the \"flying saucer\" sound at low speed.\nA yellow icon appears in the top-left of the driver's cluster,\nindicating the sound is disabled.",
                                "ru", "Отключает звук «летающей тарелки» при движении на низкой скорости.\nНа приборке водителя появится жёлтый значок в левом верхнем углу,\nсигнализирующий о том, что звук отключен."))
                        .build());

        // Section: Other
        Section otherSection = Section.create(this, theme, language,
                mapOf("en", "Other", "ru", "Другое")).build();
        panel.addView(otherSection);

        otherSection.addCheckbox(
                Checkbox.create(this, theme, language, false)
                        .label(mapOf("en", "Disable Activation QR Code on Startup", "ru", "Отключить QR-код активации при запуске"))
                        .description(mapOf(
                                "en", "Used only for unactivated vehicles.\nDisables the QR code when the car starts.",
                                "ru", "Используется только для неактивированных машин.\nОтключает QR-код при запуске автомобиля."))
                        .build());
        otherSection.addCheckbox(
                Checkbox.create(this, theme, language, false)
                        .label(mapOf("en", "Enable OTA Firmware Update", "ru", "Включить систему обновления прошивки автомобиля (OTA, Over The Air)"))
                        .description(mapOf(
                                "en", "Enable this option to receive car updates over the internet.\nCunBA may be removed during the update; recovery requires a laptop.",
                                "ru", "Включите эту опцию, если хотите получать обновления автомобиля через интернет.\nПри обновлении CunBA может быть удалена, восстановление с использованием ноутбука."))
                        .build());

        return panel;
    }
}

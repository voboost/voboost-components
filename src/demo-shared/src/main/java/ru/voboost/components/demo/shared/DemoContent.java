package ru.voboost.components.demo.shared;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.select.SelectOption;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.theme.Theme;

/**
 * Shared content provider for demo applications.
 *
 * <p>This class provides pre-configured content for all demo applications:
 * - Tab items for the Tabs component
 * - Section titles for each tab
 * - Radio buttons for each tab's content
 * - Default values for each tab
 */
public class DemoContent {

    private DemoContent() {
        // Prevent instantiation
    }

    // ============================================================
    // Helper method for creating localized maps
    // ============================================================

    private static Map<String, String> mapOf(String... keyValuePairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            map.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return map;
    }

    // ============================================================
    // Tab items
    // ============================================================

    /**
     * Returns the list of TabItem objects for all 8 tabs.
     *
     * @return List of TabItem objects
     */
    public static List<TabItem> getTabItems() {
        List<TabItem> tabItems = new ArrayList<>();

        tabItems.add(new TabItem("settings", createSettingsTabLabel()));
        tabItems.add(new TabItem("button", createButtonTabLabel()));
        tabItems.add(new TabItem("buttons", createButtonsTabLabel()));
        tabItems.add(new TabItem("checkbox", createCheckboxTabLabel()));
        tabItems.add(new TabItem("radio", createRadioTabLabel()));
        tabItems.add(new TabItem("select", createSelectTabLabel()));
        tabItems.add(new TabItem("dialog", createDialogTabLabel()));
        tabItems.add(new TabItem("toast", createToastTabLabel()));

        return tabItems;
    }

    // Tab label creation methods
    private static Map<String, String> createSettingsTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Settings");
        label.put("ru", "Настройки");
        return label;
    }

    private static Map<String, String> createButtonTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Button");
        label.put("ru", "Кнопка");
        return label;
    }

    private static Map<String, String> createButtonsTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Buttons");
        label.put("ru", "Кнопки");
        return label;
    }

    private static Map<String, String> createCheckboxTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Checkbox");
        label.put("ru", "Переключатель");
        return label;
    }

    private static Map<String, String> createRadioTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Radio");
        label.put("ru", "Радио");
        return label;
    }

    private static Map<String, String> createSelectTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Select");
        label.put("ru", "Выбор");
        return label;
    }

    private static Map<String, String> createDialogTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Dialog");
        label.put("ru", "Диалог");
        return label;
    }

    private static Map<String, String> createToastTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Toast");
        label.put("ru", "Уведомление");
        return label;
    }

    // ============================================================
    // Section titles
    // ============================================================

    /**
     * Returns the section title for the specified tab.
     *
     * @param tabValue the tab value (e.g., "settings", "button")
     * @return Map of language code to title text
     */
    public static Map<String, String> getSectionTitle(String tabValue) {
        switch (tabValue) {
            case "settings":
                return createSettingsSectionTitle();
            case "button":
                return createButtonSectionTitle();
            case "buttons":
                return createButtonsSectionTitle();
            case "checkbox":
                return createCheckboxSectionTitle();
            case "radio":
                return createRadioSectionTitle();
            case "select":
                return createSelectSectionTitle();
            case "dialog":
                return createDialogSectionTitle();
            case "toast":
                return createToastSectionTitle();
            default:
                return createDefaultSectionTitle();
        }
    }

    private static Map<String, String> createSettingsSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");
        title.put("ru", "Настройки");
        return title;
    }

    private static Map<String, String> createButtonSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Button Component");
        title.put("ru", "Компонент Кнопка");
        return title;
    }

    private static Map<String, String> createButtonsSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Buttons Component");
        title.put("ru", "Компонент Кнопки");
        return title;
    }

    private static Map<String, String> createCheckboxSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Checkbox Component");
        title.put("ru", "Компонент Переключатель");
        return title;
    }

    private static Map<String, String> createRadioSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Radio Component");
        title.put("ru", "Компонент Радио");
        return title;
    }

    private static Map<String, String> createSelectSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Select Component");
        title.put("ru", "Компонент Выбор");
        return title;
    }

    private static Map<String, String> createDialogSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Dialog Component");
        title.put("ru", "Компонент Диалог");
        return title;
    }

    private static Map<String, String> createToastSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Toast Component");
        title.put("ru", "Компонент Уведомление");
        return title;
    }

    private static Map<String, String> createDefaultSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");
        title.put("ru", "Настройки");
        return title;
    }

    // ============================================================
    // Public getters for specific section titles (convenience methods)
    // ============================================================

    /**
     * Returns the section title for the Select tab.
     *
     * @return Map of language code to title text
     */
    public static Map<String, String> getSelectSectionTitle() {
        return getSectionTitle("select");
    }

    /**
     * Returns the section title for the Dialog tab.
     *
     * @return Map of language code to title text
     */
    public static Map<String, String> getDialogSectionTitle() {
        return getSectionTitle("dialog");
    }

    // ============================================================
    // Radio buttons for Settings tab (language/theme/car_type)
    // ============================================================

    /**
     * Returns the radio buttons for the specified settings sub-section.
     * Used for language, theme, and car_type within the Settings tab.
     *
     * @param tabValue the sub-section value (e.g., "language", "theme", "car_type")
     * @return List of RadioButton objects
     */
    public static List<RadioButton> getRadioButtons(String tabValue) {
        switch (tabValue) {
            case "language":
                return createLanguageRadioButtons();
            case "theme":
                return createThemeRadioButtons();
            case "car_type":
                return createCarTypeRadioButtons();
            default:
                return createDefaultRadioButtons();
        }
    }

    /**
     * Returns the default value for the specified tab.
     *
     * @param tabValue the tab value (e.g., "language", "theme")
     * @return the default value
     */
    public static String getDefaultValue(String tabValue) {
        switch (tabValue) {
            case "language":
                return "en";
            case "theme":
                return "dark";
            case "car_type":
                return "free";
            default:
                return "";
        }
    }

    private static List<RadioButton> createLanguageRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();
        buttons.add(new RadioButton("en", mapOf("en", "English", "ru", "English")));
        buttons.add(new RadioButton("ru", mapOf("en", "Русский", "ru", "Русский")));
        return buttons;
    }

    private static List<RadioButton> createThemeRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();
        buttons.add(new RadioButton("light", mapOf("en", "Light", "ru", "Светлая")));
        buttons.add(new RadioButton("dark", mapOf("en", "Dark", "ru", "Тёмная")));
        return buttons;
    }

    private static List<RadioButton> createCarTypeRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();
        buttons.add(new RadioButton("free", mapOf("en", "Free", "ru", "Фри")));
        buttons.add(new RadioButton("dreamer", mapOf("en", "Dreamer", "ru", "Дример")));
        return buttons;
    }

    private static List<RadioButton> createDefaultRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();
        buttons.add(new RadioButton("default", mapOf("en", "Default", "ru", "По умолчанию")));
        return buttons;
    }

    // ============================================================
    // Button tab content
    // ============================================================

    public static int getButtonSectionCount() {
        return 2;
    }

    public static Map<String, String> getButtonSectionTitle(int sectionIndex) {
        Map<String, String> title = new HashMap<>();
        switch (sectionIndex) {
            case 0:
                title.put("en", "Button Styles");
                title.put("ru", "Стили кнопок");
                break;
            case 1:
                title.put("en", "Button with Description");
                title.put("ru", "Кнопка с описанием");
                break;
            default:
                title.put("en", "Button");
                title.put("ru", "Кнопка");
                break;
        }
        return title;
    }

    // ============================================================
    // Buttons tab content
    // ============================================================

    public static int getButtonsSectionCount() {
        return 3;
    }

    public static Map<String, String> getButtonsSectionTitle(int sectionIndex) {
        Map<String, String> title = new HashMap<>();
        switch (sectionIndex) {
            case 0:
                title.put("en", "Two Buttons");
                title.put("ru", "Две кнопки");
                break;
            case 1:
                title.put("en", "Three Buttons");
                title.put("ru", "Три кнопки");
                break;
            case 2:
                title.put("en", "Buttons with Right Text");
                title.put("ru", "Кнопки с текстом справа");
                break;
            default:
                title.put("en", "Buttons");
                title.put("ru", "Кнопки");
                break;
        }
        return title;
    }

    public static List<ru.voboost.components.buttons.ButtonConfig> getButtonsConfig(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return java.util.Arrays.asList(
                    new ru.voboost.components.buttons.ButtonConfig("lower", "Lower"),
                    new ru.voboost.components.buttons.ButtonConfig("restore", "Restore"));
            case 1:
                return java.util.Arrays.asList(
                    new ru.voboost.components.buttons.ButtonConfig("eco", "ECO"),
                    new ru.voboost.components.buttons.ButtonConfig("comfort", "Comfort"),
                    new ru.voboost.components.buttons.ButtonConfig("sport", "Sport"));
            case 2:
                return java.util.Arrays.asList(
                    new ru.voboost.components.buttons.ButtonConfig("on", "ON"),
                    new ru.voboost.components.buttons.ButtonConfig("off", "OFF"));
            default:
                return java.util.Arrays.asList(
                    new ru.voboost.components.buttons.ButtonConfig("ok", "OK"));
        }
    }

    public static String getButtonsDefaultValue(int sectionIndex) {
        switch (sectionIndex) {
            case 0: return "restore";
            case 1: return "comfort";
            case 2: return "on";
            default: return "ok";
        }
    }

    // ============================================================
    // Checkbox tab content
    // ============================================================

    public static int getCheckboxSectionCount() {
        return 3;
    }

    public static Map<String, String> getCheckboxSectionTitle(int sectionIndex) {
        Map<String, String> title = new HashMap<>();
        switch (sectionIndex) {
            case 0:
                title.put("en", "Checkbox with Label");
                title.put("ru", "Переключатель с меткой");
                break;
            case 1:
                title.put("en", "Checkbox with Label and Description");
                title.put("ru", "Переключатель с меткой и описанием");
                break;
            case 2:
                title.put("en", "Multiple Checkboxes");
                title.put("ru", "Несколько переключателей");
                break;
            default:
                title.put("en", "Checkbox");
                title.put("ru", "Переключатель");
                break;
        }
        return title;
    }

    // ============================================================
    // Radio tab content
    // ============================================================

    public static int getRadioSectionCount() {
        return 3;
    }

    public static Map<String, String> getRadioSectionTitle(int sectionIndex) {
        Map<String, String> title = new HashMap<>();
        switch (sectionIndex) {
            case 0:
                title.put("en", "Radio with Title");
                title.put("ru", "Радио с заголовком");
                break;
            case 1:
                title.put("en", "Radio with Title and Description Above");
                title.put("ru", "Радио с заголовком и описанием сверху");
                break;
            case 2:
                title.put("en", "Radio with Title and Description Below");
                title.put("ru", "Радио с заголовком и описанием снизу");
                break;
            default:
                title.put("en", "Radio");
                title.put("ru", "Радио");
                break;
        }
        return title;
    }

    public static List<RadioButton> getRadioSubRadioButtons(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return java.util.Arrays.asList(
                    new RadioButton("light", mapOf("en", "Light", "ru", "Свет")),
                    new RadioButton("light_sound", mapOf("en", "Light + Sound", "ru", "Свет + Звук")));
            case 1:
                return java.util.Arrays.asList(
                    new RadioButton("low", mapOf("en", "Low", "ru", "Низ")),
                    new RadioButton("med", mapOf("en", "Med", "ru", "Сред")),
                    new RadioButton("high", mapOf("en", "High", "ru", "Выс")));
            case 2:
                return java.util.Arrays.asList(
                    new RadioButton("off", mapOf("en", "Off", "ru", "Выкл")),
                    new RadioButton("15", mapOf("en", "15s", "ru", "15с")),
                    new RadioButton("30", mapOf("en", "30s", "ru", "30с")),
                    new RadioButton("60", mapOf("en", "60s", "ru", "60с")));
            default:
                return java.util.Arrays.asList(
                    new RadioButton("default", mapOf("en", "Default", "ru", "По умолчанию")));
        }
    }

    public static String getRadioSubDefaultValue(int sectionIndex) {
        switch (sectionIndex) {
            case 0: return "light";
            case 1: return "med";
            case 2: return "30";
            default: return "default";
        }
    }

    // ============================================================
    // Select/Dialog/Toast tab content
    // ============================================================

    /**
     * Returns SelectOption items for the Select demo.
     *
     * @return List of SelectOption objects
     */
    public static List<SelectOption> getSelectOptions() {
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

    public static int getToastSectionCount() {
        return 2;
    }

    public static Map<String, String> getToastSectionTitle(int sectionIndex) {
        Map<String, String> title = new HashMap<>();
        switch (sectionIndex) {
            case 0:
                title.put("en", "Short Toast");
                title.put("ru", "Короткое уведомление");
                break;
            case 1:
                title.put("en", "Long Toast");
                title.put("ru", "Длинное уведомление");
                break;
            default:
                title.put("en", "Toast");
                title.put("ru", "Уведомление");
                break;
        }
        return title;
    }

    public static Map<String, String> getToastButtonText(int sectionIndex) {
        Map<String, String> text = new HashMap<>();
        switch (sectionIndex) {
            case 0:
                text.put("en", "Show Short Toast");
                text.put("ru", "Короткое уведомление");
                break;
            case 1:
                text.put("en", "Show Long Toast");
                text.put("ru", "Длинное уведомление");
                break;
            default:
                text.put("en", "Show Toast");
                text.put("ru", "Показать уведомление");
                break;
        }
        return text;
    }

    /**
     * Returns localized button labels for the Dialog demo.
     *
     * @return Map with keys "title", "message", "confirm", "cancel" and values for language codes
     */
    public static Map<String, Map<String, String>> getDialogContent() {
        Map<String, Map<String, String>> content = new HashMap<>();

        Map<String, String> titleLabels = new HashMap<>();
        titleLabels.put("en", "Reset Settings");
        titleLabels.put("ru", "Сброс настроек");
        content.put("title", titleLabels);

        Map<String, String> messageLabels = new HashMap<>();
        messageLabels.put("en", "Are you sure you want to reset all settings?");
        messageLabels.put("ru", "Вы уверены, что хотите сбросить все настройки?");
        content.put("message", messageLabels);

        Map<String, String> confirmLabels = new HashMap<>();
        confirmLabels.put("en", "Reset");
        confirmLabels.put("ru", "Сброс");
        content.put("confirm", confirmLabels);

        Map<String, String> cancelLabels = new HashMap<>();
        cancelLabels.put("en", "Cancel");
        cancelLabels.put("ru", "Отмена");
        content.put("cancel", cancelLabels);

        return content;
    }

    /**
     * Creates a Select component with standard configuration.
     * This ensures consistent setup across all demos.
     *
     * @param context Android context
     * @param options list of select options
     * @param selectedValue initially selected value
     * @param theme theme to apply
     * @param language language to apply
     * @param onValueChange callback for value changes
     * @return configured Select component
     */
    public static ru.voboost.components.select.Select createSelect(
            Context context,
            List<ru.voboost.components.select.SelectOption> options,
            String selectedValue,
            Theme theme,
            Language language,
            ru.voboost.components.select.Select.OnValueChangeListener onValueChange) {
        ru.voboost.components.select.Select select = new ru.voboost.components.select.Select(context);
        select.setTheme(theme);
        select.setLanguage(language);
        select.setOptions(options);
        select.setSelectedValue(selectedValue);
        if (onValueChange != null) {
            select.setOnValueChangeListener(onValueChange);
        }
        return select;
    }

    // ============================================================
    // Climate tab content (for demo-kotlin scroll testing)
    // ============================================================

    public static int getClimateSectionCount() {
        return 5;
    }

    public static Map<String, String> getClimateSectionTitle(int sectionIndex) {
        Map<String, String> title = new HashMap<>();
        switch (sectionIndex) {
            case 0:
                title.put("en", "Temperature");
                title.put("ru", "Температура");
                break;
            case 1:
                title.put("en", "Fan Speed");
                title.put("ru", "Скорость вентилятора");
                break;
            case 2:
                title.put("en", "Air Direction");
                title.put("ru", "Направление воздуха");
                break;
            case 3:
                title.put("en", "AC Mode");
                title.put("ru", "Режим кондиционера");
                break;
            case 4:
                title.put("en", "Recirculation");
                title.put("ru", "Рециркуляция");
                break;
            default:
                title.put("en", "Climate");
                title.put("ru", "Климат");
                break;
        }
        return title;
    }

    public static List<RadioButton> getClimateSubRadioButtons(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return java.util.Arrays.asList(
                    new RadioButton("low", mapOf("en", "Low", "ru", "Низ")),
                    new RadioButton("med", mapOf("en", "Med", "ru", "Сред")),
                    new RadioButton("high", mapOf("en", "High", "ru", "Выс")));
            case 1:
                return java.util.Arrays.asList(
                    new RadioButton("auto", mapOf("en", "Auto", "ru", "Авто")),
                    new RadioButton("1", mapOf("en", "1", "ru", "1")),
                    new RadioButton("2", mapOf("en", "2", "ru", "2")),
                    new RadioButton("3", mapOf("en", "3", "ru", "3")));
            case 2:
                return java.util.Arrays.asList(
                    new RadioButton("face", mapOf("en", "Face", "ru", "Лицо")),
                    new RadioButton("feet", mapOf("en", "Feet", "ru", "Ноги")),
                    new RadioButton("both", mapOf("en", "Both", "ru", "Оба")));
            case 3:
                return java.util.Arrays.asList(
                    new RadioButton("off", mapOf("en", "Off", "ru", "Выкл")),
                    new RadioButton("on", mapOf("en", "On", "ru", "Вкл")),
                    new RadioButton("auto", mapOf("en", "Auto", "ru", "Авто")));
            case 4:
                return java.util.Arrays.asList(
                    new RadioButton("fresh", mapOf("en", "Fresh", "ru", "Свежий")),
                    new RadioButton("recirc", mapOf("en", "Recirc", "ru", "Рециркуля")));
            default:
                return java.util.Arrays.asList(
                    new RadioButton("default", mapOf("en", "Default", "ru", "По умолчанию")));
        }
    }

    public static String getClimateSubDefaultValue(int sectionIndex) {
        switch (sectionIndex) {
            case 0: return "med";
            case 1: return "auto";
            case 2: return "both";
            case 3: return "auto";
            case 4: return "fresh";
            default: return "default";
        }
    }
}

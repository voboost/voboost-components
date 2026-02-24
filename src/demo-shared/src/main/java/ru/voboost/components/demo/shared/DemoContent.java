package ru.voboost.components.demo.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.tabs.TabItem;

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

    /**
     * Returns the list of TabItem objects for all 7 tabs.
     *
     * @return List of TabItem objects
     */
    public static List<TabItem> getTabItems() {
        List<TabItem> tabItems = new ArrayList<>();

        tabItems.add(new TabItem("language", createLanguageTabLabel()));
        tabItems.add(new TabItem("theme", createThemeTabLabel()));
        tabItems.add(new TabItem("car_type", createCarTypeTabLabel()));
        tabItems.add(new TabItem("climate", createClimateTabLabel()));
        tabItems.add(new TabItem("audio", createAudioTabLabel()));
        tabItems.add(new TabItem("display", createDisplayTabLabel()));
        tabItems.add(new TabItem("system", createSystemTabLabel()));

        return tabItems;
    }

    /**
     * Returns the section title for the specified tab.
     *
     * @param tabValue the tab value (e.g., "language", "theme")
     * @return Map of language code to title text
     */
    public static Map<String, String> getSectionTitle(String tabValue) {
        switch (tabValue) {
            case "language":
                return createLanguageSectionTitle();
            case "theme":
                return createThemeSectionTitle();
            case "car_type":
                return createCarTypeSectionTitle();
            case "climate":
                return createClimateSectionTitle();
            case "audio":
                return createAudioSectionTitle();
            case "display":
                return createDisplaySectionTitle();
            case "system":
                return createSystemSectionTitle();
            default:
                return createDefaultSectionTitle();
        }
    }

    /**
     * Returns the radio buttons for the specified tab.
     *
     * @param tabValue the tab value (e.g., "language", "theme")
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
            case "climate":
                return createClimateRadioButtons();
            case "audio":
                return createAudioRadioButtons();
            case "display":
                return createDisplayRadioButtons();
            case "system":
                return createSystemRadioButtons();
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
            case "climate":
                return "auto";
            case "audio":
                return "standard";
            case "display":
                return "auto";
            case "system":
                return "normal";
            default:
                return "";
        }
    }

    // Tab label creation methods
    private static Map<String, String> createLanguageTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Language");
        label.put("ru", "Язык");
        return label;
    }

    private static Map<String, String> createThemeTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Theme");
        label.put("ru", "Тема");
        return label;
    }

    private static Map<String, String> createCarTypeTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Car Type");
        label.put("ru", "Тип авто");
        return label;
    }

    private static Map<String, String> createClimateTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Climate");
        label.put("ru", "Климат");
        return label;
    }

    private static Map<String, String> createAudioTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Audio");
        label.put("ru", "Аудио");
        return label;
    }

    private static Map<String, String> createDisplayTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "Display");
        label.put("ru", "Дисплей");
        return label;
    }

    private static Map<String, String> createSystemTabLabel() {
        Map<String, String> label = new HashMap<>();
        label.put("en", "System");
        label.put("ru", "Система");
        return label;
    }

    // Section title creation methods
    private static Map<String, String> createLanguageSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Language Selection");
        title.put("ru", "Выбор языка");
        return title;
    }

    private static Map<String, String> createThemeSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Theme Selection");
        title.put("ru", "Выбор темы");
        return title;
    }

    private static Map<String, String> createCarTypeSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Vehicle Model");
        title.put("ru", "Модель автомобиля");
        return title;
    }

    private static Map<String, String> createClimateSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Climate Control");
        title.put("ru", "Управление климатом");
        return title;
    }

    private static Map<String, String> createAudioSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Audio Settings");
        title.put("ru", "Настройки аудио");
        return title;
    }

    private static Map<String, String> createDisplaySectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Display Settings");
        title.put("ru", "Настройки дисплея");
        return title;
    }

    private static Map<String, String> createSystemSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "System Settings");
        title.put("ru", "Системные настройки");
        return title;
    }

    private static Map<String, String> createDefaultSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");
        title.put("ru", "Настройки");
        return title;
    }

    // Radio button creation methods
    private static List<RadioButton> createLanguageRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> englishLabels = new HashMap<>();
        englishLabels.put("en", "English");
        englishLabels.put("ru", "English");
        buttons.add(new RadioButton("en", englishLabels));

        Map<String, String> russianLabels = new HashMap<>();
        russianLabels.put("en", "Русский");
        russianLabels.put("ru", "Русский");
        buttons.add(new RadioButton("ru", russianLabels));

        return buttons;
    }

    private static List<RadioButton> createThemeRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> lightLabels = new HashMap<>();
        lightLabels.put("en", "Light");
        lightLabels.put("ru", "Светлая");
        buttons.add(new RadioButton("light", lightLabels));

        Map<String, String> darkLabels = new HashMap<>();
        darkLabels.put("en", "Dark");
        darkLabels.put("ru", "Тёмная");
        buttons.add(new RadioButton("dark", darkLabels));

        return buttons;
    }

    private static List<RadioButton> createCarTypeRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> freeLabels = new HashMap<>();
        freeLabels.put("en", "Free");
        freeLabels.put("ru", "Фри");
        buttons.add(new RadioButton("free", freeLabels));

        Map<String, String> dreamerLabels = new HashMap<>();
        dreamerLabels.put("en", "Dreamer");
        dreamerLabels.put("ru", "Дример");
        buttons.add(new RadioButton("dreamer", dreamerLabels));

        return buttons;
    }

    private static List<RadioButton> createClimateRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> autoLabels = new HashMap<>();
        autoLabels.put("en", "Automatic");
        autoLabels.put("ru", "Автоматический");
        buttons.add(new RadioButton("auto", autoLabels));

        Map<String, String> manualLabels = new HashMap<>();
        manualLabels.put("en", "Manual");
        manualLabels.put("ru", "Ручной");
        buttons.add(new RadioButton("manual", manualLabels));

        Map<String, String> ecoLabels = new HashMap<>();
        ecoLabels.put("en", "Eco Mode");
        ecoLabels.put("ru", "Эко режим");
        buttons.add(new RadioButton("eco", ecoLabels));

        Map<String, String> sportLabels = new HashMap<>();
        sportLabels.put("en", "Sport Mode");
        sportLabels.put("ru", "Спорт режим");
        buttons.add(new RadioButton("sport", sportLabels));

        return buttons;
    }

    private static List<RadioButton> createAudioRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> standardLabels = new HashMap<>();
        standardLabels.put("en", "Standard");
        standardLabels.put("ru", "Стандартный");
        buttons.add(new RadioButton("standard", standardLabels));

        Map<String, String> premiumLabels = new HashMap<>();
        premiumLabels.put("en", "Premium");
        premiumLabels.put("ru", "Премиум");
        buttons.add(new RadioButton("premium", premiumLabels));

        Map<String, String> surroundLabels = new HashMap<>();
        surroundLabels.put("en", "Surround");
        surroundLabels.put("ru", "Объёмный звук");
        buttons.add(new RadioButton("surround", surroundLabels));

        Map<String, String> offLabels = new HashMap<>();
        offLabels.put("en", "Off");
        offLabels.put("ru", "Выключено");
        buttons.add(new RadioButton("off", offLabels));

        return buttons;
    }

    private static List<RadioButton> createDisplayRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> autoLabels = new HashMap<>();
        autoLabels.put("en", "Automatic");
        autoLabels.put("ru", "Автоматический");
        buttons.add(new RadioButton("auto", autoLabels));

        Map<String, String> dayLabels = new HashMap<>();
        dayLabels.put("en", "Day Mode");
        dayLabels.put("ru", "Дневной режим");
        buttons.add(new RadioButton("day", dayLabels));

        Map<String, String> nightLabels = new HashMap<>();
        nightLabels.put("en", "Night Mode");
        nightLabels.put("ru", "Ночной режим");
        buttons.add(new RadioButton("night", nightLabels));

        Map<String, String> adaptiveLabels = new HashMap<>();
        adaptiveLabels.put("en", "Adaptive");
        adaptiveLabels.put("ru", "Адаптивный");
        buttons.add(new RadioButton("adaptive", adaptiveLabels));

        return buttons;
    }

    private static List<RadioButton> createSystemRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> normalLabels = new HashMap<>();
        normalLabels.put("en", "Normal");
        normalLabels.put("ru", "Обычный");
        buttons.add(new RadioButton("normal", normalLabels));

        Map<String, String> performanceLabels = new HashMap<>();
        performanceLabels.put("en", "Performance");
        performanceLabels.put("ru", "Производительность");
        buttons.add(new RadioButton("performance", performanceLabels));

        Map<String, String> ecoLabels = new HashMap<>();
        ecoLabels.put("en", "Eco Mode");
        ecoLabels.put("ru", "Эко режим");
        buttons.add(new RadioButton("eco", ecoLabels));

        Map<String, String> customLabels = new HashMap<>();
        customLabels.put("en", "Custom");
        customLabels.put("ru", "Пользовательский");
        buttons.add(new RadioButton("custom", customLabels));

        return buttons;
    }

    private static List<RadioButton> createDefaultRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> defaultLabels = new HashMap<>();
        defaultLabels.put("en", "Default");
        defaultLabels.put("ru", "По умолчанию");
        buttons.add(new RadioButton("default", defaultLabels));

        return buttons;
    }

    // Climate sub-section methods for multi-section climate panel

    /**
     * Returns the number of climate sub-sections.
     *
     * @return the number of climate sub-sections
     */
    public static int getClimateSectionCount() {
        return 5;
    }

    /**
     * Returns the section title for a specific climate sub-section.
     *
     * @param sectionIndex the section index (0-4)
     * @return Map of language code to title text
     */
    public static Map<String, String> getClimateSectionTitle(int sectionIndex) {
        Map<String, String> title = new HashMap<>();
        switch (sectionIndex) {
            case 0:
                title.put("en", "Temperature Mode");
                title.put("ru", "Режим температуры");
                break;
            case 1:
                title.put("en", "Energy Mode");
                title.put("ru", "Режим энергии");
                break;
            case 2:
                title.put("en", "Fan Speed");
                title.put("ru", "Скорость вентилятора");
                break;
            case 3:
                title.put("en", "Air Distribution");
                title.put("ru", "Распределение воздуха");
                break;
            case 4:
                title.put("en", "Seat Heating");
                title.put("ru", "Подогрев сидений");
                break;
            default:
                title.put("en", "Settings");
                title.put("ru", "Настройки");
                break;
        }
        return title;
    }

    /**
     * Returns the radio buttons for a specific climate sub-section.
     *
     * @param sectionIndex the section index (0-4)
     * @return List of RadioButton objects
     */
    public static List<RadioButton> getClimateSubRadioButtons(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return createClimateTemperatureRadioButtons();
            case 1:
                return createClimateEnergyRadioButtons();
            case 2:
                return createClimateFanSpeedRadioButtons();
            case 3:
                return createClimateAirDistributionRadioButtons();
            case 4:
                return createClimateSeatHeatingRadioButtons();
            default:
                return createDefaultRadioButtons();
        }
    }

    /**
     * Returns the default value for a specific climate sub-section.
     *
     * @param sectionIndex the section index (0-4)
     * @return the default value
     */
    public static String getClimateSubDefaultValue(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return "auto";
            case 1:
                return "eco";
            case 2:
                return "auto_fan";
            case 3:
                return "face";
            case 4:
                return "off";
            default:
                return "";
        }
    }

    private static List<RadioButton> createClimateTemperatureRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> autoLabels = new HashMap<>();
        autoLabels.put("en", "Automatic");
        autoLabels.put("ru", "Автоматический");
        buttons.add(new RadioButton("auto", autoLabels));

        Map<String, String> manualLabels = new HashMap<>();
        manualLabels.put("en", "Manual");
        manualLabels.put("ru", "Ручной");
        buttons.add(new RadioButton("manual", manualLabels));

        return buttons;
    }

    private static List<RadioButton> createClimateEnergyRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> ecoLabels = new HashMap<>();
        ecoLabels.put("en", "Eco Mode");
        ecoLabels.put("ru", "Эко режим");
        buttons.add(new RadioButton("eco", ecoLabels));

        Map<String, String> sportLabels = new HashMap<>();
        sportLabels.put("en", "Sport Mode");
        sportLabels.put("ru", "Спорт режим");
        buttons.add(new RadioButton("sport", sportLabels));

        Map<String, String> comfortLabels = new HashMap<>();
        comfortLabels.put("en", "Comfort");
        comfortLabels.put("ru", "Комфорт");
        buttons.add(new RadioButton("comfort", comfortLabels));

        return buttons;
    }

    private static List<RadioButton> createClimateFanSpeedRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> lowLabels = new HashMap<>();
        lowLabels.put("en", "Low");
        lowLabels.put("ru", "Низкая");
        buttons.add(new RadioButton("low", lowLabels));

        Map<String, String> mediumLabels = new HashMap<>();
        mediumLabels.put("en", "Medium");
        mediumLabels.put("ru", "Средняя");
        buttons.add(new RadioButton("medium", mediumLabels));

        Map<String, String> highLabels = new HashMap<>();
        highLabels.put("en", "High");
        highLabels.put("ru", "Высокая");
        buttons.add(new RadioButton("high", highLabels));

        Map<String, String> autoFanLabels = new HashMap<>();
        autoFanLabels.put("en", "Auto");
        autoFanLabels.put("ru", "Авто");
        buttons.add(new RadioButton("auto_fan", autoFanLabels));

        return buttons;
    }

    private static List<RadioButton> createClimateAirDistributionRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> faceLabels = new HashMap<>();
        faceLabels.put("en", "Face");
        faceLabels.put("ru", "Лицо");
        buttons.add(new RadioButton("face", faceLabels));

        Map<String, String> feetLabels = new HashMap<>();
        feetLabels.put("en", "Feet");
        feetLabels.put("ru", "Ноги");
        buttons.add(new RadioButton("feet", feetLabels));

        Map<String, String> windshieldLabels = new HashMap<>();
        windshieldLabels.put("en", "Windshield");
        windshieldLabels.put("ru", "Лобовое стекло");
        buttons.add(new RadioButton("windshield", windshieldLabels));

        Map<String, String> mixedLabels = new HashMap<>();
        mixedLabels.put("en", "Mixed");
        mixedLabels.put("ru", "Смешанный");
        buttons.add(new RadioButton("mixed", mixedLabels));

        return buttons;
    }

    private static List<RadioButton> createClimateSeatHeatingRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> offLabels = new HashMap<>();
        offLabels.put("en", "Off");
        offLabels.put("ru", "Выключено");
        buttons.add(new RadioButton("off", offLabels));

        Map<String, String> lowLabels = new HashMap<>();
        lowLabels.put("en", "Low");
        lowLabels.put("ru", "Низкий");
        buttons.add(new RadioButton("low_heat", lowLabels));

        Map<String, String> mediumLabels = new HashMap<>();
        mediumLabels.put("en", "Medium");
        mediumLabels.put("ru", "Средний");
        buttons.add(new RadioButton("medium_heat", mediumLabels));

        Map<String, String> highLabels = new HashMap<>();
        highLabels.put("en", "High");
        highLabels.put("ru", "Высокий");
        buttons.add(new RadioButton("high_heat", highLabels));

        return buttons;
    }
}

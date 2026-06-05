package ru.voboost.components.demo.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.select.SelectOption;
import ru.voboost.components.tabs.TabItem;

/**
 * Shared content provider for demo applications.
 *
 * <p>This class is the single source of truth for all localized demo content:
 * tab items, section titles, radio/checkbox/button labels, dialog and toast text.
 * All three demos (Java, Kotlin, Compose) render from these getters, which keeps
 * them functionally identical.
 */
public class DemoContent {

    private DemoContent() {
        // Prevent instantiation
    }

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

    public static List<TabItem> getTabItems() {
        List<TabItem> tabItems = new ArrayList<>();
        tabItems.add(new TabItem("settings", mapOf("en", "Settings", "ru", "Настройки")));
        tabItems.add(new TabItem("button", mapOf("en", "Button", "ru", "Кнопка")));
        tabItems.add(new TabItem("buttons", mapOf("en", "Buttons", "ru", "Кнопки")));
        tabItems.add(new TabItem("checkbox", mapOf("en", "Checkbox", "ru", "Переключатель")));
        tabItems.add(new TabItem("radio", mapOf("en", "Radio", "ru", "Радио")));
        tabItems.add(new TabItem("select", mapOf("en", "Select", "ru", "Выбор")));
        tabItems.add(new TabItem("dialog", mapOf("en", "Dialog", "ru", "Диалог")));
        tabItems.add(new TabItem("toast", mapOf("en", "Toast", "ru", "Уведомление")));
        return tabItems;
    }

    // ============================================================
    // Section titles
    // ============================================================

    public static Map<String, String> getSectionTitle(String tabValue) {
        switch (tabValue) {
            case "settings":
                return mapOf("en", "Settings", "ru", "Настройки");
            case "button":
                return mapOf("en", "Button Component", "ru", "Компонент Кнопка");
            case "buttons":
                return mapOf("en", "Buttons Component", "ru", "Компонент Кнопки");
            case "checkbox":
                return mapOf("en", "Checkbox Component", "ru", "Компонент Переключатель");
            case "radio":
                return mapOf("en", "Radio Component", "ru", "Компонент Радио");
            case "select":
                return mapOf("en", "Select Component", "ru", "Компонент Выбор");
            case "dialog":
                return mapOf("en", "Dialog Component", "ru", "Компонент Диалог");
            case "toast":
                return mapOf("en", "Toast Component", "ru", "Компонент Уведомление");
            default:
                return mapOf("en", "Settings", "ru", "Настройки");
        }
    }

    public static Map<String, String> getSelectSectionTitle() {
        return getSectionTitle("select");
    }

    public static Map<String, String> getDialogSectionTitle() {
        return getSectionTitle("dialog");
    }

    // ============================================================
    // Settings tab: language/theme/car_type radios
    // ============================================================

    public static List<RadioButton> getRadioButtons(String tabValue) {
        switch (tabValue) {
            case "language":
                return java.util.Arrays.asList(
                    new RadioButton("en", mapOf("en", "English", "ru", "English")),
                    new RadioButton("ru", mapOf("en", "Русский", "ru", "Русский")));
            case "theme":
                return java.util.Arrays.asList(
                    new RadioButton("light", mapOf("en", "Light", "ru", "Светлая")),
                    new RadioButton("dark", mapOf("en", "Dark", "ru", "Тёмная")));
            case "car_type":
                return java.util.Arrays.asList(
                    new RadioButton("free", mapOf("en", "Free", "ru", "Фри")),
                    new RadioButton("dreamer", mapOf("en", "Dreamer", "ru", "Дример")));
            default:
                return java.util.Arrays.asList(
                    new RadioButton("default", mapOf("en", "Default", "ru", "По умолчанию")));
        }
    }

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

    // ============================================================
    // Button tab content
    // ============================================================

    public static int getButtonSectionCount() {
        return 2;
    }

    public static Map<String, String> getButtonSectionTitle(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return mapOf("en", "Button Styles", "ru", "Стили кнопок");
            case 1:
                return mapOf("en", "Button with Description", "ru", "Кнопка с описанием");
            default:
                return mapOf("en", "Button", "ru", "Кнопка");
        }
    }

    public static String getButtonPrimaryText() {
        return "Primary";
    }

    public static String getButtonSecondaryText() {
        return "Secondary";
    }

    public static String getButtonWithDescriptionText() {
        return "Calibrate";
    }

    public static Map<String, String> getButtonWithDescriptionDescription() {
        return mapOf(
            "en", "Run camera calibration.\nDrive straight for 2 minutes.",
            "ru", "Запустить калибровку камеры.\nДвигайтесь прямо 2 минуты.");
    }

    // ============================================================
    // Buttons tab content
    // ============================================================

    public static int getButtonsSectionCount() {
        return 3;
    }

    public static Map<String, String> getButtonsSectionTitle(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return mapOf("en", "Two Buttons", "ru", "Две кнопки");
            case 1:
                return mapOf("en", "Three Buttons", "ru", "Три кнопки");
            case 2:
                return mapOf("en", "Buttons with Right Text", "ru", "Кнопки с текстом справа");
            default:
                return mapOf("en", "Buttons", "ru", "Кнопки");
        }
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
            case 0:
                return "restore";
            case 1:
                return "comfort";
            case 2:
                return "on";
            default:
                return "ok";
        }
    }

    public static Map<String, String> getButtonsRightText(int sectionIndex) {
        if (sectionIndex == 2) {
            return mapOf("en", "Auto headlamp", "ru", "Авто фары");
        }
        return new HashMap<>();
    }

    // ============================================================
    // Checkbox tab content
    // ============================================================

    public static int getCheckboxSectionCount() {
        return 3;
    }

    public static Map<String, String> getCheckboxSectionTitle(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return mapOf("en", "Checkbox with Label", "ru", "Переключатель с меткой");
            case 1:
                return mapOf(
                    "en", "Checkbox with Label and Description",
                    "ru", "Переключатель с меткой и описанием");
            case 2:
                return mapOf("en", "Multiple Checkboxes", "ru", "Несколько переключателей");
            default:
                return mapOf("en", "Checkbox", "ru", "Переключатель");
        }
    }

    public static Map<String, String> getCheckboxLabel(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return mapOf("en", "Auto-fold mirrors", "ru", "Автоскладывание зеркал");
            case 1:
                return mapOf("en", "Tow mode", "ru", "Режим буксировки");
            case 2:
                return mapOf("en", "Welcome lamp", "ru", "Приветственная подсветка");
            default:
                return mapOf("en", "Checkbox", "ru", "Переключатель");
        }
    }

    public static Map<String, String> getCheckboxDescription(int sectionIndex) {
        if (sectionIndex == 1) {
            return mapOf(
                "en", "Maintain N gear when vehicle is rescued",
                "ru", "Поддерживать нейтраль при буксировке");
        }
        return new HashMap<>();
    }

    public static boolean getCheckboxChecked(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return true;
            case 2:
                return true;
            default:
                return false;
        }
    }

    public static Map<String, String> getCheckboxExtraLabel() {
        return mapOf("en", "Auto-tilt mirrors", "ru", "Автонаклон зеркал");
    }

    public static boolean getCheckboxExtraChecked() {
        return false;
    }

    // ============================================================
    // Radio tab content
    // ============================================================

    public static int getRadioSectionCount() {
        return 3;
    }

    public static Map<String, String> getRadioSectionTitle(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return mapOf("en", "Radio with Title", "ru", "Радио с заголовком");
            case 1:
                return mapOf(
                    "en", "Radio with Title and Description Above",
                    "ru", "Радио с заголовком и описанием сверху");
            case 2:
                return mapOf(
                    "en", "Radio with Title and Description Below",
                    "ru", "Радио с заголовком и описанием снизу");
            default:
                return mapOf("en", "Radio", "ru", "Радио");
        }
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
            case 0:
                return "light";
            case 1:
                return "med";
            case 2:
                return "30";
            default:
                return "default";
        }
    }

    public static Map<String, String> getRadioSubTitle(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return mapOf("en", "Anti-theft alarm", "ru", "Противоугонная сигнализация");
            case 1:
                return mapOf("en", "Energy recovery", "ru", "Рекуперация энергии");
            case 2:
                return mapOf("en", "Come home lights", "ru", "Подсветка дороги домой");
            default:
                return mapOf("en", "Radio", "ru", "Радио");
        }
    }

    public static Map<String, String> getRadioSubDescriptionAbove(int sectionIndex) {
        if (sectionIndex == 1) {
            return mapOf(
                "en", "Adjusts braking energy recovery level",
                "ru", "Регулирует уровень рекуперации торможения");
        }
        return new HashMap<>();
    }

    public static Map<String, String> getRadioSubDescriptionBelow(int sectionIndex) {
        if (sectionIndex == 2) {
            return mapOf(
                "en", "Headlights stay on after locking",
                "ru", "Фары остаются включёнными после блокировки");
        }
        return new HashMap<>();
    }

    // ============================================================
    // Select tab content
    // ============================================================

    public static List<SelectOption> getSelectOptions() {
        List<SelectOption> options = new ArrayList<>();
        options.add(new SelectOption("auto", mapOf("en", "Automatic", "ru", "Автоматический")));
        options.add(new SelectOption("manual", mapOf("en", "Manual", "ru", "Ручной")));
        options.add(new SelectOption("eco", mapOf("en", "Eco Mode", "ru", "Эко режим")));
        options.add(new SelectOption("sport", mapOf("en", "Sport Mode", "ru", "Спорт режим")));
        options.add(new SelectOption("comfort", mapOf("en", "Comfort", "ru", "Комфорт")));
        return options;
    }

    // ============================================================
    // Dialog tab content
    // ============================================================

    public static Map<String, Map<String, String>> getDialogContent() {
        Map<String, Map<String, String>> content = new HashMap<>();
        content.put("title", mapOf("en", "Reset Settings", "ru", "Сброс настроек"));
        content.put(
            "message",
            mapOf(
                "en", "Are you sure you want to reset all settings?",
                "ru", "Вы уверены, что хотите сбросить все настройки?"));
        content.put("confirm", mapOf("en", "Reset", "ru", "Сброс"));
        content.put("cancel", mapOf("en", "Cancel", "ru", "Отмена"));
        return content;
    }

    // ============================================================
    // Toast tab content
    // ============================================================

    public static int getToastSectionCount() {
        return 2;
    }

    public static Map<String, String> getToastSectionTitle(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return mapOf("en", "Short Toast", "ru", "Короткое уведомление");
            case 1:
                return mapOf("en", "Long Toast", "ru", "Длинное уведомление");
            default:
                return mapOf("en", "Toast", "ru", "Уведомление");
        }
    }

    public static Map<String, String> getToastButtonText(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return mapOf("en", "Show Short Toast", "ru", "Короткое уведомление");
            case 1:
                return mapOf("en", "Show Long Toast", "ru", "Длинное уведомление");
            default:
                return mapOf("en", "Show Toast", "ru", "Показать уведомление");
        }
    }

    public static Map<String, String> getToastMessage(int sectionIndex) {
        switch (sectionIndex) {
            case 0:
                return mapOf("en", "Settings saved", "ru", "Настройки сохранены");
            case 1:
                return mapOf(
                    "en", "Your settings have been successfully saved",
                    "ru", "Ваши настройки успешно сохранены");
            default:
                return mapOf("en", "Settings saved", "ru", "Настройки сохранены");
        }
    }
}

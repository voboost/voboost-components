package ru.voboost.components.demo.pixel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.tabs.TabItem;

/**
 * Static content matching the reference screenshot (interface-2-display.png).
 *
 * This content replicates the Voyah settings Display page:
 * - 5 tabs: Network, Display, Voice, Sound, Reminder
 * - Display tab selected (index 1)
 * - 3 sections each with "Language" title
 * - Radio: "Chinese (Simplified)" / "English", English selected
 */
public final class PixelContent {

    private PixelContent() {
        // Prevent instantiation
    }

    // ================================================================
    // TABS
    // ================================================================

    /**
     * Returns tab items matching the reference screenshot.
     */
    public static List<TabItem> getTabItems() {
        List<TabItem> items = new ArrayList<>();
        items.add(createTab("network", "Network"));
        items.add(createTab("display", "Display"));
        items.add(createTab("voice", "Voice"));
        items.add(createTab("sound", "Sound"));
        items.add(createTab("reminder", "Reminder"));
        return items;
    }

    /**
     * The initially selected tab value.
     */
    public static String getSelectedTab() {
        return "display";
    }

    // ================================================================
    // SECTIONS
    // ================================================================

    /**
     * Returns section title "Language".
     */
    public static Map<String, String> getLanguageSectionTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Language");
        title.put("ru", "Language");
        return title;
    }

    /**
     * Number of Language sections on the Display tab.
     */
    public static int getSectionCount() {
        return 3;
    }

    // ================================================================
    // RADIO BUTTONS
    // ================================================================

    /**
     * Returns radio buttons: "Chinese (Simplified)" and "English".
     */
    public static List<RadioButton> getLanguageRadioButtons() {
        List<RadioButton> buttons = new ArrayList<>();

        Map<String, String> chineseLabel = new HashMap<>();
        chineseLabel.put("en", "Chinese (Simplified)");
        chineseLabel.put("ru", "Chinese (Simplified)");
        buttons.add(new RadioButton("zh", chineseLabel));

        Map<String, String> englishLabel = new HashMap<>();
        englishLabel.put("en", "English");
        englishLabel.put("ru", "English");
        buttons.add(new RadioButton("en", englishLabel));

        return buttons;
    }

    /**
     * The initially selected radio value.
     */
    public static String getSelectedLanguage() {
        return "en";
    }

    // ================================================================
    // HELPERS
    // ================================================================

    private static TabItem createTab(String value, String label) {
        Map<String, String> labels = new HashMap<>();
        labels.put("en", label);
        labels.put("ru", label);
        return new TabItem(value, labels);
    }
}

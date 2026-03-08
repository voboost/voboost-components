# Consumer ProGuard rules for voboost-components
# These rules are automatically applied to all consumers of this library

# Keep component classes
-keep public class ru.voboost.components.button.Button
-keep public class ru.voboost.components.buttons.Buttons
# Checkbox component - keep all public classes and inner classes
-keep public class ru.voboost.components.checkbox.Checkbox
-keep public class ru.voboost.components.checkbox.CheckboxColors
-keep public class ru.voboost.components.checkbox.CheckboxColorSchemes
-keep public class ru.voboost.components.checkbox.CheckboxDimensions
-keep public class ru.voboost.components.checkbox.CheckboxTextColors
-keep public class ru.voboost.components.checkbox.CheckboxTextColorSchemes
-keep public class ru.voboost.components.checkbox.CheckboxTextDimensions
-keep public class ru.voboost.components.checkbox.CheckboxTheme
-keep public class ru.voboost.components.dialog.Dialog
-keep public class ru.voboost.components.panel.Panel
-keep public class ru.voboost.components.popup.Popup
-keep public class ru.voboost.components.radio.Radio
-keep public class ru.voboost.components.screen.Screen
-keep public class ru.voboost.components.section.Section
-keep public class ru.voboost.components.select.Select
-keep public class ru.voboost.components.tabs.Tabs
-keep public class ru.voboost.components.text.Text
-keep public class ru.voboost.components.toast.Toast

# Keep theme classes
-keep public class ru.voboost.components.button.ButtonStyle
-keep public class ru.voboost.components.button.ButtonTheme
-keep public class ru.voboost.components.buttons.ButtonsConfig
-keep public class ru.voboost.components.buttons.ButtonsTheme
-keep public class ru.voboost.components.checkbox.CheckboxTheme
-keep public class ru.voboost.components.dialog.DialogTheme
-keep public class ru.voboost.components.panel.PanelTheme
-keep public class ru.voboost.components.popup.PopupTheme
-keep public class ru.voboost.components.radio.RadioTheme
-keep public class ru.voboost.components.screen.ScreenTheme
-keep public class ru.voboost.components.section.SectionTheme
-keep public class ru.voboost.components.select.SelectTheme
-keep public class ru.voboost.components.tabs.TabsTheme
-keep public class ru.voboost.components.text.TextTheme
-keep public class ru.voboost.components.toast.ToastTheme

# Keep utility classes
-keep public class ru.voboost.components.font.Font
-keep public class ru.voboost.components.i18n.Language
-keep public class ru.voboost.components.theme.Theme

# Keep custom View constructors
-keepclassmembers class ru.voboost.components.** {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

# Keep enums
-keepclassmembers enum ru.voboost.components.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

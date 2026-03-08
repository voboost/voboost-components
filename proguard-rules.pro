# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Kotlin metadata for reflection
-keep class kotlin.Metadata { *; }

# Keep library public API - specific classes only
-keep public class ru.voboost.components.radio.Radio { *; }
-keep public class ru.voboost.components.radio.RadioButton { *; }
-keep public class ru.voboost.components.screen.Screen { *; }
-keep public class ru.voboost.components.panel.Panel { *; }
-keep public class ru.voboost.components.section.Section { *; }
-keep public class ru.voboost.components.tabs.Tabs { *; }
-keep public class ru.voboost.components.tabs.TabItem { *; }
-keep public class ru.voboost.components.text.Text { *; }
-keep public class ru.voboost.components.text.TextRole { *; }
-keep public class ru.voboost.components.font.Font { *; }
-keep public class ru.voboost.components.theme.Theme { *; }
-keep public class ru.voboost.components.i18n.Language { *; }
-keep public class ru.voboost.components.button.Button { *; }
-keep public class ru.voboost.components.button.ButtonStyle { *; }
# Checkbox component - keep all public classes and inner classes
-keep public class ru.voboost.components.checkbox.Checkbox { *; }
-keep public class ru.voboost.components.checkbox.CheckboxColors { *; }
-keep public class ru.voboost.components.checkbox.CheckboxColorSchemes { *; }
-keep public class ru.voboost.components.checkbox.CheckboxDimensions { *; }
-keep public class ru.voboost.components.checkbox.CheckboxTextColors { *; }
-keep public class ru.voboost.components.checkbox.CheckboxTextColorSchemes { *; }
-keep public class ru.voboost.components.checkbox.CheckboxTextDimensions { *; }
-keep public class ru.voboost.components.checkbox.CheckboxTheme { *; }
-keep public class ru.voboost.components.toast.Toast { *; }
-keep public class ru.voboost.components.dialog.Dialog { *; }
-keep public class ru.voboost.components.dialog.DialogColors { *; }
-keep public class ru.voboost.components.dialog.DialogDimensions { *; }
-keep public class ru.voboost.components.dialog.DialogColorSchemes { *; }
-keep public class ru.voboost.components.dialog.DialogTheme { *; }
-keep public class ru.voboost.components.popup.Popup { *; }
-keep public class ru.voboost.components.popup.PopupColors { *; }
-keep public class ru.voboost.components.popup.PopupDimensions { *; }
-keep public class ru.voboost.components.popup.PopupColorSchemes { *; }
-keep public class ru.voboost.components.popup.PopupTheme { *; }
-keep public class ru.voboost.components.select.Select { *; }
-keep public class ru.voboost.components.select.SelectOption { *; }
-keep public class ru.voboost.components.select.SelectColors { *; }
-keep public class ru.voboost.components.select.SelectDimensions { *; }
-keep public class ru.voboost.components.select.SelectColorSchemes { *; }
-keep public class ru.voboost.components.select.SelectTheme { *; }
-keep public class ru.voboost.components.select.SelectPopup { *; }
-keep public class ru.voboost.components.select.SelectWheel { *; }
-keep public class ru.voboost.components.select.SelectOption { *; }
-keep public class ru.voboost.components.buttons.Buttons { *; }
-keep public class ru.voboost.components.buttons.ButtonConfig { *; }
-keep public class ru.voboost.components.i18n.ILocalizable { *; }
-keep public class ru.voboost.components.theme.IThemable { *; }

# Keep Android View constructors for custom views
-keepclasseswithmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

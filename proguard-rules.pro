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

# Keep Android View constructors for custom views
-keepclasseswithmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

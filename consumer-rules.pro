# Consumer ProGuard rules for voboost-components
# These rules are automatically applied to all consumers of this library

# Keep public API classes — consumers need these
-keep public class ru.voboost.components.radio.Radio { public *; }
-keep public class ru.voboost.components.radio.RadioButton { public *; }
-keep public class ru.voboost.components.screen.Screen { public *; }
-keep public class ru.voboost.components.panel.Panel { public *; }
-keep public class ru.voboost.components.section.Section { public *; }
-keep public class ru.voboost.components.tabs.Tabs { public *; }
-keep public class ru.voboost.components.tabs.TabItem { public *; }
-keep public class ru.voboost.components.text.Text { public *; }
-keep public class ru.voboost.components.text.TextRole { public *; }
-keep public class ru.voboost.components.font.Font { public *; }
-keep public class ru.voboost.components.theme.Theme { public *; }
-keep public class ru.voboost.components.i18n.Language { public *; }

# Keep View constructors (needed for XML inflation and programmatic creation)
-keepclasseswithmembers class ru.voboost.components.** extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

# Keep enums (needed for Theme and Language)
-keepclassmembers class ru.voboost.components.** extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations (needed for state restoration)
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ProGuard rules for demo-kotlin application

# Keep only entry points — R8 will trace and keep what's reachable
-keep public class ru.voboost.components.demo.kotlin.MainActivity { *; }
-keep public class ru.voboost.components.demo.shared.** { *; }

# Keep View constructors (needed for inflation)
-keepclasseswithmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

# Keep Kotlin metadata for reflection
-keep class kotlin.Metadata { *; }

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Suppress warnings for Compose (not used in demo-kotlin but may be referenced)
-dontwarn androidx.compose.**
-dontwarn androidx.activity.compose.**

# Aggressive optimizations
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''
-mergeinterfacesaggressively

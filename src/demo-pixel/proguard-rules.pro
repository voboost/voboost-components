# ProGuard rules for demo-pixel application

# Keep only entry points — R8 will trace and keep what's reachable
-keep public class ru.voboost.components.demo.pixel.MainActivity { *; }
-keep public class ru.voboost.components.demo.pixel.PixelContent { *; }

# Keep View constructors (needed for inflation)
-keepclasseswithmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Suppress warnings for Compose (not used in demo-pixel but may be referenced)
-dontwarn androidx.compose.**
-dontwarn androidx.activity.compose.**
-dontwarn androidx.appcompat.**
-dontwarn com.google.android.material.**
-dontwarn androidx.fragment.**
-dontwarn androidx.lifecycle.**

# Aggressive optimizations
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''
-mergeinterfacesaggressively

# ProGuard rules for demo-java application

# Keep only entry points — R8 will trace and keep what's reachable
-keep public class ru.voboost.components.demo.java.MainActivity { *; }
-keep public class ru.voboost.components.demo.shared.** { *; }

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

# Suppress warnings only for libraries explicitly excluded from demo-java
# These are compileOnly dependencies in the main library that demo-java doesn't use
-dontwarn androidx.compose.**
-dontwarn androidx.activity.compose.**

# Aggressive optimizations
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''
-mergeinterfacesaggressively

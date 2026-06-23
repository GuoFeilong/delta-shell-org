# App: delta-helper-app
# Library modules also ship consumer-rules.pro (core-network, core-activation).
# keepRules/api-models.keep — API DTO keep (AGP merges src/main/keepRules/*.keep)

# --- Hilt / Dagger ---
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keepclasseswithmembers class * {
    @dagger.* <methods>;
    @javax.inject.* <methods>;
}
-keep class com.delta.helper.di.** { *; }
-keep class com.delta.helper.HelperApplication { *; }

# --- Kotlin ---
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.reflect.**

# --- Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.** {
    volatile <fields>;
}

# --- Compose (Material3 ships consumer rules; keep app entry only) ---
-keep class com.delta.helper.MainActivity { *; }

# --- Overlay service ---
-keep class com.delta.helper.overlay.** { *; }

# --- BuildConfig / activation app context ---
-keep class com.delta.helper.BuildConfig { *; }
-keep class com.delta.helper.activation.** { *; }
-keep class com.delta.helper.config.** { *; }

# --- DataStore ---
-keep class androidx.datastore.*.** { *; }

# --- Play Services App Set (device id) ---
-keep class com.google.android.gms.appset.** { *; }
-dontwarn com.google.android.gms.**

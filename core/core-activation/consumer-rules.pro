# Activation Retrofit APIs
-keep,allowobfuscation interface com.delta.core.activation.api.** { *; }

# Activation request/response DTOs
-keep @kotlinx.serialization.Serializable class com.delta.core.activation.model.dto.** { *; }
-keep class com.delta.core.activation.model.dto.**$$serializer { *; }

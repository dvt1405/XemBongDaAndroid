# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
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
-dontwarn com.google.protobuf.java_com_google_ads_interactivemedia_v3__sdk_1p_binary_b0308732GeneratedExtensionRegistryLite$Loader
-keep class cn.pedant.SweetAlert.** {*; }
-keep class com.kt.apps.xembongda.model.football.** {*; }
-keep class com.kt.apps.xembongda.model.authenticate.** {*; }
-keep class com.kt.apps.xembongda.model.comments.** {*; }
-keep class com.kt.apps.xembongda.model.UserDTO {*; }
-keep class com.noqoush.adfalcon.android.sdk.** {*;}
-keep class com.google.ads.mediation.adfalcon.** {*;}
-keep public class com.google.android.gms.ads.** {
 public *;
}
-keep public class com.google.ads.** {
 public *;
}

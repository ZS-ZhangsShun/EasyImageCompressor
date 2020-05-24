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
#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
#避免混淆泛型 如果混淆报错建议关掉
-keepattributes Signature

#---------------------------------2.第三方包-------------------------------
#-dontwarn org.codehaus.jackson.**

# okhttp
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }
#-dontwarn okhttp3.**

# Retrofit
#-keep class retrofit2.** { *; }
#-dontwarn retrofit2.**
#-keepattributes Signature
#-keepattributes Exceptions
#-dontwarn javax.annotation.**
#
#-keep class okio.** { *; }
#-keep class org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#-keep class java.nio.file.** { *; }
#-dontwarn okio.**

# banner 的混淆代码
#-keep class com.youth.banner.** {
#    *;
# }

 #ARouter
# -keep public class com.alibaba.android.arouter.routes.**{*;}
# -keep public class com.alibaba.android.arouter.facade.**{*;}
# -keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

#glide#
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.AppGlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}

#-dontwarn com.alibaba.fastjson.**
#
#-keep class com.alibaba.fastjson.** { *; }
#-keep public class com.xxx.xxx.bean.** {
#
#public void set(**);
#
#public *** get*();
#
#public *** is*();
#
#}

#Umeng
#-keep class com.umeng.** {*;}
#-keepclassmembers class * {
#   public <init> (org.json.JSONObject);
#}
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
#-keep public class com.wbb.broadcasr.R$*{
#public static final int *;
#}

-dontwarn  org.apache.http.**
-keep class org.apache.http.**{ *;}
-dontwarn  com.loopj.android.http.**
-keep class  com.loopj.android.http.**{ *;}
-dontwarn  com.wecaresoftware.elfone.libcloudclient.**
-keep class com.wecaresoftware.elfone.libcloudclient.**{ *;}

-dontwarn sun.misc.**
-keep class sun.misc.**{ *;}

#---------------------------------默认保留区---------------------------------

###-----------基本配置-不能被混淆的------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.app.Notification

#support.v4/v7包不混淆
-keep class android.support.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v7.**
-keep interface android.support.v7.app.** { *; }
-dontwarn android.support.**    # 忽略警告
#保持注解继承类不混淆
-keep class * extends java.lang.annotation.Annotation {*;}

#保持Serializable实现类不被混淆
-keepnames class * implements java.io.Serializable

#保持Serializable不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举enum类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#自定义组件不被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}
#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
#StartOtherApp 反射#
-keep class com.wecaresoftware.elfone.libstartotherapp.rom.** { *; }

-keep class com.zs.easy.imgcompress.** {*;}
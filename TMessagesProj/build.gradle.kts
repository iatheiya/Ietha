plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.googleServices)
}
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
repositories {
  mavenCentral()
  google()
}
configurations {
  "compile".exclude(group = "com.google.firebase", module = "firebase-core")
  "compile".exclude(group = "androidx.recyclerview", module = "recyclerview")
}
dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.palette)
  implementation(libs.androidx.exifinterface)
  implementation(libs.androidx.dynamicanimation)
  implementation(libs.androidx.sharetarget)
  implementation(libs.androidx.interpolator)
  implementation(libs.androidx.biometric)
  implementation(libs.gms.play.services.cast.framework)
  implementation(libs.androidx.mediarouter)
  implementation(libs.nanohttpd)
  compileOnly(libs.checker.qual)
  compileOnly(libs.checker.compat.qual)
  implementation(libs.firebase.messaging)
  implementation(libs.firebase.config)
  implementation(libs.firebase.datatransport)
  implementation(libs.firebase.appindexing)
  implementation(libs.play.services.maps)
  implementation(libs.play.services.auth)
  implementation(libs.play.services.vision)
  implementation(libs.play.services.wearable)
  implementation(libs.play.services.location)
  implementation(libs.play.services.wallet)
  implementation(libs.mp4parser.isoparser)
  implementation(libs.stripe.android)
  implementation(libs.mlkit.language.id)
  implementation(libs.billing)
  implementation(libs.gson)
  implementation(libs.guava)
  implementation(libs.play.integrity)
  implementation(libs.play.services.safetynet)
  implementation(libs.play.services.mlkit.subject.segmentation)
  implementation(libs.play.services.mlkit.image.labeling)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services.auth)
  constraints {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0") {
      because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
    }
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0") {
      because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
    }
  }
  implementation(libs.recaptcha)
  coreLibraryDesugaring(libs.desugar.jdk.libs)
}
val isWindows = DefaultNativePlatform.getCurrentOperatingSystem().toFamilyName() == OperatingSystemFamily.WINDOWS
android {
  compileSdk = 35
  buildToolsVersion = "35.0.0"
  ndkVersion = "21.4.7075529"
  defaultConfig {
    minSdk = 21
    targetSdk = 35
    vectorDrawables.generatedDensities = listOf("mdpi", "hdpi", "xhdpi", "xxhdpi")
    multiDexEnabled = true
    externalNativeBuild {
      cmake {
        version = "3.10.2"
        arguments.addAll(listOf("-DANDROID_STL=c++_static", "-DANDROID_PLATFORM=android-21"))
      }
    }
  }
  buildFeatures {
    buildConfig = true
  }
  sourceSets {
    getByName("main").jniLibs.srcDirs = listOf("./jni/")
  }
  externalNativeBuild {
    cmake {
      path = file("jni/CMakeLists.txt")
    }
  }
  lint {
    disable += listOf("MissingTranslation", "ExtraTranslation", "BlockedPrivateApi")
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    isCoreLibraryDesugaringEnabled = true
  }
  buildTypes {
    getByName("debug") {
      isJniDebuggable = true
      isMinifyEnabled = false
      isShrinkResources = false
      isMultiDexEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "../TMessagesProj/proguard-rules.pro",
        "../TMessagesProj/proguard-rules-beta.pro"
      )
      ndk.debugSymbolLevel = "FULL"
      buildConfigField("String", "BUILD_VERSION_STRING", "\"$APP_VERSION_NAME\"")
      buildConfigField("String", "APP_CENTER_HASH", "\"\"")
      buildConfigField("String", "BETA_URL", getProps("BETA_PRIVATE_URL"))
      buildConfigField("boolean", "DEBUG_VERSION", "true")
      buildConfigField("boolean", "DEBUG_PRIVATE_VERSION", "true")
      buildConfigField("boolean", "BUILD_HOST_IS_WINDOWS", isWindows.toString())
      buildConfigField("int", "VERSION_NUM", "0")
    }
    create("HA_private") {
      isJniDebuggable = false
      isMinifyEnabled = true
      isMultiDexEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "../TMessagesProj/proguard-rules.pro",
        "../TMessagesProj/proguard-rules-beta.pro"
      )
      ndk.debugSymbolLevel = "FULL"
      buildConfigField("String", "BUILD_VERSION_STRING", "\"$APP_VERSION_NAME\"")
      buildConfigField("String", "APP_CENTER_HASH", getProps("APP_CENTER_HASH_PRIVATE"))
      buildConfigField("String", "BETA_URL", getProps("BETA_PRIVATE_URL"))
      buildConfigField("boolean", "DEBUG_VERSION", "true")
      buildConfigField("boolean", "DEBUG_PRIVATE_VERSION", "true")
      buildConfigField("boolean", "BUILD_HOST_IS_WINDOWS", isWindows.toString())
      buildConfigField("int", "VERSION_NUM", "1")
    }
    create("HA_public") {
      isJniDebuggable = false
      isMinifyEnabled = true
      isMultiDexEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "../TMessagesProj/proguard-rules.pro",
        "../TMessagesProj/proguard-rules-beta.pro"
      )
      ndk.debugSymbolLevel = "FULL"
      buildConfigField("String", "BUILD_VERSION_STRING", "\"$APP_VERSION_NAME\"")
      buildConfigField("String", "APP_CENTER_HASH", getProps("APP_CENTER_HASH_PUBLIC"))
      buildConfigField("String", "BETA_URL", getProps("BETA_PUBLIC_URL"))
      buildConfigField("boolean", "DEBUG_VERSION", "true")
      buildConfigField("boolean", "DEBUG_PRIVATE_VERSION", "false")
      buildConfigField("boolean", "BUILD_HOST_IS_WINDOWS", isWindows.toString())
      buildConfigField("int", "VERSION_NUM", "4")
    }
    create("HA_hardcore") {
      isJniDebuggable = false
      isMinifyEnabled = true
      isMultiDexEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "../TMessagesProj/proguard-rules.pro",
        "../TMessagesProj/proguard-rules-beta.pro"
      )
      ndk.debugSymbolLevel = "FULL"
      buildConfigField("String", "BUILD_VERSION_STRING", "\"$APP_VERSION_NAME\"")
      buildConfigField("String", "APP_CENTER_HASH", getProps("APP_CENTER_HASH_HARDCORE"))
      buildConfigField("String", "BETA_URL", getProps("APP_HARDCORE_URL"))
      buildConfigField("boolean", "DEBUG_VERSION", "true")
      buildConfigField("boolean", "DEBUG_PRIVATE_VERSION", "true")
      buildConfigField("boolean", "BUILD_HOST_IS_WINDOWS", isWindows.toString())
      buildConfigField("int", "VERSION_NUM", "5")
    }
    create("standalone") {
      isJniDebuggable = false
      isMinifyEnabled = true
      isMultiDexEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "../TMessagesProj/proguard-rules.pro")
      ndk.debugSymbolLevel = "FULL"
      buildConfigField("String", "BUILD_VERSION_STRING", "\"$APP_VERSION_NAME\"")
      buildConfigField("String", "APP_CENTER_HASH", "\"\"")
      buildConfigField("String", "BETA_URL", "\"\"")
      buildConfigField("boolean", "DEBUG_VERSION", "false")
      buildConfigField("boolean", "DEBUG_PRIVATE_VERSION", "false")
      buildConfigField("boolean", "BUILD_HOST_IS_WINDOWS", isWindows.toString())
      buildConfigField("int", "VERSION_NUM", "6")
    }
    getByName("release") {
      isJniDebuggable = false
      isMinifyEnabled = true
      isShrinkResources = false
      isMultiDexEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "../TMessagesProj/proguard-rules.pro")
      ndk.debugSymbolLevel = "FULL"
      buildConfigField("String", "BUILD_VERSION_STRING", "\"$APP_VERSION_NAME\"")
      buildConfigField("String", "APP_CENTER_HASH", "\"\"")
      buildConfigField("String", "BETA_URL", "\"\"")
      buildConfigField("boolean", "DEBUG_VERSION", "false")
      buildConfigField("boolean", "DEBUG_PRIVATE_VERSION", "false")
      buildConfigField("boolean", "BUILD_HOST_IS_WINDOWS", isWindows.toString())
      buildConfigField("int", "VERSION_NUM", "7")
    }
  }
  namespace = "org.telegram.messenger"
}
fun getProps(propName: String): String {
  val propsFile = rootProject.file("local.properties")
  if (propsFile.exists()) {
    val props = Properties()
    props.load(FileInputStream(propsFile))
    return props.getProperty(propName) ?: ""
  }
  return ""
}
tasks.register("checkVisibility") {
  doFirst {
    val isPrivateBuild =
      gradle.startParameter.taskNames.any { it.contains("HA_private") || it.contains("HA_hardcore") || it.contains("Debug") || it.contains("Release") }
    val isPublicAllowed = !project.hasProperty("IS_PRIVATE") || !(project.property("IS_PRIVATE") as Boolean)
    if (!isPrivateBuild && !isPublicAllowed) {
      throw GradleException("Building public version of private code!")
    }
  }
  doLast {
    if (gradle.startParameter.taskNames.any { it.contains("HA_public") }) {
      val privateBuild = file("${projectDir}_AppHockeyApp/afat/HA_private/Telegram-Beta.apk")
      if (privateBuild.exists()) {
        privateBuild.delete()
      }
    }
  }
}
tasks.named("preBuild") {
  dependsOn("checkVisibility")
}

plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.googleServices)
  alias(libs.plugins.firebaseCrashlytics)
}
configurations {
  "compile".exclude(group = "com.google.firebase", module = "firebase-core")
  "compile".exclude(group = "androidx.recyclerview", module = "recyclerview")
}
dependencies {
  implementation(project(":TMessagesProj"))
  implementation(libs.androidx.fragment)
  implementation(libs.androidx.core.ktx)
  coreLibraryDesugaring(libs.desugar.jdk.libs)
  implementation(files("../TMessagesProj/libs/libgsaverification-client.aar"))
  implementation(libs.appcenter.distribute)
  implementation(libs.appcenter.crashes)
  implementation(libs.appcenter.analytics)
  implementation(platform("com.google.firebase:firebase-bom:33.5.0"))
  implementation("com.google.firebase:firebase-crashlytics")
}
android {
  compileSdk = 35
  buildToolsVersion = "35.0.0"
  defaultConfig {
    applicationId = APP_PACKAGE
    minSdk = 21
    targetSdk = 35
    versionCode = Integer.parseInt(APP_VERSION_CODE)
    versionName = APP_VERSION_NAME
    ndkVersion = "21.4.7075529"
    multiDexEnabled = true
    vectorDrawables {
      generatedDensities.addAll(listOf("mdpi", "hdpi", "xhdpi", "xxhdpi"))
    }
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
  namespace = "org.telegram.messenger.regular"
  lint {
    checkReleaseBuilds = false
  }
  sourceSets {
    getByName("main").jniLibs.srcDirs = listOf("../TMessagesProj/jni/")
  }
  lint {
    disable += listOf("MissingTranslation", "ExtraTranslation", "BlockedPrivateApi")
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    isCoreLibraryDesugaringEnabled = true
  }
  signingConfigs {
    getByName("debug") {
      storeFile = file("../TMessagesProj/config/release.keystore")
      storePassword = RELEASE_STORE_PASSWORD
      keyAlias = RELEASE_KEY_ALIAS
      keyPassword = RELEASE_KEY_PASSWORD
    }
    create("release") {
      storeFile = file("../TMessagesProj/config/release.keystore")
      storePassword = RELEASE_STORE_PASSWORD
      keyAlias = RELEASE_KEY_ALIAS
      keyPassword = RELEASE_KEY_PASSWORD
    }
  }
  buildTypes {
    create("HA_private") {
      isDebuggable = false
      isJniDebuggable = false
      signingConfig = signingConfigs.getByName("debug")
      applicationIdSuffix = ".beta"
      isMinifyEnabled = true
      isMultiDexEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "../TMessagesProj/proguard-rules.pro",
        "../TMessagesProj/proguard-rules-beta.pro"
      )
      ndk.debugSymbolLevel = "FULL"
    }
    create("HA_public") {
      isDebuggable = false
      isJniDebuggable = false
      signingConfig = signingConfigs.getByName("debug")
      applicationIdSuffix = ".beta"
      isMinifyEnabled = true
      isMultiDexEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "../TMessagesProj/proguard-rules.pro",
        "../TMessagesProj/proguard-rules-beta.pro"
      )
      ndk.debugSymbolLevel = "FULL"
    }
    create("HA_hardcore") {
      isDebuggable = false
      isJniDebuggable = false
      signingConfig = signingConfigs.getByName("debug")
      applicationIdSuffix = ".beta"
      isMinifyEnabled = true
      isMultiDexEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "../TMessagesProj/proguard-rules.pro",
        "../TMessagesProj/proguard-rules-beta.pro"
      )
      ndk.debugSymbolLevel = "FULL"
    }
  }
  create("HA_private").sourceSets {
    manifest.srcFile = "../TMessagesProj/config/debug/AndroidManifest.xml"
  }
  create("HA_public").sourceSets {
    manifest.srcFile = "../TMessagesProj/config/debug/AndroidManifest.xml"
  }
  create("HA_hardcore").sourceSets {
    manifest.srcFile = "../TMessagesProj/config/debug/AndroidManifest.xml"
  }
  flavorDimensions.add("minApi")
  productFlavors {
    create("bundleAfat") {
      ndk {
        abiFilters = listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
      }
      dimension = "minApi"
      extensions {
        create("abiVersionCode") {
          set("1")
        }
      }
    }
    create("bundleAfat_SDK23") {
      ndk {
        abiFilters = listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
      }
      minSdk = 23
      dimension = "minApi"
      extensions {
        create("abiVersionCode") {
          set("2")
        }
      }
    }
    create("afat") {
      ndk {
        abiFilters = listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
      }
      dimension = "minApi"
      extensions {
        create("abiVersionCode") {
          set("9")
        }
      }
    }
  }
  applicationVariants.all {
    outputs.all {
      outputFileName = "Telegram-Beta.apk"
      versionCodeOverride = defaultConfig.versionCode * 10 + productFlavors[0].extensions["abiVersionCode"] as Int
    }
  }
  variantFilter {
    val names = flavors.map { it.name }
    if (buildType.name != "release" && !names.contains("afat")) {
      ignore = true
    }
  }
}

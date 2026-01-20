plugins {
  alias(libs.plugins.androidApplication)
}

repositories {
  mavenCentral()
  google()
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
    vectorDrawables.generatedDensities = listOf("mdpi", "hdpi", "xhdpi", "xxhdpi")

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

  namespace = "org.telegram.messenger.web"

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
    getByName("debug") {
      isDebuggable = true
      isJniDebuggable = true
      signingConfig = signingConfigs.getByName("debug")
      applicationIdSuffix = ".web"
      isMinifyEnabled = false
      isMultiDexEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "../TMessagesProj/proguard-rules.pro",
        "../TMessagesProj/proguard-rules-beta.pro"
      )
      ndk.debugSymbolLevel = "FULL"
    }
    create("standalone") {
      isDebuggable = false
      isJniDebuggable = false
      signingConfig = signingConfigs.getByName("release")
      applicationIdSuffix = ".web"
      isMinifyEnabled = true
      isMultiDexEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android.txt"), "../TMessagesProj/proguard-rules.pro")
      ndk.debugSymbolLevel = "FULL"
    }
  }

  getByName("debug").sourceSets {
    manifest.srcFile = "../TMessagesProj/config/release/AndroidManifest_standalone.xml"
  }

  create("standalone").sourceSets {
    manifest.srcFile = "../TMessagesProj/config/release/AndroidManifest_standalone.xml"
  }

  flavorDimensions.add("minApi")
  productFlavors {
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
      getByName("standalone").sourceSets {
        manifest.srcFile = "../TMessagesProj/config/release/AndroidManifest_standalone.xml"
      }
    }
  }

  applicationVariants.all {
    outputs.all {
      outputFileName = "app.apk"
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

apply(plugin = "com.google.gms.google-services")

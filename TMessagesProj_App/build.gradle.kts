plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

repositories {
    mavenCentral()
    google()
}

configurations {
    all {
        exclude(group = "com.google.firebase", module = "firebase-core")
        exclude(group = "androidx.recyclerview", module = "recyclerview")
    }
}

dependencies {
    implementation(project(":TMessagesProj"))
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(files("../TMessagesProj/libs/libgsaverification-client.aar"))
}

val APP_PACKAGE: String = findProperty("APP_PACKAGE")?.toString() ?: "org.telegram.messenger.regular"
val APP_VERSION_CODE: String = findProperty("APP_VERSION_CODE")?.toString() ?: "1"
val APP_VERSION_NAME: String = findProperty("APP_VERSION_NAME")?.toString() ?: "1.0.0"

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
            generatedDensities?.addAll(listOf("mdpi", "hdpi", "xhdpi", "xxhdpi"))
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
        disable += listOf("MissingTranslation", "ExtraTranslation", "BlockedPrivateApi")
    }

    sourceSets {
        getByName("main") {
            jniLibs.setSrcDirs(listOf("../TMessagesProj/jni/"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../TMessagesProj/config/release.keystore")
            storePassword = findProperty("RELEASE_STORE_PASSWORD")?.toString() ?: ""
            keyAlias = findProperty("RELEASE_KEY_ALIAS")?.toString() ?: ""
            keyPassword = findProperty("RELEASE_KEY_PASSWORD")?.toString() ?: ""
        }
        create("release") {
            storeFile = file("../TMessagesProj/config/release.keystore")
            storePassword = findProperty("RELEASE_STORE_PASSWORD")?.toString() ?: ""
            keyAlias = findProperty("RELEASE_KEY_ALIAS")?.toString() ?: ""
            keyPassword = findProperty("RELEASE_KEY_PASSWORD")?.toString() ?: ""
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isJniDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".beta"
            isMinifyEnabled = false
            isShrinkResources = false
            isMultiDexEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "../TMessagesProj/proguard-rules.pro",
                "../TMessagesProj/proguard-rules-beta.pro"
            )
            ndk.debugSymbolLevel = "FULL"
            sourceSets {
                getByName("debug") {
                    manifest.srcFile("../TMessagesProj/config/debug/AndroidManifest.xml")
                }
            }
        }

        create("standalone") {
            isDebuggable = false
            isJniDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            applicationIdSuffix = ".web"
            isMinifyEnabled = true
            isShrinkResources = false
            isMultiDexEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "../TMessagesProj/proguard-rules.pro"
            )
            ndk.debugSymbolLevel = "FULL"
            sourceSets {
                getByName("standalone") {
                    manifest.srcFile("../TMessagesProj/config/release/AndroidManifest.xml")
                }
            }
        }

        getByName("release") {
            isDebuggable = false
            isJniDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = false
            isMultiDexEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "../TMessagesProj/proguard-rules.pro"
            )
            ndk.debugSymbolLevel = "FULL"
            sourceSets {
                getByName("release") {
                    manifest.srcFile("../TMessagesProj/config/release/AndroidManifest.xml")
                }
            }
        }
    }

    flavorDimensions.add("minApi")

    productFlavors {
        create("bundleAfat") {
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
            }
            dimension = "minApi"
            ext["abiVersionCode"] = 1
        }

        create("bundleAfat_SDK23") {
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
            }
            minSdk = 23
            dimension = "minApi"
            ext["abiVersionCode"] = 2
            val sourceSetConfigure = {
                manifest.srcFile("../TMessagesProj/config/debug/AndroidManifest_SDK23.xml")
            }
        }

        create("afat") {
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
            }
            dimension = "minApi"
            ext["abiVersionCode"] = 9
        }
    }

    applicationVariants.all {
        val variant = this
        outputs.all {
            val output = this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl
            if (output != null) {
                output.outputFileName = "app.apk"
                val abiVersionCode = variant.productFlavors[0].ext["abiVersionCode"] as? Int ?: 0
                output.versionCodeOverride = defaultConfig.versionCode!! * 10 + abiVersionCode
            }
        }
    }

    variantFilter {
        val names = flavors.map { it.name }
        if (buildType.name != "release" && !names.contains("afat")) {
            ignore = true
        }
    }
}
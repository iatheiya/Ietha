plugins {
    alias(libs.plugins.androidLibrary)
}

import java.util.Properties
import java.io.FileInputStream

configurations {
    all {
        exclude(group = "com.google.firebase", module = "firebase-core")
        exclude(group = "androidx.recyclerview", module = "recyclerview")
    }
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

val osName = System.getProperty("os.name") ?: ""
val isWindows = osName.lowercase().contains("win")
val APP_VERSION_NAME: String = findProperty("APP_VERSION_NAME")?.toString() ?: "1.0.0"

android {
    compileSdk = 35
    buildToolsVersion = "35.0.0"
    ndkVersion = "21.4.7075529"

    fun com.android.build.api.dsl.BuildType.applyCommon(
        minify: Boolean,
        versionNum: Int,
        appCenterHash: String?,
        betaUrl: String?,
        debugVersionVal: Boolean,
        debugPrivateVal: Boolean,
        jniDebugVal: Boolean,
        useFullProguardList: Boolean = true
    ) {
        isMinifyEnabled = minify
        try {
            ndk.debugSymbolLevel = "FULL"
        } catch (_: Throwable) {
        }
        buildConfigField("String", "BUILD_VERSION_STRING", "\"$APP_VERSION_NAME\"")
        val appCenterLiteral = appCenterHash?.let { "\"$it\"" } ?: "\"\""
        val betaUrlLiteral = betaUrl?.let { "\"$it\"" } ?: "\"\""

        buildConfigField("String", "APP_CENTER_HASH", appCenterLiteral)
        buildConfigField("String", "BETA_URL", betaUrlLiteral)
        buildConfigField("boolean", "DEBUG_VERSION", debugVersionVal.toString())
        buildConfigField("boolean", "DEBUG_PRIVATE_VERSION", debugPrivateVal.toString())
        buildConfigField("boolean", "BUILD_HOST_IS_WINDOWS", isWindows.toString())
        buildConfigField("int", "VERSION_NUM", versionNum.toString())

        isJniDebuggable = jniDebugVal

        val proguardList = mutableListOf(getDefaultProguardFile("proguard-android-optimize.txt"))
        if (useFullProguardList) {
            proguardList.add(file("../TMessagesProj/proguard-rules.pro"))
            proguardList.add(file("../TMessagesProj/proguard-rules-beta.pro"))
        } else {
            proguardList.add(file("../TMessagesProj/proguard-rules.pro"))
        }
        proguardFiles(*proguardList.toTypedArray())
    }

    defaultConfig {
        minSdk = 21
        vectorDrawables {
            generatedDensities?.addAll(listOf("mdpi", "hdpi", "xhdpi", "xxhdpi"))
        }

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
        getByName("main") {
            jniLibs.srcDir("jni")
        }
    }

    externalNativeBuild {
        cmake { path = file("jni/CMakeLists.txt") }
    }

    lint { disable.addAll(listOf("MissingTranslation", "ExtraTranslation", "BlockedPrivateApi")) }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }

    buildTypes {
        getByName("debug") {
            applyCommon(
                minify = false,
                versionNum = 0,
                appCenterHash = "",
                betaUrl = getProps("BETA_PRIVATE_URL"),
                debugVersionVal = true,
                debugPrivateVal = true,
                jniDebugVal = true,
                useFullProguardList = true
            )
        }
        create("HA_private") {
            applyCommon(
                minify = true,
                versionNum = 1,
                appCenterHash = getProps("APP_CENTER_HASH_PRIVATE"),
                betaUrl = getProps("BETA_PRIVATE_URL"),
                debugVersionVal = true,
                debugPrivateVal = true,
                jniDebugVal = false,
                useFullProguardList = true
            )
        }
        create("HA_public") {
            applyCommon(
                minify = true,
                versionNum = 4,
                appCenterHash = getProps("APP_CENTER_HASH_PUBLIC"),
                betaUrl = getProps("APP_PUBLIC_URL"),
                debugVersionVal = true,
                debugPrivateVal = false,
                jniDebugVal = false,
                useFullProguardList = true
            )
        }
        create("HA_hardcore") {
            applyCommon(
                minify = true,
                versionNum = 5,
                appCenterHash = getProps("APP_CENTER_HASH_HARDCORE"),
                betaUrl = getProps("APP_HARDCORE_URL"),
                debugVersionVal = true,
                debugPrivateVal = true,
                jniDebugVal = false,
                useFullProguardList = true
            )
        }
        create("standalone") {
            applyCommon(
                minify = true,
                versionNum = 6,
                appCenterHash = "",
                betaUrl = "",
                debugVersionVal = false,
                debugPrivateVal = false,
                jniDebugVal = false,
                useFullProguardList = false
            )
        }
        getByName("release") {
            applyCommon(
                minify = true,
                versionNum = 7,
                appCenterHash = "",
                betaUrl = "",
                debugVersionVal = false,
                debugPrivateVal = false,
                jniDebugVal = false,
                useFullProguardList = false
            )
            isShrinkResources = false
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
        val isPrivateBuild = gradle.startParameter.taskNames.any {
            it.contains("HA_private") || it.contains("HA_hardcore") || it.contains("Debug") || it.contains("Release")
        }
        val isPublicAllowed = !project.hasProperty("IS_PRIVATE") || project.property("IS_PRIVATE").toString().toBoolean().not()
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

pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://developer.huawei.com/repo/") }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.huawei.agconnect") {
                useModule("com.huawei.agconnect:agcp:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
}

rootProject.name = "Ietha"

include(
    ":TMessagesProj",
    ":TMessagesProj_App",
    ":TMessagesProj_AppHuawei",
    ":TMessagesProj_AppHockeyApp",
    ":TMessagesProj_AppStandalone",
    ":TMessagesProj_AppTests"
)
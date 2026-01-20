pluginManagement {
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://developer.huawei.com/repo/") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
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

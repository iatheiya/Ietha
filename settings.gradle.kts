pluginManagement {
  repositories {
    mavenCentral()
    google()
    maven { url = uri("https://developer.huawei.com/repo/") }
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    google()
    maven { url = uri("https://developer.huawei.com/repo/") }
  }
  versionCatalogs {
    create("libs") {
      from(files("libs.versions.toml"))
    }
  }
}

rootProject.name = "Ietha"
include(":TMessagesProj")
include(":TMessagesProj_App")
include(":TMessagesProj_AppHuawei")
include(":TMessagesProj_AppHockeyApp")
include(":TMessagesProj_AppStandalone")
include(":TMessagesProj_AppTests")

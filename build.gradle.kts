plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.googleServices)
  alias(libs.plugins.firebaseCrashlytics)
}

repositories {
  mavenCentral()
  google()
  maven { url = uri("https://developer.huawei.com/repo/") }
}

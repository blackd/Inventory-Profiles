pluginManagement {
  repositories {
    maven(url = "https://maven.fabricmc.net") {
      name = "Fabric"
    }
    gradlePluginPortal()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    google() // for proguard 7.0.0

    maven(url = "https://maven.fabricmc.net/") {
      name = "Fabric"
    }
  }
}
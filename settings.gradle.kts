pluginManagement {
  repositories {
    jcenter()
    gradlePluginPortal()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")

    maven(url = "https://maven.fabricmc.net/") {
      name = "Fabric"
    }
  }
}
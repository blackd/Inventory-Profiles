pluginManagement {
  repositories {
    jcenter()
    gradlePluginPortal()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")

    maven("https://files.minecraftforge.net/maven")
  }

  resolutionStrategy {
    eachPlugin {
      // ref: https://github.com/MinecraftForge/ForgeGradle/issues/439
      // version: https://files.minecraftforge.net/maven/net/minecraftforge/gradle/ForgeGradle/
      if (requested.id.id == "net.minecraftforge.gradle") {
        useModule("net.minecraftforge.gradle:ForgeGradle:3.0.179")
      }
    }
  }
}
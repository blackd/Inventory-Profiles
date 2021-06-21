pluginManagement {
  repositories {
    maven("https://files.minecraftforge.net/maven")
    jcenter()
    gradlePluginPortal()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")

    google() // for proguard 7.0.0


  }

  resolutionStrategy {
    eachPlugin {
      // ref: https://github.com/MinecraftForge/ForgeGradle/issues/439
      // version: https://files.minecraftforge.net/maven/net/minecraftforge/gradle/ForgeGradle/
      if (requested.id.id == "net.minecraftforge.gradle") {
        useModule("net.minecraftforge.gradle:ForgeGradle:4.1.7")
      }
    }
  }
}
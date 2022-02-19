
rootProject.name = "InventoryProfilesNext"
include("common")



include("platforms:fabric-1.15")
include("platforms:forge-1.15")

if (System.getenv("IPN_COMPILE_ALL") != null) {
    //include("platforms:fabric-1.14")
}


pluginManagement {
    repositories {
        maven(url = "https://maven.fabricmc.net") {
            name = "Fabric"
        }
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.4.1"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

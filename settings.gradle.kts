
rootProject.name = "InventoryProfilesNext"
include("common")


if (JavaVersion.current() >= JavaVersion.VERSION_16) {
    include("platforms:fabric-1.18")
    include("platforms:fabric-1.17")
    include("platforms:fabric-1.16")

    include("platforms:forge-1.17")
    include("platforms:forge-1.18")
}

include("platforms:forge-1.16")


if (System.getenv("IPN_COMPILE_ALL") != null) {
    //include("platforms:fabric-1.14")
    //include("platforms:fabric-1.15")
    //include("platforms:forge-1.15")
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

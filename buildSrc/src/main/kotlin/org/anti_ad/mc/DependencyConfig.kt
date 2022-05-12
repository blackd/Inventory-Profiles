package org.anti_ad.mc

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.dependencies

//var shadedApi: Configuration? = null

fun Project.configureDependencies() {
    apply(plugin = "kotlin")
    apply(plugin = "java")
    apply(plugin = "java-library")



    configurations {
        val shaded = create("shaded")
        val shadedApi = create("shadedApi")
        shaded.extendsFrom(shadedApi)
        getByName("api").extendsFrom(shadedApi)
        val shadedImplementation = create("shadedImplementation")
        shaded.extendsFrom(shadedImplementation)
        getByName("implementation").extendsFrom(shadedImplementation)
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.enginehub.org/repo/") }
        maven { url = uri("https://repo.codemc.org/repository/maven-public") }
        //maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
        //maven("https://raw.githubusercontent.com/TerraformersMC/Archive/main/releases")
        maven { url = uri("https://maven.terraformersmc.com/releases") }
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/releases/") }
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.shedaniel.me") }
    }

    dependencies {
        "api"("org.jetbrains:annotations:20.1.0")
    }
}

fun Project.fabricCommonDependency(minecraft_version: Any,
                                   mappings_version: Any,
                                   loader_version: Any,
                                   fabric_api_version: Any,
                                   modmenu_version: Any) {
    dependencies {

        "shadedApi"(project(":common"))
        "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
        "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-common:1.6.21")
        "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
        "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")

        "minecraft"("com.mojang:minecraft:$minecraft_version")
        "mappings"("net.fabricmc:yarn:$mappings_version:v2")

        "modImplementation"("net.fabricmc:fabric-loader:$loader_version")
        "modImplementation"("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
        "modImplementation"("com.terraformersmc:modmenu:$modmenu_version")
    }
}



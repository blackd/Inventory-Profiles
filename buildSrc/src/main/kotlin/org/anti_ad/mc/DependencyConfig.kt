package org.anti_ad.mc

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.dependencies

fun Project.configureDependencies() {
    apply(plugin = "kotlin")
    //    apply(plugin = "antlr")
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
        maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
        //maven("https://raw.githubusercontent.com/TerraformersMC/Archive/main/releases")
        maven { url = uri("https://maven.terraformersmc.com/releases") }
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/releases/") }
        maven { url = uri("https://maven.fabricmc.net/") }

    }

    dependencies {
        "api"("org.jetbrains:annotations:20.1.0")
    }
}

fun Project.addPostDeps() {
    buildscript {
        dependencies.classpath("com.guardsquare:proguard-gradle:7.1.1")
    }
}
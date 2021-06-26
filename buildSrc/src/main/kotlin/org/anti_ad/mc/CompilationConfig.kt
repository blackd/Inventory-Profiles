package org.anti_ad.mc

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.kotlin.dsl.*

import org.gradle.language.jvm.tasks.ProcessResources

fun Project.configureCompilation() {
    apply(plugin = "maven-publish")
    apply(plugin = "idea")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        doFirst {
            options.compilerArgs.add("-Xlint:all")
        }
    }



    tasks.withType<ProcessResources> {
        include("**/*")
        filesMatching(listOf("*.json", "*.txt")) {
            filter<org.apache.tools.ant.filters.ReplaceTokens>(
                "tokens" to mapOf(
                    "VERSION" to project.version.toString(),
                    "DESCRIPTION" to project.properties["ipnext.description"],
                    "WIKI" to project.properties["ipnext.docs"],
                    "SOURCE" to project.properties["ipnext.scm"],
                    "ISSUES" to project.properties["ipnext.tracker"],
                    "LICENSE" to project.properties["ipnext.license"]
                )
            )
        }
    }
    tasks.withType<Jar> {
        archiveBaseName.set("InventoryProfilesNext-${archiveBaseName.get()}")
        from("../LICENSE", "../../LICENSE")
    }
}
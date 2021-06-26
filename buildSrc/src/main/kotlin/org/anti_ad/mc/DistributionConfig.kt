package org.anti_ad.mc

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginConvention
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.kotlin.dsl.*

fun Project.configureDistribution() {
    apply(plugin = "java-library")
    apply(plugin = "com.github.johnrengelman.shadow")


    tasks.named<ShadowJar>("shadowJar") {

        configurations = listOf(project.configurations["shaded"])

        archiveClassifier.set("shaded")
        setVersion(project.version)
        relocate("org.antlr", "org.anti_ad.mc.common.embedded.org.antlr")
        relocate("org.apache.commons", "org.anti_ad.mc.common.embedded.commons")
        relocate("kotlin", "org.anti_ad.mc.common.embedded.kotlin")

        exclude("**/*.kotlin_metadata")
        exclude("**/*.kotlin_module")
        exclude("**/*.kotlin_builtins")
        exclude("**/*_ws.class") // fixme find a better solution for removing *.ws.kts
        exclude("**/*_ws$*.class")
        exclude("mappings/mappings.tiny") // before kt, build .jar don"t have this folder (this 500K thing)
        exclude("com/ibm/**")
        exclude("org/glassfish/**")
        exclude("org/intellij/**")
        exclude("org/jetbrains/**")
        minimize()
    }
    convention.getPlugin<BasePluginConvention>().archivesBaseName = project.name

    tasks.named<DefaultTask>("build") {
        dependsOn(tasks["shadowJar"])
    }

}

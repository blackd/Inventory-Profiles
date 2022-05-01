package org.anti_ad.mc

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension

import org.gradle.kotlin.dsl.*

fun Project.configureDistribution(is18: Boolean) {
    apply(plugin = "java-library")
    apply(plugin = "com.github.johnrengelman.shadow")


    tasks.named<ShadowJar>("shadowJar") {

        configurations = listOf(project.configurations["shaded"])

        archiveClassifier.set("shaded")
        setVersion(project.version)

        relocate("org.antlr", "org.anti_ad.embedded.org.antlr")
        relocate("kotlin", "org.anti_ad.embedded.kotlin")
        relocate("kotlinx", "org.anti_ad.embedded.kotlinx")

        //include("assets/**")
        //include("org/anti_ad/mc/**")

        //exclude("**/*.kotlin_metadata")
        //exclude("**/*.kotlin_module")
        //exclude("**/*.kotlin_builtins")
        //exclude("**/*_ws.class") // fixme find a better solution for removing *.ws.kts
        //exclude("**/*_ws$*.class")
        exclude("**/*.stg")
        exclude("**/*.st")
        exclude("mappings/mappings.tiny") // before kt, build .jar don"t have this folder (this 500K thing)
        exclude("com/ibm/**")
        exclude("org/glassfish/**")
        exclude("org/intellij/**")
        exclude("org/jetbrains/**")
        exclude("org/jline/**")
        exclude("net/minecraftforge/**")
        exclude("io/netty/**")
        //exclude("mappings/mappings.tiny") // before kt, build .jar don"t have this folder (this 500K thing)
        exclude("META-INF/maven/**")
        exclude("META-INF/LICENSE")
        exclude("META-INF/README")

        minimize()
    }
    extensions.findByType(BasePluginExtension::class.java)?.archivesName?.set(project.name)
    //convention.getPlugin<BasePluginExtension>().archivesBaseName = project.name

    tasks.named<DefaultTask>("build") {
        dependsOn(tasks.findByPath(":common:build"))
        dependsOn(tasks["shadowJar"])
    }

}

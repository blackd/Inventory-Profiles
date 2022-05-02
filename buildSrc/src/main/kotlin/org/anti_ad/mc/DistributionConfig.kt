package org.anti_ad.mc

//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension

import org.gradle.kotlin.dsl.*

fun Project.configureDistribution(is18: Boolean) {
    apply(plugin = "java-library")
//    apply(plugin = "com.github.johnrengelman.shadow")


    extensions.findByType(BasePluginExtension::class.java)?.archivesName?.set(project.name)
    //convention.getPlugin<BasePluginExtension>().archivesBaseName = project.name

    tasks.named<DefaultTask>("build") {
        dependsOn(tasks.findByPath(":common:build"))
        dependsOn(tasks["shadowJar"])
    }

}

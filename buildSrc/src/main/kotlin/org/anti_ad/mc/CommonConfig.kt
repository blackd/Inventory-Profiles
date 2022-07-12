/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*

import java.io.ByteArrayOutputStream


fun Project.configureCommon(is18: Boolean = false) {
    configureDependencies()
    configureCompilation(is18)
    configureDistribution(is18)

    version = rootProject.version
}

fun Project.platformsCommonConfig() {
    tasks["javadoc"].enabled = false
}
fun Project.registerMinimizeJarTask() {
    tasks.register<DefaultTask>("minimizeJar") {

        val isForge = !project.name.startsWith("fabric")
        val taskName = if (isForge) { "shadowJar" } else { "remapJar" }
        val jarTask = project.tasks.named<org.gradle.jvm.tasks.Jar>(taskName)
        dependsOn(jarTask)
        if (isForge) {
            val endTask = project.tasks.named("reobfJar")
            dependsOn(endTask)
        }
        val jarFile = jarTask.get()
        val jarPath = project.layout.buildDirectory.file("libs/" + jarFile.archiveFileName.get())
        doLast {
            exec {
                this.workingDir = project.layout.projectDirectory.asFile
                val script = rootProject.layout.projectDirectory.file("optimize-jar.sh")
                this.executable = script.asFile.absolutePath
                this.args(jarPath.get().asFile.absolutePath, project.layout.buildDirectory.get().asFile.absolutePath)

            }
        }
    }
}

fun Project.forgeCommonAfterEvaluate(mod_loader: Any, minecraft_version: Any, mod_artefact_version: Any) {
    tasks.named<Task>("reobfJar") {
        val shadow = tasks.getByName("customJar");
        dependsOn(shadow)
        dependsOn(tasks["copyProGuardJar"])
        //input = shadow.archiveFile.orNull?.asFile
    }
    tasks.named<Task>("proguard") {
        val shadow = tasks.getByName<Task>("shadowJar");
        dependsOn(shadow)
    }

    val forgeRemapJar = tasks.named<org.gradle.jvm.tasks.Jar>("shadowJar").get()
    registerCopyJarForPublishTask(forgeRemapJar, mod_loader, minecraft_version, mod_artefact_version).get().dependsOn("shadowJar").dependsOn("reobfJar")

    tasks.named<DefaultTask>("build") {
        dependsOn("copyJavadoc")
        dependsOn("packageSources")
        dependsOn("copyJarForPublish")
//        dependsOn("minimizeJar")
    }
    rootAfterEvaluate()
}

fun Project.rootAfterEvaluate() {

    if (System.getenv("IPNEXT_RELEASE") == null) {
        val buildTasks = mutableListOf<Task>()

        rootProject.subprojects.filter { subProject ->
            subProject.name.contains("platforms:")
        }.forEach {
            it.tasks["build"]?.let { buildTask ->
                buildTasks.add(buildTask)
            }
        }

        rootProject.subprojects.forEach { p ->
            p.tasks.forEach {
                if (it.name == "minimizeJar") {
                    buildTasks.forEach { buildTask ->
                        buildTask.dependsOn(it)
                    }
                }
            }
        }
    }
    // one must disable parallel execution for this to work
    //val depTree = addTaskToDepTree(0,tasks["build"], mutableSetOf<String>())
    //logger.lifecycle(depTree)
}

fun Project.registerCopyJarForPublishTask(source: Jar, mod_loader: Any, minecraft_version: Any, mod_artefact_version: Any): TaskProvider<Copy> {
    return tasks.register<Copy>("copyJarForPublish") {
        from(source.archiveFile.get().asFile)
        into(layout.buildDirectory.dir("publish"))
        rename {
            "$mod_loader-$minecraft_version-$mod_artefact_version.jar"
        }

        logger.debug("will rename ${source.archiveFile.get().asFile} to $mod_loader-$minecraft_version-$mod_artefact_version.jar" )
    }
}
fun Project.fabricCommonAfterEvaluate(mod_loader: Any, minecraft_version: Any, mod_artefact_version: Any) {
    val remapped = tasks.named<Task>("remapJar")

    tasks.register<Copy>("injectCommonResources") {
        dependsOn(":common:processResources")
        from(project(":common").layout.buildDirectory.dir("resources/main"))
        include("assets/**")
        into(project.layout.buildDirectory.dir("resources/main"))
    }

    tasks.register<Delete>("removeCommonResources") {
        this.delete(project.layout.buildDirectory.dir("resources/main/assets"))
    }

    tasks.getByName("runClient") {
        dependsOn("injectCommonResources")
        finalizedBy("removeCommonResources")
    }

    val fabricRemapJar = tasks.named<org.gradle.jvm.tasks.Jar>("remapJar").get()
    registerCopyJarForPublishTask(fabricRemapJar,mod_loader, minecraft_version, mod_artefact_version).get().dependsOn(remapped)

    tasks.named<Task>("prepareRemapJar") {
        val proGuardTask = tasks.getByName<Task>("proguard")
        mustRunAfter(proGuardTask)
    }


    rootAfterEvaluate()
}

fun Project.fabricRegisterCommonTasks(mod_loader: Any, minecraft_version: Any, mod_artefact_version: Any) {

    tasks.register<Copy>("copyJavadoc") {
        dependsOn(":common:packageJavadoc")

        val javadocJar = project(":common").tasks.named<Jar>("packageJavadoc").get()
        from(javadocJar)
        into(layout.buildDirectory.dir("publish"))
        rename {
            "$mod_loader-$minecraft_version-$mod_artefact_version-javadoc.jar"
        }
        logger.debug("will rename ${javadocJar.archiveFile.get().asFile} to $mod_loader-$minecraft_version-$mod_artefact_version.jar" )
    }

    val prepareSourceJar = tasks.register<Copy>("prepareSourceJar") {
        dependsOn(":common:generateGrammarSource")
        dependsOn(":common:generateTestGrammarSource")
        val commonKotlinSources = project(":common").layout.projectDirectory.dir("src/main/java")
        val commonAntlrSources = project(":common").layout.projectDirectory.dir("src/main/java")
        val commonGeneratedSources = project(":common").layout.buildDirectory.dir("generated-src/antlr/main")
        val platformSources = layout.projectDirectory.dir("src/main/java")
        from(commonKotlinSources) {
            include("**/*.java")
            include("**/*.kt")
        }
        from(commonGeneratedSources) {
            include("**/*.java")
            include("**/*.tokens")
            include("**/*.interp")
        }
        from(commonAntlrSources) {
            include("**/*.g4")
        }
        from(platformSources) {
            include("**/*.java")
            include("**/*.kt")
        }
        into(layout.buildDirectory.dir("srcJarContent"))
    }

    tasks.register<Jar>("packageSources") {
        dependsOn("prepareSourceJar")
        archiveClassifier.set("sources")
        archiveBaseName.set("$mod_loader-$minecraft_version-$mod_artefact_version")
        archiveVersion.set("")
        destinationDirectory.set(layout.buildDirectory.dir("publish"))

        from(layout.buildDirectory.dir("srcJarContent"))

    }
}
private var gitVersionString: String = ""

fun Project.getGitHash(): String {
    if (gitVersionString.isNotEmpty()) {
        return gitVersionString
    }
    val stdout = ByteArrayOutputStream()
    val exitCode = exec {
        commandLine = mutableListOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
        this.isIgnoreExitValue = true
    }.exitValue
    return  if (exitCode == 0) {
        gitVersionString = stdout.toString().trim()
        gitVersionString

    } else {
        gitVersionString = "not-a-git-repo"
        gitVersionString
    }

}

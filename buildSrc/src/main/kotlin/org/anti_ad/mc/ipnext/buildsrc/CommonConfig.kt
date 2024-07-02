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

package org.anti_ad.mc.ipnext.buildsrc

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*

import java.io.ByteArrayOutputStream
import kotlin.io.path.div

fun Project.configureCommon() {
    configureDependencies()
    configureCompilation("InventoryProfilesNext")
    configureDistribution()

    version = rootProject.version
}

fun Project.platformsCommonConfig() {
    tasks["javadoc"].enabled = false
}
fun Project.registerMinimizeJarTask(): DefaultTask {
    val minTask = tasks.register<DefaultTask>("minimizeJar") {
        group = "build"

        val isForge = !project.name.startsWith("fabric")
        val taskName = if (isForge) { "shadowJar" } else { "remapJar" }
        val jarTask = project.tasks.named<org.gradle.jvm.tasks.Jar>(taskName)
        dependsOn(jarTask)
        if (isForge) {
            var endTask = project.tasks.named("proguard")
            dependsOn(endTask)
            endTask = project.tasks.named("customJar")
            dependsOn(endTask)

        }
        val jarFile = jarTask.get()
        val jarPath = project.layout.buildDirectory.file("libs/" + jarFile.archiveFileName.get())
        var outputFile = project.layout.buildDirectory.dir("optimized-mod").get().asFile.toPath() / jarFile.archiveFileName.get()
        doLast {
            exec {
                this.workingDir = project.layout.projectDirectory.asFile
                val script = rootProject.layout.projectDirectory.file("optimize-jar.sh")
                this.executable = script.asFile.absolutePath
                this.args(jarPath.get().asFile.absolutePath, project.layout.buildDirectory.get().asFile.absolutePath, outputFile)

            }
        }
        this.outputs.file(outputFile)
    }
    tasks.named("build").get().dependsOn(minTask)
    return minTask.get()
}

fun Project.forgeCommonAfterEvaluate(mod_loader: Any, minecraft_version: Any, mod_artefact_version: Any) {
/*
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
*/

    val forgeRemapJar = tasks.named<org.gradle.jvm.tasks.Jar>("shadowJar").get()
    registerCopyJarForPublishTask(forgeRemapJar, mod_loader, minecraft_version, mod_artefact_version).get().dependsOn("shadowJar") //.dependsOn("reobfJar")

    tasks.named("publishMavenPublicationToIpnOfficialRepoRepository")?.get()
        ?.dependsOn("build")
        ?.dependsOn("minimizeJar")
        ?.dependsOn("jar")
        ?.mustRunAfter("minimizeJar")
        ?.mustRunAfter("build") ?: logger.lifecycle("Can't find task 'publishMavenPublicationToIpnOfficialRepoRepository'")

    tasks.named("modrinth")?.get()
        ?.dependsOn("build")
        ?.dependsOn("minimizeJar")
        ?.dependsOn("deobfJar")
        ?.mustRunAfter("minimizeJar")
        ?.mustRunAfter("build") ?: logger.lifecycle("Can't find task 'modrinth'")
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

    val fabricRemapJar = tasks.named<org.gradle.jvm.tasks.Jar>("remapJar").get()
    registerCopyJarForPublishTask(fabricRemapJar,mod_loader, minecraft_version, mod_artefact_version).get().dependsOn(remapped)

    tasks.named("publishMavenPublicationToIpnOfficialRepoRepository")?.get()
        ?.dependsOn("build")
        ?.dependsOn("minimizeJar") ?: logger.error("Can't find task 'publishMavenPublicationToIpnOfficialRepoRepository'")

    tasks.named("modrinth")?.get()
        ?.dependsOn("build")
        ?.dependsOn("minimizeJar") ?: logger.error("Can't find task 'modrinth'")

    /*
        tasks.named<Task>("prepareRemapJar") {
            val proGuardTask = tasks.getByName<Task>("proguard")
            mustRunAfter(proGuardTask)
        }
    */

    rootAfterEvaluate()
}

fun Project.fabricRegisterCommonTasks(mod_loader: Any, minecraft_version: Any, mod_artefact_version: Any) {
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

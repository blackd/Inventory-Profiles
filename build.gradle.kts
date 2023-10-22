/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
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

import org.anti_ad.mc.ipnext.buildsrc.getGitHash
import org.anti_ad.mc.ipnext.buildsrc.loom_version
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

val versionObj = Version("1", "10", "8",
                         preRelease = (System.getenv("IPNEXT_RELEASE") == null))


repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
    maven(url = "https://maven.fabricmc.net") {
        name = "Fabric"
    }
    maven("https://maven.terraformersmc.com/releases")
    maven ("https://plugins.gradle.org/m2/")


}


plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"


    idea
    `java-library`
    `maven-publish`
    signing
    antlr
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false

    id("io.github.gradle-nexus.publish-plugin") version "1.1.0" apply true
    id("fabric-loom") version(org.anti_ad.mc.ipnext.buildsrc.loom_version) apply false
    id("com.matthewprenger.cursegradle") version "1.4.+" apply false
    id("com.modrinth.minotaur") version "2.+" apply false
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}





// This is here, but it looks like it's not inherited by the child projects
tasks.named<KotlinCompile>("compileKotlin") {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-opt-in=kotlin.ExperimentalStdlibApi")
    }
}

//group = "org.anti-ad.mc"

evaluationDependsOnChildren()

allprojects {
    version = versionObj.toCleanString()
//    group = "org.anti-ad.mc"
    ext.set("mod_artefact_version", versionObj.toCleanString())
    ext.set("mod_artefact_is_release", versionObj.isRelease())
    ext.set("libIPN_version", "4.0.0")

    tasks.withType<JavaCompile>().configureEach {
        options.isFork = true
        options.isIncremental = true
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            languageVersion = "1.8"
            jvmTarget = "17"
            freeCompilerArgs = mutableListOf("-opt-in=kotlin.ExperimentalStdlibApi", "-opt-in=kotlin.RequiresOptIn") + freeCompilerArgs
        }
        this.incremental = true
    }

}


tasks.named<Jar>("jar") {
    enabled = false
}


tasks.register("owner-testing-env") {
    onlyIf {
        System.getenv("IPNEXT_ITS_ME") != null
    }
    doLast {
        val bos = ByteArrayOutputStream()
        exec {
            workingDir = layout.projectDirectory.asFile.absoluteFile
            commandLine("${System.getenv("HOME")}/.local/bin/update-ipnext-test-env.sh",
                        project.layout.buildDirectory.dir("libs").get().asFile.absolutePath,
                        "-${versionObj.toCleanString()}")
            standardOutput = bos
        }
        logger.lifecycle(bos.toString())
    }
}

tasks.register<Copy>("copyPlatformJars") {
    subprojects.filter {
        val isFabric = it.name.startsWith("fabric")
        val isForge = it.name.startsWith("forge")
        isFabric || isForge
    }.forEach {
        //group = "org.anti-ad.mc.platforms"
        val isForge = !it.name.startsWith("fabric")
        val taskName = if (isForge) { "shadowJar" } else { "remapJar" }
        val jarTask = it.tasks.named<org.gradle.jvm.tasks.Jar>(taskName)
        dependsOn(jarTask)
        if (isForge) {
            val endTask = it.tasks.named("reobfJar")
            dependsOn(endTask)
        }
        val jarFile = jarTask.get()
        val jarPath = it.layout.buildDirectory.file("libs/" + jarFile.archiveFileName.get())
        logger.debug("""
            *************************
              ${it.path} finalized mod jar is ${jarPath.get().asFile.absoluteFile}
            *************************
        """.trimIndent())
        from(jarPath)
    }

    into(layout.buildDirectory.dir("libs"))

    subprojects.forEach {
        it.getTasksByName("minimizeJar", false).forEach { t ->
            dependsOn(t)
        }
    }
    finalizedBy("owner-testing-env")
}


tasks.named<DefaultTask>("build") {

    /*
    subprojects.filter {
        val isFabric = it.name.startsWith("fabric")
        val isForge = it.name.startsWith("forge")
        isFabric || isForge
    }.forEach {
        dependsOn(it.tasks["build"])
    }
     */
    dependsOn(tasks["copyPlatformJars"])
    //finalizedBy(tasks["copyPlatformJars"])
}

afterEvaluate {
    /*
    tasks.named<DefaultTask>("build") {
        subprojects.filter {
            val isFabric = it.name.startsWith("fabric")
            val isForge = it.name.startsWith("forge")
            isFabric || isForge
        }.forEach {
            dependsOn(it.tasks["build"])
        }
        subprojects.forEach {
            it.getTasksByName("build", false).forEach { t ->
                dependsOn(t)
            }
        }
        dependsOn(tasks["copyPlatformJars"])
    }
     */
}


/**
 * Version class that does version stuff.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Version(val major: String, val minor: String, val revision: String, val preRelease: Boolean = false) {

    val gitHash
        get() = getGitHash()

    override fun toString(): String {
        return if (!preRelease)
            "$major.$minor.$revision"
        else //Only use git hash if it's a prerelease.
            "$major.$minor.$revision-BETA+C$gitHash-SNAPSHOT"
    }

    fun toCleanString(): String {
        return if (!preRelease)
            "$major.$minor.$revision"
        else //Only use git hash if it's a prerelease.
            "$major.$minor.$revision-SNAPSHOT"
    }

    fun isRelease() = !preRelease
}

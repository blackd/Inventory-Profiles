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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream

val versionObj = Version("2", "0", "2",
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

dependencies {
    antlr("org.antlr:antlr4:4+") // use ANTLR version 4
}

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"


    idea
    `java-library`
    `maven-publish`
    signing
    antlr
    id("io.github.goooler.shadow") version "8+" apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0" apply true
    id("fabric-loom") version("1.6-SNAPSHOT") apply false
    id("com.matthewprenger.cursegradle") version "1.4.+" apply false
    id("com.modrinth.minotaur") version "2.+" apply false
    id("net.neoforged.gradle.userdev") version "7.0.145" apply false
}






evaluationDependsOnChildren()
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

allprojects {
    version = versionObj.toCleanString()
    group = "org.anti-ad.mc"
    ext.set("mod_artefact_version", versionObj.toCleanString())
    ext.set("mod_artefact_is_release", versionObj.isRelease())
    ext.set("libIPN_version", "6.0.0")

    tasks.withType<JavaCompile>().configureEach {
        options.isFork = true
        options.isIncremental = true
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.addAll(listOf("-opt-in=kotlin.ExperimentalStdlibApi", "-opt-in=kotlin.RequiresOptIn"))
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

afterEvaluate {
    tasks.register<Copy>("copyPlatformJars") {
        subprojects.filter {
            it.name.startsWith("fabric") || it.name.startsWith("forge") || it.name.startsWith("neoforge")
        }.forEach { //group = "org.anti-ad.mc.platforms"
            val isForge = it.name.startsWith("forge")
            val taskName = "minimizeJar"
            val jarTask = it.tasks.named<DefaultTask>(taskName)
            dependsOn(jarTask)
            if (isForge) {
                var endTask = it.tasks.named("deobfJar")
                dependsOn(endTask)
                endTask = it.tasks.named("jar")
                dependsOn(endTask)
                endTask = it.tasks.named("customJar")
                dependsOn(endTask)
                endTask = it.tasks.named("copyProGuardJar")
                dependsOn(endTask)


            }
            val jarFile = jarTask.get()
            val jarPath = jarFile.outputs.files.first().toPath()
            logger.debug(
                """
            *************************
              ${it.path} finalized mod jar is ${jarPath.toFile().absoluteFile}
            *************************
        """.trimIndent()
                        )
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

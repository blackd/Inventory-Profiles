import org.anti_ad.mc.getGitHash
import proguard.gradle.ProGuardTask

val versionObj = Version("0", "8", "0", true)

buildscript {
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.1.0-beta5")
    }
}

repositories {
    maven(url = "https://maven.fabricmc.net") {
        name = "Fabric"
    }
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlinx")

    google()
    gradlePluginPortal()
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.4.32"
}

tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    kotlinOptions {
        jvmTarget = "15"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.ExperimentalStdlibApi")
    }
}

allprojects {
    version = versionObj
    group = "org.anti_ad.mc"

    tasks.withType<JavaCompile>().configureEach {
        options.isFork = true
        options.isIncremental = true
    }

}

//val proguard by tasks.registering(ProGuardTask::class) {
//
//    configuration("proguard.txt")
//
//    // project(":platforms:fabric_1_17").tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").get().archiveFileName
//    val fabricRemapJar = project(":platforms:fabric-1.17").tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").get()
//    val inName = fabricRemapJar.archiveFile.get().asFile.absolutePath
//
//    injars(inName)
//    outjars("build/libs/${fabricRemapJar.archiveBaseName.get()}-all-proguard.jar")
//
//    doFirst {
//        libraryjars( project(":platforms:fabric-1.17").configurations.runtimeClasspath.get().files)
//    }
//}

tasks.named<DefaultTask>("build") {

    listOf(":platforms:fabric-1.17", ":common").forEach {
        dependsOn(project(it).tasks["build"])
    }
//    finalizedBy(tasks["proguard"])
}



/**
 * Version class that does version stuff.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Version(val major: String, val minor: String, val revision: String, val preRelease: Boolean = false) {

    override fun toString(): String {
        return if (!preRelease)
            "$major.$minor.$revision"
        else //Only use git hash if it's a prerelease.
            "$major.$minor.$revision-BETA+${getGitHash()}"
    }
}

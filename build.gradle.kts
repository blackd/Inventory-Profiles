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

// This is here but it looks like it's not inherited by the child projets
tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    kotlinOptions {
        jvmTarget = "1.8"
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


tasks.register<Copy>("copyPlatformJars") {
    val fabric117Jar = project(":platforms:fabric-1.17").tasks.named<org.gradle.jvm.tasks.Jar>("remapShadedJar").get()
    val fabric116Jar = project(":platforms:fabric-1.16").tasks.named<org.gradle.jvm.tasks.Jar>("remapShadedJar").get()

    val fabric117JarPath = project(":platforms:fabric-1.17").layout.buildDirectory.file("libs/" + fabric117Jar.archiveFileName.get())
    val fabric116JarPath = project(":platforms:fabric-1.16").layout.buildDirectory.file("libs/" + fabric116Jar.archiveFileName.get())

    from(
        fabric116JarPath, fabric117JarPath
    )
    into(layout.buildDirectory.dir("libs"))
    from(fabric117Jar.archiveFileName)

}

tasks.named<DefaultTask>("build") {

    listOf(":common", ":platforms:fabric-1.17", ":platforms:fabric-1.17").forEach {
        dependsOn(project(it).tasks["build"])
    }

    finalizedBy(tasks["copyPlatformJars"])
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

package org.anti_ad.mc

import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

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

fun Project.getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    val exitCode = exec {
        commandLine = mutableListOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
        this.isIgnoreExitValue = true
    }.exitValue
    return  if (exitCode == 0) {
        stdout.toString().trim()
    } else {
        "not-a-git-repo"
    }

}

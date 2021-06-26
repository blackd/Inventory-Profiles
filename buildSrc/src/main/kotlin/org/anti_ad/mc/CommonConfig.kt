package org.anti_ad.mc

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType
import java.io.ByteArrayOutputStream

fun Project.configureCommon() {
    configureDependencies()
    configureCompilation()
    configureDistribution()

    version = rootProject.version
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

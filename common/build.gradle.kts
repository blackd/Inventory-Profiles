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


import org.anti_ad.mc.configureCompilation
import org.anti_ad.mc.configureDependencies
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") //version "1.6.21"
    kotlin("plugin.serialization") //version "1.6.21"
    `java-library`
    `maven-publish`
    idea
    antlr

}

configureCompilation(true, "InventoryProfilesNext")
configureDependencies()

group = "org.anti-ad.mc"



dependencies {

    val antlrVersion = "4.9.3"
    "antlr"("org.antlr:antlr4:$antlrVersion")
    "implementation"("org.antlr:antlr4-runtime:$antlrVersion")
    "api"("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    "api"("org.jetbrains.kotlin:kotlin-stdlib-common:1.6.21")
    "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
    "api"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")
    "api"("org.jetbrains.kotlin:kotlin-reflect:1.6.21")

    "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    "compileOnlyApi"(group = "org.apache.logging.log4j",
                     name = "log4j-api",
                     version = "2.18.0")
    "compileOnlyApi"(group = "org.lwjgl",
                     name = "lwjgl-glfw",
                     version = "3.3.1")
}


apply(plugin = "kotlinx-serialization")

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.5"
    freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")
}

tasks.named<AntlrTask>("generateGrammarSource").configure {
    val pkg = "org.anti_ad.mc.common.gen"
    outputDirectory = file("build/generated-src/antlr/main/${pkg.replace('.', '/')}")
    arguments = listOf(
        "-visitor", "-package", pkg,
        "-Xexact-output-dir"
    )
}

tasks.named("compileKotlin") {
    dependsOn("generateGrammarSource")
}

tasks.named("compileJava") {
    dependsOn("generateGrammarSource")
}


tasks.withType<JavaCompile>().configureEach {
    dependsOn("generateGrammarSource")
}

plugins.withId("idea") {
    configure<org.gradle.plugins.ide.idea.model.IdeaModel> {
        afterEvaluate {
            module.sourceDirs.add(file("src/main/antlr"))
            module.sourceDirs.add(file("build/generated-src/antlr/main"))
            //module.generatedSourceDirs.add(file("build/generated-src/antlr/main"))
        }
    }
}

val javadoc = tasks.named<Javadoc>("javadoc") {
    this.title = "Inventory Profiles Next API"

    source = project.fileTree("src/main/java/org/anti_ad/mc/ipn/api/")

    classpath = project.fileTree("/") {
        include("src/main/java/")
    }
    classpath += configurations.compileClasspath.get()

}

tasks.create<Jar>("packageJavadoc") {
    from(javadoc)
    archiveClassifier.set("javadoc")
}

tasks.named<DefaultTask>("build") {
    dependsOn("javadoc")
    dependsOn("packageJavadoc")
}
repositories {
    mavenCentral()
}

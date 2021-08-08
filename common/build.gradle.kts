import org.anti_ad.mc.configureCompilation
import org.anti_ad.mc.configureDependencies
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    //kotlin("jvm") version "1.5.21"
    idea
    antlr
}

configureCompilation()
configureDependencies()

group = "org.anti_ad.mc.common"

dependencies {

    //"shadedApi"("org.apache.commons:commons-rng-core:1.3")
    //"shadedApi"("commons-io:commons-io:2.4")

    "shadedApi"("commons-io:commons-io:2.6")



    val antlrVersion = "4.9.2"
    "antlr"("org.antlr:antlr4:$antlrVersion")
    "implementation"("org.antlr:antlr4-runtime:$antlrVersion")
    "compileOnly"(group = "com.google.code.gson",
                  name = "gson",
                  version = "2.8.7")
    "compileOnlyApi"(group = "org.apache.logging.log4j",
                     name = "log4j-api",
                     version = "2.14.1")
    /*
    "runtimeOnly"(group = "org.apache.logging.log4j",
                  name = "log4j-core",
                  version = "2.14.1")

     */
    "compileOnlyApi"(group = "org.lwjgl",
                     name = "lwjgl-glfw",
                     version = "3.2.2")

}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.5"
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
            module.generatedSourceDirs.add(file("build/generated-src/antlr/main"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
//            artifact(tasks["sourcesJar"])
            artifact(tasks["jar"])
        }
    }

    repositories {
        val mavenUrl = "https://repo.codemc.io/repository/maven-releases/"
        val mavenSnapshotUrl = "https://repo.codemc.io/repository/maven-snapshots/"

        maven(mavenUrl) {
            val mavenUsername: String? by project
            val mavenPassword: String? by project
            if (mavenUsername != null && mavenPassword != null) {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}
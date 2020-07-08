buildscript {
  repositories {
    jcenter() // for shadow plugin
    google() // for proguard 7.0.0
  }
  dependencies {
    classpath("com.guardsquare:proguard-gradle:7.0.0")
  }
}

plugins {
  kotlin("jvm") version kotlin_version
  id("fabric-loom") version loom_version
  `maven-publish`
  id("com.github.johnrengelman.shadow") version "5.2.0"
  id("antlr")
}

repositories {
  mavenCentral()
  maven("https://dl.bintray.com/kotlin/kotlin-eap")
  maven("https://kotlin.bintray.com/kotlinx")
}

// ref: https://github.com/DaemonicLabs/fabric-example-mod-kotlin
// ref: https://github.com/natanfudge/fabric-example-mod-kotlin

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

base {
  archivesBaseName = "$mod_id-$mod_loader-$minecraft_version"
}

version = mod_version
group = maven_group

minecraft {
}

dependencies {
  //to change the versions see the gradle.properties file
  minecraft("com.mojang:minecraft:$minecraft_version")
  mappings("net.fabricmc:yarn:$yarn_mappings")
  modCompile("net.fabricmc:fabric-loader:$loader_version")
//  compileOnly("com.google.code.findbugs:jsr305:3.0.2")

  // Fabric API. This is technically optional, but you probably want it anyway.
  //modCompile "net.fabricmc.fabric-api(:fabric-api:${project.fabric_version}")

  // PSA: Some older mods, compiled on Loom 0.2.1, might have outdated Maven POMs.
  // You may need to force-disable transitiveness on them.
  modCompile("io.github.prospector:modmenu:$mod_menu_version")

  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("script-runtime"))
  antlr("org.antlr:antlr4:4.8")
  implementation("org.antlr:antlr4-runtime:4.8")
}

tasks.processResources {
  filesMatching("fabric.mod.json") {
    expand("version" to project.version)
  }
}

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs = listOf("-Xopt-in=kotlin.ExperimentalStdlibApi")
  }
}

// ============
// run client
// ============

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      // add all the jars that should be included when publishing to maven
      artifact(tasks.jar.get()) {
        builtBy(tasks.remapJar)
      }
    }
  }
}

// ============
// build task
// ============

val buildBaseName = "${base.archivesBaseName}-$version"

/*
output jars: (embedding library: kotlin, antlr)
                    *-dev.jar          | no embedded kotlin, no mapping
(by remapJar)       *-remapped-dev.jar | no embedded kotlin, mapped
(by shadowJar)      *-all.jar          | embedded kotlin, no mapping
(by proguard)       *-all-proguard.jar | embedded kotlin, removed unused embedding classes, no mapping
(by remapShadowJar) *.jar              | embedded kotlin, removed unused embedding classes, mapped

 */

// default fabric remap task
// *-dev.jar to *-remapped-dev.jar
tasks.remapJar {
  archiveFileName.set("$buildBaseName-remapped-dev.jar")
}

tasks.shadowJar {
  dependencies {
    include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
    include(dependency("org.jetbrains.kotlin:kotlin-stdlib-common"))
    include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7"))
    include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8"))
    include(dependency("org.antlr:antlr4-runtime"))
  }
  relocate("kotlin", "io.github.jsnimda.common.embedded.kotlin")
  relocate("org.antlr", "io.github.jsnimda.common.embedded.org.antlr")
  exclude("**/*.kotlin_metadata")
  exclude("**/*.kotlin_module")
  exclude("**/*.kotlin_builtins")
  exclude("**/*_ws.class") // fixme find a better solution for removing *.ws.kts
  exclude("**/*_ws$*.class")
  exclude("mappings/mappings.tiny") // before kt, build .jar don"t have this folder (this 500K thing)
}

val remapShadowJar by tasks.registering(net.fabricmc.loom.task.RemapJarTask::class) {
//  input = shadowJar.archivePath
  input.set(file("build/libs/$buildBaseName-all-proguard.jar"))
  addNestedDependencies.set(tasks.remapJar.get().addNestedDependencies.get())
}


val proguard by tasks.registering(proguard.gradle.ProGuardTask::class) {
  configuration("proguard.txt")

  injars("build/libs/$buildBaseName-all.jar")
  outjars("build/libs/$buildBaseName-all-proguard.jar")

  doFirst {
    libraryjars(configurations.runtimeClasspath.get().files)
  }
}

tasks {
  proguard {
    dependsOn(shadowJar)
  }
  remapShadowJar {
    dependsOn(proguard)
  }
  build {
    dependsOn(remapShadowJar)
  }
}

// ============
// antlr
// ============

//https://stackoverflow.com/questions/10615966/compiling-3-2-antlr-grammar-with-gradle
val genAntlr by tasks.registering(JavaExec::class) {
  description = "Generates Java sources from Antlr4 grammars."

  val destinationDir = "src/main/java/io/github/jsnimda/inventoryprofiles/gen"
  val antlrSource = "src/main/java/io/github/jsnimda/inventoryprofiles/parser/antlr"
  val packageName = "io.github.jsnimda.inventoryprofiles.gen"

  inputs.dir(file(antlrSource))
  outputs.dir(file(destinationDir))

  val grammars = fileTree(antlrSource) { include("**/*.g4") }
  val files = grammars.files.map { it.relativeTo(file(".")).normalize() } // no absolute path in generated files

  main = "org.antlr.v4.Tool"
  classpath = configurations.antlr.get()
  args = listOf("-o", destinationDir, "-package", packageName, "-Xexact-output-dir") +
      files.map { it.toString().replace('\\', '/') }
}

// disable default antlr generateGrammarSource
tasks.generateGrammarSource {
  enabled = false
}

tasks.wrapper {
  distributionType = Wrapper.DistributionType.ALL
}
import proguard.gradle.ProGuardTask

buildscript {
  dependencies {
    classpath("com.guardsquare:proguard-gradle:7.1.0-beta5")
  }
}

plugins {
  `maven-publish`
  kotlin("jvm") version kotlin_version
  id("com.github.johnrengelman.shadow") version "7.0.0"
  id("antlr")
  id("com.matthewprenger.cursegradle") version "1.4.0"
  id("fabric-loom") version loom_version
}

repositories {
  mavenCentral()
  maven("https://repo1.maven.org/maven2/")
  maven("https://dl.bintray.com/kotlin/kotlin-eap")
  maven("https://kotlin.bintray.com/kotlinx")
  maven("https://maven.terraformersmc.com/releases")
}

java {
  sourceCompatibility = JavaVersion.VERSION_16
  targetCompatibility = JavaVersion.VERSION_16
}

base {
  //archivesBaseName = "$mod_id-$mod_loader-$minecraft_version"
  archivesName.set("$mod_id-$mod_loader-$minecraft_version")
}

version = mod_version
group = maven_group

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "15"
    freeCompilerArgs = listOf("-Xopt-in=kotlin.ExperimentalStdlibApi")
  }
}

// ============
// dependencies
// ============

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("script-runtime"))
  antlr("org.antlr:antlr4:4.8")
  implementation("org.antlr:antlr4-runtime:4.8")

  // minecraft
  minecraft("com.mojang:minecraft:$minecraft_version")
  mappings("net.fabricmc:yarn:$yarn_mappings")
  modImplementation("net.fabricmc:fabric-loader:$loader_version")
  compileOnly("com.google.code.findbugs:jsr305:3.0.2")

  modImplementation("com.terraformersmc:modmenu:$mod_menu_version")
}

// ============
// run client
// ============

// ref: https://github.com/DaemonicLabs/fabric-example-mod-kotlin
// ref: https://github.com/natanfudge/fabric-example-mod-kotlin

minecraft {

}

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

tasks.processResources {
  inputs.property("version", project.version)
  filesMatching("fabric.mod.json") {
    expand("version" to project.version)
  }
}

// ============
// build task
// ============

val buildBaseName = "${base.archivesBaseName}-$version"

/*
output jars: (embedding library: kotlin, antlr)
                    *-non-shadow.jar   | no embedded kotlin, no mapping
(by remapJar)       *-remapped-dev.jar | no embedded kotlin, mapped
(by shadowJar)      *-all.jar          | embedded kotlin, no mapping
(by proguard)       *-all-proguard.jar | embedded kotlin, removed unused embedding classes, no mapping
(by remapShadowJar) *.jar              | embedded kotlin, removed unused embedding classes, mapped

 */

tasks.jar {
  archiveFileName.set("$buildBaseName-non-shadow.jar")
}

// ============
// distinct task
// ============

tasks.remapJar { // fabric
  archiveFileName.set("$buildBaseName-remapped-dev.jar")
}

val remapShadowJar by tasks.registering(net.fabricmc.loom.task.RemapJarTask::class) {
//  input = shadowJar.archivePath
  input.set(file("build/libs/$buildBaseName-all-proguard.jar"))
  addNestedDependencies.set(tasks.remapJar.get().addNestedDependencies.get())
}

val remapCustomJar = remapShadowJar

// ============
// common task
// ============

tasks.shadowJar {
  dependencies {
    include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
    include(dependency("org.jetbrains.kotlin:kotlin-stdlib-common"))
    //include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7"))
    //include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8"))
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
  exclude("META-INF/maven/**")
}

val proguard by tasks.registering(ProGuardTask::class) {
  configuration("proguard.txt")

  injars("build/libs/$buildBaseName-all.jar")
  outjars("build/libs/$buildBaseName-all-proguard.jar")

  //doFirst {
    libraryjars(configurations.runtimeClasspath.get().files)
  //}
}

tasks {
  proguard {
    dependsOn(shadowJar)
  }
  remapCustomJar {
    dependsOn(proguard)
  }
  build {
    dependsOn(remapCustomJar)
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

// ============
// wrapper
// ============

tasks.wrapper {
  distributionType = Wrapper.DistributionType.ALL
}

// ============
// curseforge
// ============

val curseforgeApiKey = project.findProperty("curseforge_api_key") ?: ""
/*
curseforge {
  apiKey = curseforgeApiKey
  project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
    id = "347463"
    changelogType = "markdown"
    changelog = file("changelog.md")
    releaseType = when (mod_loader) {
      "forge" -> "release"
      "fabric" -> "beta"
      else -> "alpha"
    }

    mainArtifact(file("build/libs/$buildBaseName.jar"), closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
      displayName = "$mod_loader $minecraft_version (v$mod_version invpro)"
    })

    afterEvaluate {
      uploadTask.dependsOn("build")
    }
  })
  options(closureOf<com.matthewprenger.cursegradle.Options> {
    debug = true
    javaIntegration = false
    forgeGradleIntegration = mod_loader == "forge"
  })
}
*/
// ============
// other
// ============

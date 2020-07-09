import proguard.gradle.ProGuardTask

buildscript {
  dependencies {
    classpath("com.guardsquare:proguard-gradle:7.0.0")
  }
}

plugins {
  `maven-publish`
  kotlin("jvm") version kotlin_version
  id("com.github.johnrengelman.shadow") version "5.2.0"
  id("antlr")

  id("net.minecraftforge.gradle")
}

repositories {
  mavenCentral()
  maven("https://dl.bintray.com/kotlin/kotlin-eap")
  maven("https://kotlin.bintray.com/kotlinx")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

base {
  archivesBaseName = "$mod_id-$mod_loader-$minecraft_version"
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
    jvmTarget = "1.8"
    freeCompilerArgs = listOf("-Xopt-in=kotlin.ExperimentalStdlibApi")
  }
}

// ============
// dependencies
// ============

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("script-runtime"))
  antlr("org.antlr:antlr4:4.8")
  implementation("org.antlr:antlr4-runtime:4.8")

  // minecraft
  minecraft("net.minecraftforge:forge:$minecraft_version-$forge_version")
}

// ============
// run client
// ============

// ref: https://github.com/proudust/minecraft-forge-kotlin-template

minecraft {
  mappings(mapOf("channel" to mappings_channel, "version" to mappings_version))
  runs.register("client") {
    workingDirectory(project.file("run"))

    // Recommended logging data for a userdev environment
    property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")

    // Recommended logging level for the console
    property("forge.logging.console.level", "debug")

    mods.register("examplemod") {
      source(sourceSets.main.get())
    }
  }
}

// Example configuration to allow publishing using the maven-publish task
// we define a custom artifact that is sourced from the reobfJar output task
// and then declare that to be published
// Note you"ll need to add a repository here
val reobfFile = file("$buildDir/reobfJar/output.jar")
val reobfArtifact = artifacts.add("default", reobfFile) {
  type = "jar"
  builtBy(reobf)
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      // add all the jars that should be included when publishing to maven
      artifact(reobfArtifact)
    }
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

val originalJar by dummyJar(
  thisJarName = "$buildBaseName-remapped-dev.jar",
  fromJarName = "$buildBaseName-non-shadow.jar"
)
val customJar by dummyJar( // dummy jar
  thisJarName = "$buildBaseName.jar",
  fromJarName = "$buildBaseName-all-proguard.jar"
)

fun dummyJar(thisJarName: String, fromJarName: String) = tasks.registering(Jar::class) { // dummy jar for reobf
  archiveFileName.set(thisJarName)
  doLast {
    copy {
      from("build/libs/$fromJarName")
      into("build/libs")
      rename { thisJarName }
    }
  }
}

reobf.register("originalJar")
reobf.register("customJar")

val remapCustomJar = customJar

// ============
// common task
// ============

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

val proguard by tasks.registering(ProGuardTask::class) {
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
// other
// ============

// https://forums.minecraftforge.net/topic/62995-shadowing-dependencies/
//https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/97c54505d9b62ea8b3a19c37d9ceb55f209eb2b1/build.gradle#L55-L69

//
////https://stackoverflow.com/questions/49638136/kotlin-gradle-plugin-how-to-use-custom-output-directory
////println(compileKotlin.destinationDir)
////tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).all {
////  destinationDir = new File(buildDir, "classes/java/main")
////}
//// tmp solution fixing duplicate classes or forge not loading the .class files
////https://discuss.gradle.org/t/duplicated-classes-output-jar-with-gradle/17301
////     ensure you"re excluding duplicates
//jar {
//  duplicatesStrategy = "exclude"
//}


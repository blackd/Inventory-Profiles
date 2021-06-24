import proguard.gradle.ProGuardTask
import com.modrinth.minotaur.TaskModrinthUpload;


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

  id ("com.modrinth.minotaur") version "1.2.1"

}

repositories {
  mavenCentral()
  maven("https://repo1.maven.org/maven2/")
  maven("https://dl.bintray.com/kotlin/kotlin-eap")
  maven("https://kotlin.bintray.com/kotlinx")
  maven("https://maven.terraformersmc.com/releases")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
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
    jvmTarget = "1.8"
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
  modImplementation("com.terraformersmc:modmenu:$mod_menu_version")
  modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

  compileOnly("com.google.code.findbugs:jsr305:3.0.2")
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

tasks.runClient {

  //mainClass.set(net.fabricmc.loom.util.Constants.Knot.KNOT_CLIENT)

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
  input.set {
    file("build/libs/$buildBaseName-all-proguard.jar")
  }
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
    include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7"))
    include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8"))
    include(dependency("org.antlr:antlr4-runtime"))
  }
  relocate("kotlin", "org.anti_ad.mc.common.embedded.kotlin")
  relocate("org.antlr", "org.anti_ad.mc.common.embedded.org.antlr")
  exclude("**/*.kotlin_metadata")
  exclude("**/*.kotlin_module")
  exclude("**/*.kotlin_builtins")
  exclude("**/*_ws.class") // fixme find a better solution for removing *.ws.kts
  exclude("**/*_ws$*.class")
  //exclude("mappings/mappings.tiny") // before kt, build .jar don"t have this folder (this 500K thing)
  exclude("META-INF/maven/**")
}

val proguard by tasks.registering(ProGuardTask::class) {
  configuration("proguard.txt")
  verbose()

  injars("build/libs/$buildBaseName-all.jar")
  outjars("build/libs/$buildBaseName-all-proguard.jar")

  doFirst {
    libraryjars(configurations.runtimeClasspath.get().files)
  }
}



// ============
// antlr
// ============

//https://stackoverflow.com/questions/10615966/compiling-3-2-antlr-grammar-with-gradle
val genAntlr by tasks.registering(JavaExec::class) {
  description = "Generates Java sources from Antlr4 grammars."

  val destinationDir = "src/main/java/org/anti_ad/mc/ipnext/gen"
  val antlrSource = "src/main/java/org/anti_ad/mc/ipnext/parser/antlr"
  val packageName = "org.anti_ad.mc.ipnext.gen"

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


curseforge {
  if (System.getenv("CURSEFORGE_DEPOY_TOKEN") != null) {
    apiKey = System.getenv("CURSEFORGE_DEPOY_TOKEN")
  }

  project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
    id = "495267"
    changelogType = "markdown"
    changelog = file("changelog.md")
    releaseType = "release"
    supported_minecraft_versions.forEach {
      if (!it.toLowerCase().contains("pre") && !it.toLowerCase().contains("shanpshot")) {
        this.addGameVersion(it)
      }
    }

    mainArtifact(file("build/libs/$buildBaseName.jar"), closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
      displayName = "Inventory Profiles Next-$mod_loader-$minecraft_version-$mod_version"
    })

    afterEvaluate {
      uploadTask.dependsOn("build")
    }

  })
  options(closureOf<com.matthewprenger.cursegradle.Options> {
    debug = false
    javaIntegration = false
    forgeGradleIntegration = mod_loader == "forge"
  })
}

// ============
// modrith
// ============


val publishModrinth by tasks.registering(TaskModrinthUpload::class) {

  onlyIf {
    System.getenv("MODRINTH_TOKEN") != null
  }

  token = System.getenv("MODRINTH_TOKEN") // An environment property called MODRINTH that is your token, set via Gradle CLI, GitHub Actions, Idea Run Configuration, or other

  projectId = "O7RBXm3n"
  versionNumber = "Inventory Profiles Next-$mod_loader-$minecraft_version-$mod_version" // Will fail if Modrinth has this version already
  // On fabric, use 'remapJar' instead of 'jar'
  this.changelog
  uploadFile = remapCustomJar.get().archiveFile // This is the java jar task. If it can't find the jar, try 'jar.outputs.getFiles().asPath' in place of 'jar'
  supported_minecraft_versions.forEach { ver ->
    addGameVersion(ver) // Call this multiple times to add multiple game versions. There are tools that can help you generate the list of versions
  }
  changelog = project.rootDir.resolve("changelog.md").readText()
  addLoader(mod_loader)

}

// ============
// other
// ============


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
  publishModrinth {
    dependsOn(build)
  }
}
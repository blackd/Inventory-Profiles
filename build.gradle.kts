import proguard.gradle.ProGuardTask
import com.modrinth.minotaur.TaskModrinthUpload;


buildscript {
  repositories {
    maven("https://files.minecraftforge.net/maven")
    mavenCentral()
  }
  dependencies {
    classpath("com.guardsquare:proguard-gradle:7.0.1")
  }

}

plugins {
  `maven-publish`
  kotlin("jvm") version kotlin_version
  id("net.minecraftforge.gradle")
  id("com.github.johnrengelman.shadow") version "5.2.0"
  id("antlr")

  id("com.matthewprenger.cursegradle") version "1.4.0"
  id ("com.modrinth.minotaur") version "1.2.1"
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
  runs.create("client") {
    workingDirectory(project.file("run"))

    // Recommended logging data for a userdev environment
    property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")

    // Recommended logging level for the console
    property("forge.logging.console.level", "debug")

    mods.create("invpro") {
      source(sourceSets.main.get())
    }
  }
}

tasks.processResources {
  inputs.property("version", project.version)
  filesMatching("META-INF/mods.toml") {
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

val originalJar by dummyJar(
  thisJarName = "$buildBaseName-remapped-dev.jar",
  fromJarName = "$buildBaseName-non-shadow.jar"
)
val customJar by dummyJar( // dummy jar
  thisJarName = "$buildBaseName.jar",
  fromJarName = "$buildBaseName-all-proguard.jar"
)

fun dummyJar(thisJarName: String, fromJarName: String) = tasks.creating(Jar::class) { // dummy jar for reobf
  archiveFileName.set(thisJarName)
  doLast {
    copy {
      from("build/libs/$fromJarName")
      into("build/libs")
      rename { thisJarName }
    }
  }
}
val reobfOriginalJar = reobf.create("originalJar")
val reobfCustomJar = reobf.create("customJar")
reobfOriginalJar.mustRunAfter("proguard")
reobfCustomJar.mustRunAfter("proguard")

reobf.create("jar").enabled = false // disable reobfJar

val remapCustomJar by tasks.registering {
  dependsOn(reobfOriginalJar)
  dependsOn(reobfCustomJar)
}

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
  exclude("mappings/mappings.tiny") // before kt, build .jar don"t have this folder (this 500K thing)
  exclude("META-INF/maven/**")
}

val proguard by tasks.registering(ProGuardTask::class) {
  configuration("proguard.txt")

  injars("build/libs/$buildBaseName-all.jar")
  outjars("build/libs/$buildBaseName-all-proguard.jar")

  doFirst {
    libraryjars(configurations.runtimeClasspath.get().files.filter {
      !it.endsWith("nashorn-core-compat-15.1.1.1.jar")
    })
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
    debug = true
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
  uploadFile = file("build/libs/$buildBaseName.jar") // This is the java jar task. If it can't find the jar, try 'jar.outputs.getFiles().asPath' in place of 'jar'
  supported_minecraft_versions.forEach { ver ->
    addGameVersion(ver) // Call this multiple times to add multiple game versions. There are tools that can help you generate the list of versions
  }
  changelog = project.rootDir.resolve("changelog.md").readText()
  addLoader(mod_loader)

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


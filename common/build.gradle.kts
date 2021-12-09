import org.anti_ad.mc.configureCompilation
import org.anti_ad.mc.configureDependencies
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    `maven-publish`
    idea
    antlr

}

configureCompilation(true)
configureDependencies()

group = "org.anti-ad.mc"



dependencies {

    val antlrVersion = "4.9.3"
    "antlr"("org.antlr:antlr4:$antlrVersion")
    "implementation"("org.antlr:antlr4-runtime:$antlrVersion")
    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-common:1.6.0")
    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.0")
    "shadedApi"("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0")
    "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    "compileOnlyApi"(group = "org.apache.logging.log4j",
                     name = "log4j-api",
                     version = "2.14.1")
    "compileOnlyApi"(group = "org.lwjgl",
                     name = "lwjgl-glfw",
                     version = "3.2.2")
}


apply(plugin = "kotlinx-serialization")

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.5"
    freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
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


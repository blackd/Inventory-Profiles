import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    "implementation"("com.github.jengelman.gradle.plugins:shadow:+")
    //implementation(kotlin("stdlib-jdk8"))
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

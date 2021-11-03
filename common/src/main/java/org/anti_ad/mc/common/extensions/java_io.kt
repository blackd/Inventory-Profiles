package org.anti_ad.mc.common.extensions

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.outputStream

// also close the input stream

fun String.writeToFile(path: Path) = path.outputStream().writer().use {
    it.write(this)
}

fun Path.createDirectories(): Path = Files.createDirectories(this)
fun Path.exists(): Boolean = Files.exists(this)

/*
fun Path.listFiles(regex: String): List<Path> = FileUtils.listFiles(this.toFile(),
                                                                    RegexFileFilter(regex,
                                                                                    IOCase.INSENSITIVE),
                                                                    null).map { it.toPath() }
 */
fun Path.listFiles(regex: String): List<Path> {
    val match = Regex(regex)
    val res: MutableList<Path> = mutableListOf()
    Files.walk(this).filter {
        match.matches(it.name)
    }.forEach {
        res.add(it)
    }
    return res
}




val Path.name
    get() = this.fileName.toString()

fun pathOf(first: String,
           vararg more: String): Path = Paths.get(first,
                                                  *more)

// ============
// operators
// ============

operator fun Path.div(other: Path): Path = resolve(other).normalize()
operator fun Path.div(other: String): Path = resolve(other).normalize()

// abs + rel = abs, abs - abs = rel
// a + b = c, c - a = b
// [/a/b/c/d] pathFrom [/a/b] = [c/d]
infix fun Path.pathFrom(other: String): Path = this pathFrom pathOf(other)
infix fun Path.pathFrom(other: Path): Path {
    try {
        return other.relativize(this).normalize()
    } catch (e: IllegalArgumentException) {
        return this
    }
}


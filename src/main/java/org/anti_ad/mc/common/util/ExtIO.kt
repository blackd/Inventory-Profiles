package org.anti_ad.mc.common.util

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOCase
import org.apache.commons.io.IOUtils
import org.apache.commons.io.filefilter.RegexFileFilter
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// also close the input stream
fun InputStream.readToString(): String = use {
    IOUtils.toString(this,
                     StandardCharsets.UTF_8)
}

fun Path.readToString(): String = FileUtils.readFileToString(this.toFile(),
                                                             StandardCharsets.UTF_8)

fun String.writeToFile(path: Path) = FileUtils.writeStringToFile(path.toFile(),
                                                                 this,
                                                                 StandardCharsets.UTF_8)

fun Path.createDirectories(): Path = Files.createDirectories(this)
fun Path.exists(): Boolean = Files.exists(this)
fun Path.listFiles(regex: String): List<Path> =
    FileUtils.listFiles(this.toFile(),
                        RegexFileFilter(regex,
                                        IOCase.INSENSITIVE),
                        null).map { it.toPath() }

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

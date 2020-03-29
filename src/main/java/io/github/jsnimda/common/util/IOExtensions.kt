package io.github.jsnimda.common.util

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOCase
import org.apache.commons.io.IOUtils
import org.apache.commons.io.filefilter.RegexFileFilter
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

// also close the input stream
fun InputStream.readToString(): String = use { IOUtils.toString(this, StandardCharsets.UTF_8) }

fun Path.readFileToString(): String = FileUtils.readFileToString(this.toFile(), StandardCharsets.UTF_8)
fun Path.writeStringToFile(data: String) = FileUtils.writeStringToFile(this.toFile(), data, StandardCharsets.UTF_8)
fun Path.createDirectories(): Path = Files.createDirectories(this)
fun Path.exists(): Boolean = Files.exists(this)
fun Path.listFiles(regex: String): List<Path> =
  FileUtils.listFiles(this.toFile(), RegexFileFilter(regex, IOCase.INSENSITIVE), null).map { it.toPath() }

val Path.name
  get() = this.fileName.toString()

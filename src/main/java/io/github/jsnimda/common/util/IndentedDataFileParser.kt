package io.github.jsnimda.common.util

import io.github.jsnimda.common.Log

private val String.indent: String
  get() {
    this.forEachIndexed { index, c -> if (!c.isWhitespace()) return this.substring(0, index) }
    return this
  }
private val String.hasIndent
  get() = this.isNotEmpty() && this[0].isWhitespace()

private fun String.isCommentOrBlank(): Boolean =
  isBlank() || trimStart().startsWith("//")

class IndentedDataFileParser(lines: List<String>, private val fileName: String) {
  companion object {
    fun parse(text: String, fileName: String = "<unknown file>"): IndentedData =
      parse(text.split(Regex("\\r?\\n")), fileName)

    fun parse(lines: List<String>, fileName: String = "<unknown file>"): IndentedData =
      IndentedDataFileParser(lines, fileName).parse()
  }

  //region Line

  private data class Line(val lineNumber: Int, val text: String)

  private fun List<Line>.grouped(): List<Group> {
    val result = mutableListOf<Group>()
    this.forEach { line ->
      if (line.text.hasIndent) {
        if (result.isEmpty()) {
          errors += line.copy(text = "unexpected indent")
        } else {
          result.last().children += line
        }
      } else {
        result.lastOrNull()?.normalize()
        result += Group(line)
      }
    }
    result.lastOrNull()?.normalize()
    return result
  }

  private val lines = lines.mapIndexed { index, s -> Line(index + 1, s) }.filter { !it.text.isCommentOrBlank() }

  //endregion

  //region Group

  private inner class Group(val owner: Line) { // owner.hasIndent is always false
    val children = mutableListOf<Line>()
    fun normalize() {
      if (children.isEmpty()) return
      val rawList = children.toList()
      val childIntent = children[0].text.indent
      children.clear()
      rawList.forEach { oldLine ->
        if (oldLine.text.startsWith(childIntent)) {
          children += oldLine.copy(text = oldLine.text.substring(childIntent.length))
        } else {
          errors += oldLine.copy(text = "indent mismatch")
        }
      }
    }
  }

  private fun Group.toData(): IndentedData = IndentedData(owner.lineNumber, false, owner.text).apply {
    children.toList().grouped().addToData(this)
  }

  private fun List<Group>.addToData(data: IndentedData) {
    this.forEach { data.subData.add(it.toData()) }
  }

  //endregion

  private val errors = mutableListOf<Line>()

  fun parse(): IndentedData = IndentedData(1, true).apply {
    lines.grouped().addToData(this)
    warnAllErrors()
  }

  private fun warnAllErrors() {
    errors.sortedBy { it.lineNumber }.forEach {
      Log.warn("Warning in parsing $fileName: ${it.text} at line ${it.lineNumber}")
    }
  }

}

private const val INDENT = "    "

class IndentedData(val lineNumber: Int, val isRoot: Boolean, text: String = "") {
  val text = text
    get() = if (isRoot) "" else field
  val subData = mutableListOf<IndentedData>()
  private val subDataStrings: List<String>
    get() = if (isRoot) subData.map { it.toString() } else subData.flatMap {
      it.toString().split("\n")
    }.map { INDENT + it }

  override fun toString(): String =
    (listOf(text) + subDataStrings).joinToString("\n")
}


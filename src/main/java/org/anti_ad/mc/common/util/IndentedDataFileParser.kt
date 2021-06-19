package org.anti_ad.mc.common.util

import org.anti_ad.mc.common.Log

class IndentedDataFileParser(lines: List<String>,
                             private val fileName: String,
                             private val maxDepth: Int = -1) {
    private val errors = mutableListOf<Line>()
    private val filteredLines = lines.mapIndexed { index, s ->
        Line(index + 1,
             s)
    }.filter { !it.text.isCommentOrBlank }
        .dropWhile { line -> line.text.hasIndent.also { if (it) errors += line.copy(text = "unexpected indent") } }

    private inner class IndentedDataImpl(
        override val lineNumber: Int,
        override val rawText: String,
        override var text: String = rawText
    ) : IndentedData {
        var maxDepth = -1
        override val subData = mutableListOf<IndentedDataImpl>()
        override fun toString(): String = paragraph
        fun deep() {
            if (maxDepth == 0) return
            if (subData.isEmpty()) return
            trimFirstIndent()
            val copy = subData.toList()
            subData.clear()
            copy.forEach { data ->
                if (data.text.hasIndent) subData.last().subData.add(data)
                else subData.add(data)
            }
            subData.forEach {
                if (maxDepth > 0) it.maxDepth = maxDepth - 1
                else it.maxDepth = maxDepth
                it.deep()
            }
        }

        fun trimFirstIndent() {
            val indent = subData.first().text.indent
            subData.retainAll { data ->
                data.text.startsWith(indent).also { retain ->
                    if (retain) data.text = data.text.substring(indent.length)
                    else errors += Line(data.lineNumber,
                                        "indent mismatch")
                }
            }
        }
    }

    private fun warnAllErrors() {
        errors.sortedBy { it.lineNumber }.forEach {
            Log.warn("Indent Error while parsing $fileName: ${it.text} at line ${it.lineNumber}")
        }
    }

    fun parse(): IndentedData = IndentedDataImpl(-1,
                                                 "").apply {
        filteredLines.mapTo(subData) {
            IndentedDataImpl(it.lineNumber,
                             it.text)
        }
        maxDepth = this@IndentedDataFileParser.maxDepth
        deep()
        warnAllErrors()
    }

    companion object {
        fun parse(text: String,
                  fileName: String = "<unknown file>",
                  maxDepth: Int = -1): IndentedData =
            parse(text.lines(),
                  fileName,
                  maxDepth)

        private fun parse(lines: List<String>,
                          fileName: String = "<unknown file>",
                          maxDepth: Int = -1): IndentedData =
            IndentedDataFileParser(lines,
                                   fileName,
                                   maxDepth).parse()
    }
}

private val String.indent: String
    get() = indexOfFirst { !it.isWhitespace() }.let {
        if (it < 0) this else substring(0,
                                        it)
    }
private val String.hasIndent
    get() = this.isNotEmpty() && this[0].isWhitespace()
private val String.isCommentOrBlank: Boolean
    get() = isBlank() || trimStart().startsWith("//")

data class Line(val lineNumber: Int,
                val text: String)

data class IndentedLine(val lineNumber: Int,
                        val rawText: String,
                        val text: String = rawText)

interface IndentedData {
    val lineNumber: Int
    val rawText: String
    val text: String
    val subData: List<IndentedData>

    val asLine
        get() = IndentedLine(lineNumber,
                             rawText,
                             text)
    val lines: List<IndentedLine>
        get() = listOf(asLine) + subLines
    val subLines: List<IndentedLine>
        get() = subData.flatMap { it.lines }
    val rawParagraph
        get() = lines.joinToString("\n") { it.rawText }
    val subRawParagraph
        get() = subLines.joinToString("\n") { it.rawText }
    val paragraph: String
        get() = (listOf(text) + subData.map { it.paragraph.prependIndent() }).joinToString("\n")
    val subParagraph: String
        get() = subData.joinToString("\n") { it.paragraph }
}

package org.anti_ad.mc.ipnext.parser

import org.antlr.v4.runtime.*

data class SyntaxErrorException(val line: Int,
                                val pos: Int,
                                val msg: String) : RuntimeException()

fun <T : Parser> String.parseBy(lexerConstructor: (CharStream) -> Lexer,
                                parserConstructor: (TokenStream) -> T,
                                lexerMode: Int = Lexer.DEFAULT_MODE): T =
        lexerConstructor(CharStreams.fromString(this)).apply {
            removeErrorListeners()
            addErrorListener(ErrorListener)
            mode(lexerMode)
        }.let {
            CommonTokenStream(it)
        }.let {
            parserConstructor(it)
        }.apply {
            removeErrorListeners()
            addErrorListener(ErrorListener)
        }

private object ErrorListener : BaseErrorListener() {

    override fun syntaxError(recognizer: Recognizer<*, *>?,
                             offendingSymbol: Any?,
                             line: Int,
                             charPositionInLine: Int,
                             msg: String,
                             e: RecognitionException?) {
        throw SyntaxErrorException(line,
                                   charPositionInLine,
                                   msg)
    }
}

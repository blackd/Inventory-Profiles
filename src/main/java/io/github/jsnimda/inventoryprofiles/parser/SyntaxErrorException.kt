package io.github.jsnimda.inventoryprofiles.parser

data class SyntaxErrorException(val line: Int, val pos: Int, val msg: String) : RuntimeException()
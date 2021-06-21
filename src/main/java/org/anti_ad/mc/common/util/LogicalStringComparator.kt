package org.anti_ad.mc.common.util

import java.math.BigInteger
import java.text.Collator

// https://stackoverflow.com/questions/23205020/java-sort-strings-like-windows-explorer
// https://stackoverflow.com/questions/60092486/java-file-list-same-order-like-window-explorer
class LogicalStringComparator(private val textComparator: Comparator<in String>) : Comparator<String> {
    companion object {
        val ignoreCase = LogicalStringComparator(String.CASE_INSENSITIVE_ORDER)
        val locale = { LogicalStringComparator(Collator.getInstance()) }
        val file = { LogicalStringComparator(Collator.getInstance().apply { strength = Collator.PRIMARY }) }
    }

    constructor() : this(naturalOrder<String>())

    private fun compareWhenEqual(str1: String,
                                 str2: String,
                                 minusCounts1: List<Int>,
                                 minusCounts2: List<Int>): Int {
        // basically equal, let see if there are different in minuses
        minusCounts1.compareTo(minusCounts2).let { result ->
            if (result != 0) return result
        }
        // logical equal, but for consistency, compare them again but by unicode
        return str1.compareTo(str2)
    }

    override fun compare(str1: String,
                         str2: String): Int {
        var index1 = 0
        var index2 = 0
        val minusCounts1 = mutableListOf<Int>()
        val minusCounts2 = mutableListOf<Int>()
        while (true) {
            //region Check '-': windows seems to omit '-'
            run {
                val startIndex1 = index1
                val startIndex2 = index2
                while (index1 < str1.length && str1[index1] == '-') index1++
                while (index2 < str2.length && str2[index2] == '-') index2++
                if (startIndex1 != index1 || startIndex2 != index2) {
                    minusCounts1.add(index1 - startIndex1)
                    minusCounts2.add(index2 - startIndex2)
                }
            }
            //endregion
            //region Check hasNext()
            if (index1 >= str1.length || index2 >= str2.length) {
                if (index1 < str1.length) return 1 // str2 shorter than str1
                if (index2 < str2.length) return -1 // str2 longer than str 1
                return compareWhenEqual(str1,
                                        str2,
                                        minusCounts1,
                                        minusCounts2)
            }
            //endregion
            //region Check digit
            // is '0'..'9', '０'..'９'
            val d1 = when (str1[index1]) {
                in '0'..'9' -> '0'
                in '\uFF10'..'\uFF19' -> '\uFF10'
                else -> '\u0000'
            }
            val d2 = when (str2[index2]) {
                in '0'..'9' -> '0'
                in '\uFF10'..'\uFF19' -> '\uFF10'
                else -> '\u0000'
            }
            if (d1 != '\u0000' && d2 != '\u0000') {
                // enter numerical content comparing mode
                val startIndex1 = index1
                val startIndex2 = index2
                while (index1 < str1.length && str1[index1] in d1..(d1 + 9)) index1++
                while (index2 < str2.length && str2[index2] in d2..(d2 + 9)) index2++
                str1.substring(startIndex1,
                               index1).let { num1 ->
                    str2.substring(startIndex2,
                                   index2).let { num2 ->
                        BigInteger(num1).compareTo(BigInteger(num2)).let { result ->
                            if (result != 0) return result
                        }
                    }
                }
            } //endregion
            else {
                textComparator.compare(str1[index1].toString(),
                                       str2[index2].toString()).let { result ->
                    if (result != 0) return result
                }
                index1++
                index2++
            }
        }
    }
}
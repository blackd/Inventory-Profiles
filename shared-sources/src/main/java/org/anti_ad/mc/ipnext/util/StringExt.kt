package org.anti_ad.mc.ipnext.util

fun String.sanitized(): String {
    if (this == ".." || this == ".") return "-dot_dot"
    return if (this.isNotEmpty()) {
        "-" + this.replace("/","(slash)")
            .replace("\\","(bslash)")
            .replace(":", "(colon)")
            .replace("<", "(lt)")
            .replace(">","(gt)")
            .replace("|","(pipe)")
            .replace("?","(qm)")
            .replace("*", "(asterisk)")
            .replace("\"","(dquote)")
    } else {
        this
    }
}
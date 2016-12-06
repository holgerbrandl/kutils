package de.mpicbg.scicomp.kutils

import java.io.File
import java.io.PrintWriter


operator fun File.div(childName: String): File {
    return this.resolve(childName)
}

fun File.changeExt(old: String, new: String): File {
    val destName = File(this.absolutePath + new)
    this.renameTo(destName)
    return destName
}

fun <T> Iterable<T>.saveAs(f: File, overwrite: Boolean = true, transform: (T) -> String = { it.toString() }) {
    if (f.isFile && !overwrite) {
        throw IllegalArgumentException("$f is present already")
    }

    val p = PrintWriter(f)
    toList().forEach { p.write(transform(it) + "\n") }

    p.close()
}


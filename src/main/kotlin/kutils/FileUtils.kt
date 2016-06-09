package kutils

import java.io.File


operator fun File.div(childName: String): File {
    return this.resolve(childName)
}

fun File.changeExt(old: String, new: String): File {
    val destName = File(this.absolutePath + new)
    this.renameTo(destName)
    return destName
}

fun <T> Iterable<T>.saveAs(f: java.io.File, overwrite: Boolean = true, transform: (T) -> String = { it.toString() }) {
    if (f.isFile && !overwrite) {
        throw IllegalArgumentException("$f is present already")
    }

    val p = java.io.PrintWriter(f)

    for (t in this) {
        p.write(transform(t))
    }

    p.close()
}


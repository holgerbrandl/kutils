package de.mpicbg.scicomp.kutils

import java.io.*
import java.util.zip.GZIPOutputStream


operator fun File.div(childName: String): File {
    return this.resolve(childName)
}


//https://dzone.com/articles/readingwriting-compressed-and
/** Save a list of items into a file. Output can be option ally zipped and a the stringifying operation can be changed from toString to custom operation if needed. */
fun <T> Iterable<T>.saveAs(f: File,
                           transform: (T) -> String = { it.toString() },
                           separator: Char = '\n',
                           overwrite: Boolean = true,
                           compress: Boolean = f.name.let { it.endsWith(".zip") || it.endsWith(".gz") }) {

    require(f.isFile && !overwrite) { "$f is present already" }

    val p = if (!compress) PrintWriter(f) else BufferedWriter(OutputStreamWriter(GZIPOutputStream(FileOutputStream(f))))

    toList().forEach { p.write(transform(it) + separator) }

    p.close()
}




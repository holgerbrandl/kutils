package de.mpicbg.scicomp.bioinfo

/**
 * @author Holger Brandl
 */


fun String.toBed3(): BedEntry? {
    if (this.startsWith("#")) return null

    val splitLine = this.split("\t")

    return BedEntry(splitLine[0], splitLine[1].toLong(), splitLine[2].toLong(), splitLine.drop(3))
}

// should go into dedicated library or even  use biojava types here
data class BedEntry(val chromsome: String, val start: Long, val end: Long, val otherProperties: List<String>) {


    val length by lazy { end - start }

    override fun toString(): String {
        return listOf(chromsome, start, end, otherProperties).joinToString("\t")
    }
}


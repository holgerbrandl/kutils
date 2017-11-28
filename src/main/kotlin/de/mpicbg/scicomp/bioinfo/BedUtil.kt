package de.mpicbg.scicomp.bioinfo

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * @author Holger Brandl
 */


fun String.toBed3(): BedEntry? {
    if (this.startsWith("#")) return null

    val splitLine = this.split("\t")

    return BedEntry(splitLine[0], splitLine[1].toLong(), splitLine[2].toLong(), otherProperties = splitLine.drop(3))
}

fun String.toBed6(): BedEntry? {
    if (this.startsWith("#")) return null

    val splitLine = this.split("\t")

    return BedEntry(splitLine[0], splitLine[1].toLong(), splitLine[2].toLong(), splitLine[3],splitLine[4],splitLine[5], otherProperties = splitLine.drop(6))
}

// should go into dedicated library or even  use biojava types here
/**
 * For format specs see http://www.ensembl.org/info/website/upload/bed.html
 */

data class BedEntry(val chromsome: String, val start: Long, val end: Long, // bed3
                    val name: String? = null, val score: String? = null, val strand: String? = null, //bed6
                    val thickStart: String? = null, val thickEnd: String? = null, val itemRgb: String? = null, val blockCount: String? = null, val blockSizes: String? = null, val blockStarts: String? = null,
                    val otherProperties: List<String> = listOf()) {

    //    name: String, score: String, strand: String, thickStart: String, thickEnd: String, itemRgb: String, blockCount: String, blockSizes: String, blockStarts: String
    //    constructor( chromsome: String,  start: Long,  end: Long, vararg  otherProperties: List<String>) : this(chromsome, start, end, otherProperties)

    val length by lazy { end - start }


    override fun toString(): String {
        val props = listOf(chromsome, start, end, name ?: ".", score ?: ".", strand, thickStart,
                thickEnd, itemRgb, blockCount, blockSizes, blockStarts) + otherProperties

        return props.filterNotNull().joinToString("\t")
    }
}


fun readBed3(file: File): Sequence<BedEntry> = BufferedReader(FileReader(file)).lineSequence()
    .filterNot { it.startsWith("#") }
    .map { it.toBed3() }
    .filterNotNull()

fun readBed6(file: File): Sequence<BedEntry> = BufferedReader(FileReader(file)).lineSequence()
    .filterNot { it.startsWith("#") }
    .map { it.toBed6() }
    .filterNotNull()

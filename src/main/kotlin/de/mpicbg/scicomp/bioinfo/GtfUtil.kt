package de.mpicbg.scicomp.bioinfo

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * @author Holger Brandl
 */

/**
 * A record in a gtf file
For gtf specs see http://www.ensembl.org/info/website/upload/gff.html

- seqname - name of the chromosome or scaffold; chromosome names can be given with or without the 'chr' prefix. Important note: the seqname must be one used within Ensembl, i.e. a standard chromosome name or an Ensembl identifier such as a scaffold ID, without any additional content such as species or assembly. See the example GFF output below.
- source - name of the program that generated this feature, or the data source (database or project name)
- feature - feature type name, e.g. Gene, Variation, Similarity
- start - Start position of the feature, with sequence numbering starting at 1.
- end - End position of the feature, with sequence numbering starting at 1.
- score - A floating point value.
- strand - defined as + (forward) or - (reverse).
- frame - One of '0', '1' or '2'. '0' indicates that the first base of the feature is the first base of a codon, '1' that the second base is the first base of a codon, and so on..
- attribute - attribute - A semicolon-separated list of tag-value pairs, providing additional information about each feature.
 */
data class GtfRecord(val seqname: String, val source: String, val feature: String, val start: Long, val end: Long, val score: String, val strand: String, val frame: String, val attributes: Map<String, String>) {


    override fun toString(): String {
        val stringifiedAttrs = attributes.map { it.key + " \"${it.value}\"" }.joinToString("; ")
        return listOf(seqname, source, feature, start, end, score, strand, frame, stringifiedAttrs).joinToString("\t")
    }
}


//fun readGTF(file: File): Sequence<GtfRecord> = file.readLines().map{ line->
fun readGTF(file: File): Sequence<GtfRecord> = BufferedReader(FileReader(file)).lineSequence().filterNot { it.startsWith("#") }.map { line ->
    val splitLine = line.split("\t")

    val attributeStrings = splitLine[8].split(";").map { it.trim() }.filter { it.isNotBlank() }
    val attributes = attributeStrings.map { keyVal -> keyVal.split(" ", limit = 2).let { it[0] to it[1].trimEnd('\"').trimStart('\"') } }.toMap()

    splitLine.run { GtfRecord(this[0], this[1], this[2], this[3].toLong(), this[4].toLong(), this[5], this[6], this[7], attributes) }
}


fun main(args: Array<String>) {
    val records = readGTF(File("/Users/brandl/projects/kotlin/kutils/src/test/resources/FBgn0012034.gtf")).toList()

    println(records.first())

    println(records.size)
}
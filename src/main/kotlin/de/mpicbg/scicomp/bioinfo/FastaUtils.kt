package de.mpicbg.scicomp.bioinfo

import de.mpicbg.scicomp.kutils.buffered
import de.mpicbg.scicomp.kutils.saveAs
import java.io.File
import java.nio.file.Files
import java.util.*

/**
 * Fasta Utilities: Reading, writing and chunking of fasta-files
 *
 * @author Holger Brandl
 */


/** From http://codeaffectionate.blogspot.de/2013/05/reading-fasta-files-with-scala.html */
fun openFasta(fastaFile: String): Iterable<FastaRecord> = openFasta(File(fastaFile))


fun openFasta(fastaFile: File): Iterable<FastaRecord> {
    return BufferedFastaReader(fastaFile).asSequence().asIterable()
}

fun write(fastaRecords: Iterable<FastaRecord>, outputFile: File) {
    fastaRecords.saveAs(outputFile, transform = { it.toEntryString() })
}

// how to prevent that the iterator progresses when using it --> use iterable

// http://stackoverflow.com/questions/19804928/scala-writing-string-iterator-to-file-in-efficient-way
fun Iterable<FastaRecord>.writeFasta(outputFile: File) = write(this, outputFile)

fun Iterable<FastaRecord>.writeFasta(outputFile: String) = write(this, File(outputFile))


//    http://stackoverflow.com/questions/12105130/generating-a-frequency-map-for-a-string-in-scala
fun Iterable<FastaRecord>.letterStats(): Map<Char, Int> {
    // fixme this is super-inefficent for larger files
    return this.map { it.sequence }.
            joinToString("").toCharArray().toList().
            groupBy { it }.
            map { Pair(it.key, it.value.size) }.toMap()
}


fun Iterable<FastaRecord>.gcContent(): Double {
    val stats: Map<Char, Int> = this.letterStats()
    return (stats['G']!! + stats['C']!!).toDouble() / stats.values.sum()
}


data class FastaRecord(val id: String, val description: String? = null, val sequence: String) {

    //    val toEntryString: String by lazy {
    fun toEntryString(lineLength: Int = 100): String {
        // inspired from see https://github.com/agjacome/funpep
        // also interesting
        // see http://stackoverflow.com/questions/10530102/java-parse-string-and-add-line-break-every-100-characters

//        val wrappedSeq = sequence.toCharArray().toList().batch(70).map{ it.joinToString("")  }.joinToString ("\n" )

        // faster impl
        val wrappedSeq = StringBuilder(sequence.length)
        var index = 0
        while (index < sequence.length) {
            wrappedSeq.append(sequence.substring(index, Math.min(index + lineLength, sequence.length)) + "\n")
            index += lineLength;
        }

        return ">" + (id + " " + (description ?: "")).trim() + "\n" + wrappedSeq
    }

    override fun toString(): String = toEntryString()
}

class FastaRecordSerializer(val lineLength: Int = 100) : ItemSerializer<FastaRecord> {

    override fun serialize(record: FastaRecord): String = record.toEntryString(lineLength)
}


internal class BufferedFastaReader(val file: File) : Iterator<FastaRecord> {

    private val fileIt by lazy { Files.lines(file.toPath()).filter { !it.isEmpty() }.iterator().buffered() }

    override fun hasNext() = fileIt.hasNext()

    override fun next(): FastaRecord {
        val tag = fileIt.next()!! // Read the tag line

        if (tag[0] != '>') throw  FastaReadException("record start expected")

        val sb: StringBuilder = StringBuilder()

        do {
            sb.append(fileIt.next())
        } while (fileIt.hasNext() && !fileIt.head()!!.startsWith(">"))

        // Remove prepending '>' and separate description from id
        val header = tag.drop(1).trim().split("\\s".toRegex(), 2)
        val id = header.get(0)
        val desc = if (header.size == 2) header.drop(1).joinToString(" ") else null

        // todo use string-builder directly in FastaRecord to speed up fasta-processing
        return FastaRecord(id, desc, sb.toString())
    }
}


fun Iterable<FastaRecord>.createSizeBalancedChunks(maxChunkSizeKB: Int, namer: ChunkNamer = SimpleChunkNamer()): List<File> {
    val sortedSeqs = sortedBy { it.sequence.length }.toMutableList()

    val chunkFiles: MutableList<File> = emptyList<File>().toMutableList()

    while (sortedSeqs.isNotEmpty()) {
        val longSeq = sortedSeqs.removeAt(0)

        val chunkList = mutableListOf(longSeq)
        var cumChunkSize = longSeq.sequence.length

        while (sortedSeqs.isNotEmpty() && cumChunkSize < maxChunkSizeKB * 1000) {
            val shortSeq = sortedSeqs.removeAt(sortedSeqs.size - 1)

            cumChunkSize += shortSeq.sequence.length
            chunkList.add(shortSeq)
        }

        // write chunk file
        val nextChunkFile = namer.getNext()

        if (!nextChunkFile.parentFile.exists()) nextChunkFile.parentFile.mkdir()
        require(!nextChunkFile.exists()) { "$nextChunkFile is already present" } // make sure that we do not override existing chunk files

        chunkList.writeFasta(nextChunkFile)
        chunkFiles.add(nextChunkFile)
    }

    return chunkFiles
}

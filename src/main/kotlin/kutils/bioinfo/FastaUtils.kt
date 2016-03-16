package kutils.bioinfo

import kutils.batch
import kutils.buffered
import kutils.div
import kutils.saveAs
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


fun writeFasta(fastaRecords: Iterable<FastaRecord>, outputFile: File) {
    fastaRecords.saveAs(outputFile, transform = { it.toEntryString })
}



// how to prevent that the iterator progresses when using it --> use iterable

// http://stackoverflow.com/questions/19804928/scala-writing-string-iterator-to-file-in-efficient-way
fun Iterable<FastaRecord>.write(outputFile: File) = writeFasta(this, outputFile)


fun Iterable<FastaRecord>.shuffle() = Collections.shuffle(this.toList())


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

    val toEntryString: String by lazy {
        // inspired from see https://github.com/agjacome/funpep
        // also interesting
        // see http://stackoverflow.com/questions/10530102/java-parse-string-and-add-line-break-every-100-characters

        val wrappedSeq = sequence.toCharArray().toList().batch(70).map { it.joinToString("\n") }
        ">" + id + " " + (description ?: "") + "\n" + wrappedSeq + "\n"
    }
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
        val header = tag.drop(1).trim().split("\\s+")
        val id = header.get(0)
        val desc = if (header.size == 2) header.drop(1).joinToString(" ") else null

        // todo use string-builder directly in FastaRecord to speed up fasta-processing
        return FastaRecord(id, desc, sb.toString())
    }
}

class FastaReadException(string: String) : Exception(string)


//
// Chunks
//


interface ChunkNamer {
    fun getNext(): File
}


data class SimpleChunkNamer(val baseDir: File = File("fasta_chunks"), val prefix: String = "chunk_") : ChunkNamer {


    var chunkCounter = 0


    override fun getNext(): File {
        chunkCounter = chunkCounter + 1
        return baseDir / (prefix + chunkCounter + ".fasta")
    }


    /**
     * List existing chunks
     */
    fun list(): List<File> {
        val copyNamer = this.copy()
        var chunkFile = copyNamer.getNext()

        var chunks = emptyList<File>().toMutableList()
        while (chunkFile.exists()) {
            chunks.add(chunkFile)
            chunkFile = copyNamer.getNext()
        }

        return chunks
    }
}


/** Inspired by http://stackoverflow.com/questions/7459174/split-list-into-multiple-lists-with-fixed-number-of-elements. */
// todo use proper api instead of static method to create chunks
fun Iterable<FastaRecord>.createChunks(chunkSize: Int, namer: ChunkNamer = SimpleChunkNamer()): Sequence<File> {

    return this.batch(chunkSize).map {
        val nextChunkFile = namer.getNext()
        if (!nextChunkFile.parentFile.exists()) nextChunkFile.parentFile.mkdir()

        require(!nextChunkFile.exists()) { "$nextChunkFile is already present" } // make sure that we do not override existing chunk files

        writeFasta(it, nextChunkFile)

        nextChunkFile
    }
}

package de.mpicbg.scicomp.bioinfo

import de.mpicbg.scicomp.kutils.buffered
import de.mpicbg.scicomp.kutils.saveAs
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.util.*
import java.util.zip.GZIPInputStream

class FastaReadException(string: String) : Exception(string)

/**
 * Fasta Utilities: Reading, writing and chunking of fasta-files
 *
 * @author Holger Brandl
 */

fun main(args: Array<String>) {

    //        http://stackoverflow.com/questions/7175893/decompressing-files-using-nio
    val zipFile = "/Users/brandl/projects/kotlin/kutils/src/test/resources/exampleFASTQ_L001_R1_file.fastq.gz"
    //    val fc = RandomAccessFile(zipFile, "r").channel
    //        ReadableByteChannel gzc = Channels.newChannel(new GZIPInputStream(Channels.newInputStream(fc)));
    //        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(Channels.newInputStream(fc))));
    val bufferedReader = BufferedReader(InputStreamReader(GZIPInputStream(FileInputStream(zipFile))))
    val lines = bufferedReader.lines()
    lines.forEach { line -> println(line) }

    // better use nio2 to iterate over the zip file
    // http://stackoverflow.com/questions/9394098/writing-gzip-file-with-nio
    // http://www.adam-bien.com/roller/abien/entry/listing_directory_contents_with_jdk
    // http://fahdshariff.blogspot.de/2011/08/java-7-working-with-zip-files.html


    val reads = openFastq("/Users/brandl/projects/kotlin/kutils/src/test/resources/exampleFASTQ_L001_R1_file.fastq")
    for (read in reads) {
        //        println(read.id)
        println(read.sequence)
    }
}


/** From http://codeaffectionate.blogspot.de/2013/05/reading-fasta-files-with-scala.html */
fun openFastq(fastqFile: String): Iterable<FastqRecord> = openFastq(File(fastqFile))


fun openFastq(fastqFile: File): Iterable<FastqRecord> {
    return BufferedFastqReader(fastqFile).asSequence().asIterable()
}


fun writeFasta(fastaRecords: Iterable<FastqRecord>, outputFile: File) {
    fastaRecords.saveAs(outputFile, transform = { it.toEntryString() })
}


// how to prevent that the iterator progresses when using it --> use iterable
// http://stackoverflow.com/questions/19804928/scala-writing-string-iterator-to-file-in-efficient-way
@Suppress("unused")
fun Iterable<FastqRecord>.write(outputFile: File) = writeFasta(this, outputFile)


fun Iterable<FastqRecord>.shuffle() = Collections.shuffle(this.toList())


//    http://stackoverflow.com/questions/12105130/generating-a-frequency-map-for-a-string-in-scala
fun Iterable<FastqRecord>.letterStats(): Map<Char, Int> {
    // fixme this is super-inefficent for larger files
    return this.map { it.sequence }.
            joinToString("").toCharArray().toList().
            groupBy { it }.
            map { Pair(it.key, it.value.size) }.toMap()
}


fun Iterable<FastqRecord>.gcContent(): Double {
    val stats: Map<Char, Int> = this.letterStats()
    return (stats['G']!! + stats['C']!!).toDouble() / stats.values.sum()
}


class FastqRecord(val id: String, val sequence: String, val qualString: String) : EntryStringable {

    override fun toEntryString(): String {
        return id + "\n" + sequence + "\n+\n" + qualString + "\n"
    }

    override fun toString(): String = toEntryString()
}


internal class BufferedFastqReader(val file: File) : Iterator<FastqRecord> {

    private val fileIt by lazy { Files.lines(file.toPath()).filter { !it.isEmpty() }.iterator().buffered() }

    override fun hasNext() = fileIt.hasNext()

    override fun next(): FastqRecord {
        val id = fileIt.next()!! // Read the tag line
        val readSeq = fileIt.next()
        if (fileIt.next() != "+") throw  FastaReadException("invalid record")
        val qualString = fileIt.next()

        assert(readSeq!!.length == qualString!!.length)

        return FastqRecord(id, readSeq, qualString)
    }
}

class FastqReadException(string: String) : Exception(string)


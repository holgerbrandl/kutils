package de.mpicbg.scicomp.bioinfo

import de.mpicbg.scicomp.kutils.batch
import de.mpicbg.scicomp.kutils.div
import de.mpicbg.scicomp.kutils.saveAs
import java.io.File


interface EntryStringable {
    fun toEntryString(): String
}

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

interface ItemSerializer<T> {
    fun serialize(record: T): String
}

class ToStringSerializer<T> : ItemSerializer<T> {
    override fun serialize(record: T): String {
        return record.toString()
    }
}

/** Inspired by http://stackoverflow.com/questions/7459174/split-list-into-multiple-lists-with-fixed-number-of-elements. */
// todo use proper api instead of static method to create chunks
fun <T> Iterable<T>.createChunks(chunkSize: Int, namer: ChunkNamer = SimpleChunkNamer(), serializer: ItemSerializer<T> = ToStringSerializer()): Sequence<File> {

    return this.batch(chunkSize).map {
        val nextChunkFile = namer.getNext()
        if (!nextChunkFile.parentFile.exists()) nextChunkFile.parentFile.mkdir()

        require(!nextChunkFile.exists()) { "$nextChunkFile is already present" } // make sure that we do not override existing chunk files

        it.saveAs(nextChunkFile, transform = { serializer.serialize(it) })

        nextChunkFile
    }
}

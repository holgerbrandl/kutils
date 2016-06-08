package kutils

import kutils.bioinfo.openFasta
import kutils.bioinfo.write
import java.io.File
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val fastaFile = File("/Volumes/furiosa/plantx/smed_genome/schMed2/renamed_assemblies/test_assemby.fasta")
    val outputFile = File("/Users/brandl/Desktop/resort.fasta")
    outputFile.delete()


    // rewrite
    openFasta(fastaFile)
            .sortedBy { -1 * it.sequence.length }
            .mapIndexed { index, fr ->
                val newId = "dd_Smes_G2_${index + 1}"
                println(newId + "\t" + fr.sequence.length + "\t" + fr.id)

                fr.copy(id = newId)
            }.write(outputFile)
//            .forEach { println(it.toEntryString()) }


//    data class SeqLength(val id: String, val length: Int)

    // lazy approach but does not create a new file
//    val sortedIds = openFasta(fastaFile).asSequence()
//            .map { SeqLength(it.id, it.sequence.length) }
//            .sortedBy { it.length }

    exitProcess(0)

}
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



    // lazy approach but does not create a new file
    val sortedIds = openFasta(fastaFile).asSequence()
            .map { it.id to it.sequence.length }
            .sortedByDescending { it.second }
            .mapIndexed { index, id2length ->
                id2length.first to "dd_Smes_G2_${index + 1}"
            }.toMap()

    // rescan the fasta and replace IDs
    openFasta(fastaFile).asSequence().map { it.copy(id = sortedIds[it.id]!!) }.asIterable().write(outputFile)

    // print mapping as reference
    sortedIds.forEach { println(it.key + "\t" + it.value) }

    exitProcess(0)

}
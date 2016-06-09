package kutils.scratch

import kutils.bioinfo.openFasta
import kutils.bioinfo.write
import java.io.File

val fastaFile = File(args[0])
val outputFile = File(args[1])

// lazy approach but does not create a new file
val sortedIds = openFasta(fastaFile).asSequence()
        .map { it.id to it.sequence.length }
        .sortedByDescending { it.second }
        .mapIndexed { index, id2length ->
            id2length.first to "dd_Smes_G2_${index + 1}"
        }.toMap()

// rescan the fasta and replace IDs
openFasta(fastaFile).asSequence()
        .map { it.copy(id = sortedIds[it.id]!!, description = "") }
        .asIterable().write(outputFile)

// print mapping as reference
sortedIds.forEach { println(it.value + "\t" + it.key) }
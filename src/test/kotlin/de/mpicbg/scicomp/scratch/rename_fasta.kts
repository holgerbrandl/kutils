package de.mpicbg.scicomp.scratch

import de.mpicbg.scicomp.bioinfo.openFasta
import de.mpicbg.scicomp.bioinfo.writeFasta
import java.io.File


val fastaFile = File(args[0])
val assemblyName = args[1]
val outputFile = File(assemblyName + ".fasta")

// lazy approach but does not create a new file
val sortedIds = openFasta(fastaFile).asSequence()
        .map { it.id to it.sequence.length }
        .sortedByDescending { it.second }
        .mapIndexed { index, id2length ->
            id2length.first to (assemblyName + "_{index + 1}")
        }.toMap()

// rescan the fasta and replace IDs
openFasta(fastaFile).asSequence()
        .map { it.copy(id = sortedIds[it.id]!!, description = "") }
        .asIterable().writeFasta(outputFile)

// print mapping as reference
sortedIds.forEach { println(it.value + "\t" + it.key) }
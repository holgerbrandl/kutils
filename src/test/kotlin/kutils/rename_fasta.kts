import kutils.bioinfo.openFasta
import kutils.bioinfo.write
import java.io.File

val fastaFile = File(args[0])
val outputFile = File(args[1])

// rewrite
openFasta(args[0])
        .sortedBy { -1 * it.sequence.length }
        .mapIndexed { index, fr ->
            val newId = "dd_Smes_G2_${index + 1}"
            println(newId + "\t" + fr.sequence.length + "\t" + fr.id)

            fr.copy(id = newId)
        }.write(File(args[1]))

import de.mpicbg.scicomp.bioinfo.toBed3
import java.io.BufferedReader
import java.io.FileReader

val lines = BufferedReader(FileReader("data.bed")).lineSequence()
// bed example
val chrRenameModel = mapOf("chr3" to "3", "MT" to "M")

lines.map { it.toBed3() }.filterNotNull().
        // replace chromsome name if it is in renaming model
        map { chrRenameModel[it.chromsome]?.let { newName -> it.copy(chromsome = newName) } }.
        map { println(it) }
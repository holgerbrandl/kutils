//kotlinc -cp $(expandcp.kts de.mpicbg.scicomp:kutils:0.4-SNAPSHOT)
//DEPS de.mpicbg.scicomp:kutils:0.3
//KOTLIN_OPTS -J-Xmx5g

import de.mpicbg.scicomp.bioinfo.*
import java.io.File
import kotlin.system.exitProcess

//val inputFasta = File("dd_Dca1_v1.prot_no_stop.fasta");
val inputFasta = File("/Volumes/furiosa/plantx/inprogress/fail/en_Sman_v1/en_Sman_v1.fasta");
val fastaBaseName = inputFasta.nameWithoutExtension

if (!inputFasta.isFile || inputFasta.length() == 0L) {
    println("Input fasta $inputFasta is missing or empty")
    exitProcess(-1)
}


// shuffle sequences
println("shuffling '$fastaBaseName' for better load balance")
val shuffledFasta = "$fastaBaseName.shuffled.fasta"

openFasta(inputFasta).shuffle().writeFasta(File(shuffledFasta))

// create chunks
print("chunking '${inputFasta}' for better load balance")

val chunks = openFasta(shuffledFasta).createChunks(
        chunkSize = 100,
        namer = SimpleChunkNamer(baseDir = File("fixed_chunks")),
        serializer = FastaRecordSerializer()
).toList()


val balancedChunks = openFasta(shuffledFasta).createSizeBalancedChunks(
        maxChunkSizeKB = 200,
        namer = SimpleChunkNamer(baseDir = File("balanced_chunks"))
)

"""
cd ~/Desktop/
cat balanced_chunks/* | grep -c ">"
cat fixed_chunks/* | grep -c ">"

cat balanced_chunks/* | wc -c
cat fixed_chunks/*    | wc -c

"""


// eval size distribution in R
"""

"""


package de.mpicbg.scicomp.bioinfo

import de.mpicbg.scicomp.kutils.MicroBenchmark
import java.io.File

object AssemblyFiltering {

    @JvmStatic
    fun main(args: Array<String>) {
        val mb = MicroBenchmark<String>()

        mb.warmupReps = 1
        mb.reps = 1

        mb.elapseNano {

            // eg. /Volumes/furiosa/bioinfo/igenomes/Mus_musculus/Ensembl_v92_custom/GRCm38_p6/Sequence/WholeGenomeFasta/
            openFasta(File("Mus_musculus.GRCm38.dna.primary_assembly.fa"))
                .filterNot { it.description!!.contains("dna:scaffold") }
                .writeFasta("~/Desktop/foo.fastq.gz")

        }.printSummary()
    }
}


object FastaShuffling {
    @JvmStatic
    fun main(args: Array<String>) {
        val mb = MicroBenchmark<String>()

        mb.warmupReps = 3
        mb.reps = 5

        mb.elapseNano {
            // [Desktop]$ wc -l dd_Smes_v99.prot_no_stop.fasta
            // 18305396 dd_Smes_v99.prot_no_stop.fasta
            //
            // head -n 10000000 dd_Smes_v99.prot_no_stop.fasta >  some_dd_smes.fasta
            //        openFasta("/Users/brandl/Desktop/ some_dd_smes.fasta").map{it.id}.count()
            //        openFasta("/Users/brandl/Desktop/some_dd_smes.fasta")
            openFasta("/Users/brandl/Desktop/dd_Smes_v99.prot_no_stop.fasta")
                .shuffled()
                .also { println(it.count()) }
                .writeFasta("shuffled.fasta")

            //        head -n 5000000 dd_Smes_v99.prot_no_stop.fasta >  some_dd_smes.fasta
            //        old records:	   mean=3.44597686592	  sd=1.9172049999406132E8


        }.printSummary()
    }
}
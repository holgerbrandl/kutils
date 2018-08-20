@file:DependsOn("de.mpicbg.scicomp:kutils:0.9.0")

import de.mpicbg.scicomp.bioinfo.openFasta
import java.io.File

// wget ftp://ftp.ensembl.org/pub/release-90/fasta/homo_sapiens/cds/Homo_sapiens.GRCh38.cds.all.fa.gz
// gunzip Homo_sapiens.GRCh38.cds.all.fa.gz
//val fastaFile = File("/Volumes/furiosa/bioinfo/brandl/misc/cnda/Homo_sapiens.GRCh38.cds.all.fa")
val fastaFile = File(args[0])

val txBiotypes = openFasta(fastaFile)
    //    .take(5)
    .map { it.description!!.split(" ").first { it.contains("transcript_biotype") } }
    .distinct()
    .map { it.split(":")[1] }

println(txBiotypes)
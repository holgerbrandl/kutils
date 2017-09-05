#!/usr/bin/env kscript

//DEPS de.mpicbg.scicomp:kutils:0.8.3

import de.mpicbg.scicomp.bioinfo.igv.builSession
import de.mpicbg.scicomp.bioinfo.igv.guessTracks
import java.io.File
import kotlin.system.exitProcess

/**
 * Programmatically create an IGV session file.
 */

if (args.isEmpty()) {
    System.err.println("Usage: make_igv_session <genome_id_or_fasta> [<bam_sam_bed_bw_vcf_gff)file>]* ]")
    exitProcess(1)
}

// Example
//val args = arrayOf("hg19", "/some/where/N.bam", "/some/where/aRG.bam", "/some/where/bRG.bam")

val genome = args.first()
val trackFiles = args.drop(1).map { File(it) }

// guess track types
val tracks = guessTracks(trackFiles)

// rather print to stdut to allow easy post-processing
builSession(genome, tracks).let { println(it) }


// /Users/brandl/IdeaProjects/kotlin_playground/src/make_igv_session.kts/make_igv_session.kts





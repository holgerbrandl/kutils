#!/usr/bin/env kscript

//DEPS de.mpicbg.scicomp:kutils:0.6.1

import de.mpicbg.scicomp.bioinfo.igv.builSession
import de.mpicbg.scicomp.bioinfo.igv.guessTracks
import java.io.File
import kotlin.system.exitProcess

/**
 * Programmatically create an IGV session file.
 */

if (args.isEmpty()) {
    System.err.println("Usage: make_igv_session <genome_id_or_fasta> [<bam_file>]* ]")
    exitProcess(1)
}

// Example
//val args = arrayOf("hg19", "/some/where/N.bam", "/some/where/aRG.bam", "/some/where/bRG.bam")


val genome = args.first()
val trackFiles = args.drop(1).map { File(it) }

// validate files
if (trackFiles.any { !it.exists() }) {
    val missingTrackFiles = trackFiles.filter { !it.exists() }
    System.err.println("Error: Some track files were missing:\n${missingTrackFiles.joinToString { "\n" }}")
    exitProcess(1)
}


// TODO Do more input validation
// * sorsted beds
// * indexed bams
val missingIndexBams = trackFiles
        .filter { it.name.endsWith(".bam") }
        .filter { !File(it.absolutePath + ".bai").exists() }

if (!missingIndexBams.isEmpty()) {
    System.err.println("Error: Bam indices are missing for the following files but are required by IGV:\n" +
            missingIndexBams.joinToString("\n"))
    exitProcess(1)
}


// guess track types
val tracks = guessTracks(trackFiles)

// rather print to stdut to allow easy post-processing
builSession(genome, tracks).let { println(it) }


// /Users/brandl/IdeaProjects/kotlin_playground/src/make_igv_session.kts/make_igv_session.kts





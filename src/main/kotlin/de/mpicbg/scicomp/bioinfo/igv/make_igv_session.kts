#!/usr/bin/env kscript

package de.mpicbg.scicomp.bioinfo.igv

import java.io.File


/**
 * Programmatically create an IGV session file.
 */


// cd /Volumes/RADseq-planarian/radseq/dd_Smes_g2_mapping; export PATH=/Users/brandl/IdeaProjects/kotlin_playground/src:${PATH}
// chmod +x make_igv_session.kts
// make_igv_session.kts

// configure properties of igv session
val genome = "/Volumes/RADseq-planarian/radseq/dd_Smes_g2_mapping/genome/dd_Smes_g2.fasta" // can also be valid identifier like mm10

//val tracks = listOf(BamTrack("SP37.bam"), BamTrack("SP90.bam"), BedTrack("uniq_var_sites_SP37.bed"), BedTrack("uniq_var_sites_SP90.bed"), BigWigTrack("SP37.bw"), BigWigTrack("SP90.bw"))
val tracks = guessTracks("my.bam your.bed".split(" ").map { File(it) })

// rather print to stdut to allow easy post-processing
builSession(genome, tracks).let { println(it) }


// test run
// /Users/brandl/IdeaProjects/kotlin_playground/src/make_igv_session.kts/make_igv_session.kts


#!/usr/bin/env kscript

@file:DependsOn("de.mpicbg.scicomp:kutils:0.9.0")

import de.mpicbg.scicomp.bioinfo.GtfRecord
import de.mpicbg.scicomp.bioinfo.readBed6
import java.io.File


//val args = arrayOf("../expression_analysis/herv_coord.bed")
require (args.size == 1 && File(args[0]).isFile()) { "Usage: bed2gtf <fasta>" }

val bedRecords = readBed6(File(args[0]))

val gtfRecords = bedRecords.map {
    GtfRecord(it.chromsome, "NA", "exon", it.start, it.end,
        strand = it.strand ?: ".", attributes = mapOf("locus_id" to it.name!!))
}

gtfRecords.forEach { println(it.toString()) }

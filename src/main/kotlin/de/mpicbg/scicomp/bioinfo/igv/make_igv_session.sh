


kscript - <<"EOF" > test_session.igv.xml
//DEPS de.mpicbg.scicomp:kutils:0.2-SNAPSHOT

import de.mpicbg.scicomp.bioinfo.igv.*

val genome = "/Volumes/RADseq-planarian/radseq/dd_Smes_g2_mapping/genome/dd_Smes_g2.fasta" // can also be valid identifier like mm10

// val tracks = listOf(BamTrack("SP37.bam"), BamTrack("SP90.bam"), BedTrack("uniq_var_sites_SP37.bed"), BedTrack("uniq_var_sites_SP90.bed"), BigWigTrack("SP37.bw"), BigWigTrack("SP90.bw"))
val tracks = guessTracks("SP37.bam","SP90.bam", "uniq_var_sites_SP37.bed", "uniq_var_sites_SP90.bed", "SP37.bw", "SP90.bw")

println(builSession(genome, tracks))
EOF


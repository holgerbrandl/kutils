## Misc bioinfo helpers that should be kotlinized (preferrable using kscript)

## Tophat Mapping Report from the logs
TophatMappingReport(){
echo '
devtools::source_url("https://raw.githubusercontent.com/holgerbrandl/datautils/v1.9/R/core_commons.R")
devtools::source_url("https://raw.githubusercontent.com/holgerbrandl/datautils/v1.9/R/ggplot_commons.R")


parseAlgnSummary_T2_0_11 <- function(alignSummary){
    #alignSummary="/projects/bioinfo/brandl/projects/marta_rnaseq/human_leipzig/mapping/S5382_aRG_1b_rep1/align_summary.txt"
    algnData <- readLines(alignSummary)

    data.frame(
        condition=basename(dirname(alignSummary)),
        num_reads=as.numeric(str_match(algnData[2], " ([0-9]*$)")[,2]),
        mapped_reads=as.numeric(str_match(algnData[3], ":[ ]*([0-9]*) ")[,2][1])
    ) %>% transform(mapping_efficiency=100*mapped_reads/num_reads)
}


algnSummary <- ldply(list.files(".", "align_summary.txt", full.names=TRUE, recursive=T), parseAlgnSummary_T2_0_11, .progress="text")
write.delim(algnSummary, file="tophat_mapping_stats.txt")

scale_fill_discrete <- function (...){ scale_color_brewer(..., type = "seq", palette="Set1", "fill", na.value = "grey50") }


projectName=basename(dirname(getwd()))

devtools::source_url("https://raw.githubusercontent.com/holgerbrandl/mdreport/master/R/mdreport-package.r")

md_new(paste("Mapping Summary for ", projectName))


md_plot(ggplot(algnSummary, aes(condition, mapping_efficiency)) + geom_bar(stat="identity") +coord_flip() + ylim(0,100) + ggtitle("mapping efficiency"))

md_plot(ggplot(algnSummary, aes(condition, num_reads)) + geom_bar(stat="identity") + coord_flip() + ggtitle("read counts") +scale_y_continuous(labels=comma))

md_plot(ggplot(algnSummary, aes(condition, mapped_reads)) + geom_bar(stat="identity") + coord_flip() + ggtitle("alignments counts") +scale_y_continuous(labels=comma))


#ggplot(melt(algnSummary), aes(condition, value)) + geom_bar(stat="identity") +facet_wrap(~variable, scales="free") + ggtitle("mapping summary") + scale_y_continuous(labels=comma)  + theme(axis.text.x=element_text(angle=90, hjust=0))
#ggsave2(w=10, h=10, p="mapstats")


md_report("tophat_mapping_report", open=F)
' | R -q --vanilla
}
export -f TophatMappingReport


## replaced with cs_bowtie_qc
#### Bowtie Mapping Report from the logs
#Bowtie2MappingReport(){
#
#echo '
#devtools::source_url("https://raw.githubusercontent.com/holgerbrandl/datautils/v1.9/R/core_commons.R")
#devtools::source_url("https://raw.githubusercontent.com/holgerbrandl/datautils/v1.9/R/ggplot_commons.R")
#
#logSuffix=".logs"
#parseAlgnSummary <- function(alignSummary){
#    #alignSummary="./H2Az_Rep1_Lane1_Lib4454.bowtie.log"
#    algnData <- readLines(alignSummary)
#
#    data.frame(
#        condition=trimEnd(basename(alignSummary), logSuffix),
#        num_reads=as.numeric(str_split_fixed(algnData[3], " ", 2)[1]),
#        unique_mappers=as.numeric(str_split_fixed(str_trim(algnData[6]), " ", 2)[1]),
#        mapping_efficiency=as.numeric(str_replace(str_split_fixed(algnData[8], " ", 2)[1], "%", "")),
#        multi_mappers=as.numeric(str_split_fixed(str_trim(algnData[7]), " ", 2)[1])
#    )
#}
#
#mapStats <- ldply(list.files(".", logSuffix, full.names=TRUE, recursive=T), parseAlgnSummary, .progress="text")
#write.delim(mapStats, file="mapStats.txt")
#
#ggplot(melt(mapStats), aes(condition, value)) + geom_bar(stat="identity") +facet_wrap(~variable, scales="free") + ggtitle("mapping summary") + scale_y_continuous(labels=comma)  + theme(axis.text.x=element_text(angle=90, hjust=0))
#ggsave2(w=10, h=10, p="mapstats")
#
#ggplot(mapStats, aes(condition, mapping_efficiency)) + geom_bar(stat="identity") +coord_flip() + ylim(0,100) + ggtitle("mapping efficiency")
#ggsave2(p="mapstats")
#ggplot(mapStats, aes(condition, num_reads)) + geom_bar(stat="identity") + coord_flip() + ggtitle("read counts")
#ggsave2(p="mapstats")
#
#ggplot(mapStats, aes(condition, unique_mappers)) + geom_bar(stat="identity") + coord_flip() + ggtitle("unique alignment") + scale_fill_discrete()
#ggsave2(p="mapstats")
#' | R --vanilla
#}
#export -f Bowtie2MappingReport



### Create a cuffdb on a network of lustre file-systen
MakeCuffDB() {
    if [ $# -ne 2 ]; then echo "Usage: MakeCuffDB <gtffile> <genomebuild>"; return; fi

echo '
devtools::source_url("https://raw.githubusercontent.com/holgerbrandl/datautils/v1.9/R/core_commons.R")

require.auto(cummeRbund)

createCuffDbTrickyDisk <- function(dbDir, gtfFile, genome, ...){
    tmpdir <- tempfile()
    system(paste("cp -r", dbDir, tmpdir))
    oldWD <- getwd()
    setwd(tmpdir)
    cuff <- readCufflinks(rebuild=T, gtf=gtfFile, genome=genome, ...)

    system(paste("cp cuffData.db", dbDir))
    system(paste("rm -r", tmpdir))

    setwd(oldWD)
    return(cuff)
}

#gtfFile="mm10_ensGene_pc.gtf"; genomeBuild="mm10"
gtfFile=commandArgs(TRUE)[1]
genomeBuild=commandArgs(TRUE)[2]

createCuffDbTrickyDisk(getwd(), gtfFile, genomeBuild)
' | R -q --no-save --no-restore  --args $1 $2
}
export -f MakeCuffDB

CountSeqs(){
    for fasta in $*; do
        grep ">" $fasta | wc -l | sed 's/^/'$fasta':\t/g'
    done
}
export -f CountSeqs


CountFastqGzReads() {
    zcat $1 | grep "^+$" | wc -l  | sed -e s/^/num_reads,/;
};
export -f CountFastqGzReads


IndexBams(){
    for bamFile in $@; do
        sem -j5 samtools index $bamFile;
    done
    sem -no-notice --wait
}
export -f IndexBams


##http://www.biostars.org/p/16471/
## estimate blast progress for fasta-query files. Result files are assumed to have the fasta id in column 1
BlastProgress(){
    if [ $# -eq 0 ]; then echo "Usage: BlastProgress <blast_query_fasta>+"; return; fi


   for query in $* ; do
        if [ ! -f $query ]; then
            >&2 echo "Error: $query is not a file"
            return;
        fi

        blast=$query.blast.out

        if [ ! -f $blast ]; then
            >&2 echo "Warning: Could not find blast output file '$blast'"
#            return;
        fi

    #    echo "the blast out is: "$blast
        #echo "the fasta query is: "$query

        #curquery=$(tail -1 $blast | cut -f 1)
        # http://tldp.org/LDP/abs/html/fto.html
        if [ -s $blast ]; then
    #        echo "file exists and has non-zero size"
            curquery=$(tail -1 $blast | cut -f 1)
            curline=$(grep -n $curquery"$" $query |  cut -f 1 -d ':')
        else
    #        echo "file does not yet exist or is empty"
            curline=0
        fi

        nblines=$(wc -l $query | cut -f 1 -d " ")
        percent=$(echo "($curline/$nblines) *100" | bc -l | cut -c 1-4)
        echo "Approximately $percent % of $query were processed."
    done
}
export -f BlastProgress


blast_progress(){
kscript - $* <<"EOF"
//DEPS de.mpicbg.scicomp:kutils:0.3
//KOTLIN_OPTS -J-Xmx5g

//kotlinc -J-Xmx5g -cp  $(expandcp.kts de.mpicbg.scicomp:kutils:0.3)

import de.mpicbg.scicomp.bioinfo.openFasta
import java.io.File
import kotlin.system.exitProcess

if(args.size == 0 ){
    System.err.println("Usage: blast_progres <fasta> <blastresults>")
    exitProcess(-1)
}

//args
val fastaFile= File(args[0])
val blastResults= File(args[1])

//val blastResults=File("dd_Smes_v1__vs__gencodeV25P.csv"); val fastaFile = File("../dd_Smes_v1.fasta")

val fastaIds = openFasta(fastaFile).map { it.id }

val procIds = blastResults.useLines { it.map{ it.split("\t")[0]}.distinct().toList()}

//todo part of kutils0.4
fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)


val pcDone = procIds.size.toDouble()/fastaIds.size
println("Approximately ${pcDone.format(2)} % of ${fastaIds.size} sequences were processed by blast.")
EOF
}



## just retains sequences whose id is in id-file (format: 1id per line)
FilterFastaByIDFile(){
python -c '
from Bio import SeqIO
import sys

#http://stackoverflow.com/questions/3925614/how-do-you-read-a-file-into-a-list-in-python
with open(sys.argv[1]) as f:
    some_list = f.read().splitlines()

for record in SeqIO.parse(sys.stdin, "fasta"):
#    recordID=record.description.split(" ")[1]
    recordID=record.id
#    print "processing" + recordID

    # http://stackoverflow.com/questions/3437059/does-python-have-a-string-contains-method
    if recordID in some_list: print record.format("fasta")
' $1;
}
export -f FilterFastaByIDFile

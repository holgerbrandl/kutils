#!/usr/bin/env bash

create_chunks(){
kscript - $@ <<"EOF"
//DEPS de.mpicbg.scicomp:kutils:0.4
import de.mpicbg.scicomp.bioinfo.*

if(args.size!=2){ System.err.println("Usage: create_chunks <fasta> <chunk_size_kb>"); kotlin.system.exitProcess(1); }

openFasta(args[0]).createSizeBalancedChunks(maxChunkSizeKB = args[1].toInt())
EOF
}


cd ~/Desktop
mkdir test
cd test
create_chunks ../en_Sman_v1.shuffled.fasta 200


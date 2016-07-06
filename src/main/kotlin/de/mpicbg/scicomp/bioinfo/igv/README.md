IGV Session File Generator
=========================


Generate igv session files using [kscript](https://github.com/holgerbrandl/kscript) and a little [support API](IgvSessionGen.kt) written in [Kotlin](https://kotlinlang.org/)

To get started setup []kscript and kotlin
```
## install kotlin via sdkman http://sdkman.io/
curl -s get.sdkman.io | bash
sdk install kotlin

## install kscript
curl -L -o ~/bin/kscript https://git.io/vaoNi && chmod u+x ~/bin/kscript
```


First Example assuming that you

```
kscript - <<"EOF" > test_session.igv.xml
//DEPS de.mpicbg.scicomp:kutils:0.2-SNAPSHOT

import de.mpicbg.scicomp.bioinfo.igv.*

val genome = "/Volumes/RADseq-planarian/radseq/dd_Smes_g2_mapping/genome/dd_Smes_g2.fasta" // can also be valid identifier like mm10

// just provide a list of igv-compatible files and let the support API do the rest using default track options
val tracks = guessTracks("SP37.bam","SP90.bam", "uniq_var_sites_SP37.bed", "uniq_var_sites_SP90.bed", "SP37.bw", "SP90.bw")

// manually instantiate tracks which gives more options to finetune their appearance
//val tracks = listOf(BamTrack("SP37.bam"), BamTrack("SP90.bam"), BedTrack("uniq_var_sites_SP37.bed"), BedTrack("uniq_var_sites_SP90.bed"), BigWigTrack("SP37.bw"), BigWigTrack("SP90.bw"))
println(builSession(genome, tracks))
EOF
```

This kscript is self-contained because `kscript` will fetch dependencies as needed and strictly versioned.

Another example that incoporates an existing list of v

ToDo
----

* Expose more track configuration via the API (e.g track collapse, visiblity windows, scaling)


Support
-------

Just submit tickets via the github tracker

# kutils - A collection of kotlin utilities

[ ![Download](https://api.bintray.com/packages/holgerbrandl/mpicbg-scicomp/kutils/images/download.svg) ](https://bintray.com/holgerbrandl/mpicbg-scicomp/kutils/_latestVersion)

## Installation

To use them add
```
compile 'de.mpicbg.scicomp:kutils:0.5'
```
to your dependencies.



## What's included?

* Utilities for [kscript](https://github.com/holgerbrandl/kscript)
* Fasta and Fastq API (read, write, iterators)
* [Parallel collections](src/main/kotlin/de/mpicbg/scicomp/kutils/ParCollections.kt) API
* Bash integration [utilities](src/main/kotlin/de/mpicbg/scicomp/kutils/Bash.kt)
* IGV session file [generator API](src/main/kotlin/de/mpicbg/scicomp/bioinfo/igv)

## Questions & Comments

Feel welcome to flood the isssue tracker with requests.

## Examples

Most examples rely on [kscript](https://github.com/holgerbrandl/kscript) for shell integration

Filter a fasta file with a provided set of ids:
```bash
kscript - id.lst some.fasta <<"EOF"
// DEPS de.mpicbg.scicomp:kutils:0.5
import de.mpicbg.scicomp.bioinfo.openFasta
import java.io.File

val filterIds = File(args[0]).readLines()

openFasta(args[1]).
        filter { !filterIds.contains(it) }.
        forEach { print(it.toEntryString()) }
EOF

```
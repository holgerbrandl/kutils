# kutils - A collection of kotlin utilities

[ ![Download](https://img.shields.io/badge/Maven%20Central-0.12-orange) ](https://mvnrepository.com/artifact/com.github.holgerbrandl/kutils)  [![Build Status](https://github.com/holgerbrandl/krangl/workflows/build/badge.svg)](https://github.com/holgerbrandl/kutils/actions?query=workflow%3Abuild)

## Installation

To use the library, simply add

```
compile 'com.github.holgerbrandl:kutils:0.12'
```

to the dependencies in your gradle-file.

## What's included?

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
// DEPS com.github.holgerbrandl:kutils:0.7

import de.mpicbg.scicomp.bioinfo.openFasta
import java.io.File

val filterIds = File(args[0]).readLines()

openFasta(args[1]).
        filter { !filterIds.contains(it.id) }.
        forEach { print(it.toEntryString()) }
EOF

```


## See also

* https://github.com/holgerbrandl/kscript-support-api

# kutils - A collection of kotlin utilities

[ ![Download](https://img.shields.io/badge/Maven%20Central-1.0-orange) ](https://mvnrepository.com/artifact/com.github.holgerbrandl/kutils)  [![Build Status](https://github.com/holgerbrandl/krangl/workflows/build/badge.svg)](https://github.com/holgerbrandl/kutils/actions?query=workflow%3Abuild)

## Installation

To use the library, simply add

```
implementation 'com.github.holgerbrandl:kutils:1.0'
```

as dependency in your gradle-file.

## What's included?

* [Parallel collections](src/main/kotlin/de/mpicbg/scicomp/kutils/ParCollections.kt) API
```kotlin
listOf(1,2,3).parmap(numThreads=4){ it + 5}
```

* Bash integration [utilities](src/main/kotlin/de/mpicbg/scicomp/kutils/Bash.kt)
```kotlin
evalBash("echo errtest", showOutput = true, wd = File(".."))
```
* Utils to capture and supress output
```
```

## Questions & Comments

Feel welcome to flood the issue tracker with requests.


## See also

* https://github.com/holgerbrandl/kscript-support-api

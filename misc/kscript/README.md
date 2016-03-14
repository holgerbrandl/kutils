## kscript - Having fun with Kotlin scripting


Easy-to-use scripting support for Kotlin on *nix-based systems.


## Installation

Simply download `kscript` (which [itself](kscript) is just 10 lines of bash) to your `~/bin` with:
```bash
curl -s https://raw.githubusercontent.com/holgerbrandl/kutils/master/misc/kscript/kscript > ~/bin/kscript && chmod u+x ~/bin/kscript
```

`kotlinc-jvm` is required to be in your `PATH`. It will be once you have [installed Kotlin](https://kotlinlang.org/docs/tutorials/command-line.html).

## Usage

Simple use `kscript` as interpreter in the shebang line of your Kotlin scripts:

```kotlin
#!/usr/bin/env kscript ""

println("Hello from Kotlin!")
for (arg in args) {
    println("arg: $arg")
}
```
Since no additional dependencies are required, the kscript argument is empty.

To specify dependencies simply use gradly-style locators. Multiple dependencies need to be separated by comma. Here's an example using [docopt](https://github.com/docopt/docopt.java) and [log4j](http://logging.apache.org/log4j/2.x/)

```kotlin
#!/usr/bin/env kscript org.docopt:docopt:0.6.0-SNAPSHOT,log4j:log4j:1.2.14

import org.docopt.Docopt
import java.util.*


val usage = """
Usage: cooltool <command> [options] [<input_file>]

Supported commands are
  submit    Submits a job to the underlying queuing system and adds it to the list
  add       Extracts job-ids from stdin and adds them to the list
  wait      Wait for a list of jobs to finish

If no args are provided cooltool is likely to do nothing
"""

val doArgs = Docopt(usage).parse(args.toList())

println("parsed args are: \n" + doArgs)

println("Hello from Kotlin!")
for (arg in args) {
    println("arg: $arg")
}
```


References
============

`kscript` is inspired (and bluntly borrows `mvncp` for dependency resolution) by [kotlin-script](https://github.com/andrewoma/kotlin-script) which is another great way to do scripting in Kotlin. `kotlin-script` has more options compared to `kscript`, but the latter is conceptually cleaner (no code wrapping) and more simplistic.

The major advantage of using `kotlin-script` over `kscript` is that the former will cache the compiled script whereas the latter does not.

`kscript` works better with Intellij as IDE, because extended multi-line shebang-headers are not (yet?) supported by Intellij (even if the kotlin-script parser seems to be able to handle them).  However in order to `mvncp` for dependency resolution, `kotlin script` relies on such mutli-line shebang headers (see [here](https://github.com/andrewoma/kotlin-script#mvncp)). In contrast, since `kscript` just works with just a standard shebang line, code parsing works very well in Intellij.











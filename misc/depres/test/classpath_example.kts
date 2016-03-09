#!/usr/bin/env kotlinc -script -classpath /Users/brandl/.m2/repository/org/docopt/docopt/0.6.0-SNAPSHOT/docopt-0.6.0-SNAPSHOT.jar

import org.docopt.Docopt
import java.util.*


val usage = """
Usage: jl <command> [options] [<joblist_file>]

Supported commands are
  submit    Submits a job to the underlying queuing system and adds it to the list
  add       Extracts job-ids from stdin and adds them to the list
  wait      Wait for a list of jobs to finish
  status    Prints various statistics and allows to create an html report for the list
  kill      Removes all  jobs of this list from the scheduler queue
  up        Moves a list of jobs to the top of a queue (if supported by the underlying scheduler)
  reset     Removes all information related to this joblist.

If no <joblist_file> is provided, jl will use '.jobs' as default
"""

val doArgs = Docopt(usage).
        parse(args.toList()).
        map { it ->
            it.key.removePrefix("--").replace("[<>]", "") to {
                if (it.value == null) null else Objects.toString(it.value)
            }
        }

println("parsed args are: \n" + doArgs)

println("Hello from Kotlin!")
for (arg in args) {
    println("arg: $arg")
}
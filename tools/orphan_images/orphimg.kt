#!/usr/bin/env kscript

@file:DependsOn("de.mpicbg.scicomp:kutils:0.9.0")
@file:DependsOn("com.xenomachina:kotlin-argparser:2.0.7")

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import java.io.File

class MyArgs(parser: ArgParser) {

    val autoRemove by parser.flagging("-a", "--auto", help = "Automatically remove orphan images")
    val imageDirectory by parser.positional("The directory which should be cleaned for orphan images")

//    val test by parser.storing("-t", argName = "foo", help = "Automatically remove orphan images").default("lala")

    val markdownFiles by parser.positionalList("Markdown files to be used as reference to detect orphaness")
}

fun main(args: Array<String>) {
    val args = mainBody { ArgParser(args).parseInto(::MyArgs) }

    // see also https://stackoverflow.com/questions/2056221/recursively-list-files-in-java
    val imageDir = File(args.imageDirectory)
    val imagesFiles = imageDir.listFiles { it -> listOf("jpg", "png").contains(it.extension) }

    // ingest all markdown files
    val mdContent = args.markdownFiles.flatMap { File(it).readLines() }

    // check for orphanness
    val orphans = imagesFiles.filter { imgFile ->
        val query = imageDir.name + "/" + imgFile.name

        mdContent.none {
            it.contains(query)
        }
    }

    println("Orphans are:")
    orphans.map { println(it) }

    if(args.autoRemove){
        print("Removing orphans...")
        orphans.forEach{it.delete()}
        println("Done")
    }
}

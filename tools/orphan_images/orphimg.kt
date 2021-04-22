#!/usr/bin/env kscript

@file:DependsOn("de.mpicbg.scicomp:kutils:0.9.0")
@file:DependsOn("com.xenomachina:kotlin-argparser:2.0.7")

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import java.io.File

class MyArgs(parser: ArgParser) {

    val autoRemove by parser.flagging("-a", "--auto", help = "Automatically remove orphan images")
    val fileNameOnly by parser.flagging("-f", "--file-only", help = "Just take the last path element (aka file name) for usage search")
    val recursive by parser.flagging("-r", "--recursive", help = "Recursively scan the target directory for image files")
    val imageDirectory by parser.positional("DIRECTORY", "The directory which should be cleaned for orphan images")

    val markdownFiles by parser.positionalList("MD-FILES", "Markdown files to be used as reference to detect orphaness")
}

fun main(_args: Array<String>) {
    val args = mainBody { ArgParser(_args).parseInto(::MyArgs) }

    fun File.isImageFile() = listOf("jpg", "png").contains(extension)

    // see also https://stackoverflow.com/questions/2056221/recursively-list-files-in-java
    val imageDir = File(args.imageDirectory)

    val imagesFiles = if (args.recursive) {
        imageDir.walkTopDown().maxDepth(3).filter { it.isImageFile() }
    } else {
        imageDir.listFiles { it -> it.isImageFile() }.asSequence()
    }


    // ingest all markdown files
    val mdContent = args.markdownFiles.flatMap { File(it).readLines() }

    // check for orphanness
    val orphans = imagesFiles.filter { imgFile ->
        val query = if (args.fileNameOnly) imgFile.name else imageDir.name + "/" + imgFile.name

        mdContent.none {
            it.contains(query)
        }
    }

    orphans.map { println(it) }

    if(args.autoRemove){
        print("Removing orphans...")
        orphans.forEach{it.delete()}
        println("Done")
    }
}


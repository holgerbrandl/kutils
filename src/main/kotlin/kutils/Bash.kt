package kutils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


data class BashResult(val exitCode: Int, val stdout: Iterable<String>, val stderr: Iterable<String>) {
    fun sout() = stdout.joinToString("\n").trim()

    fun serr() = stderr.joinToString("\n").trim()
}


fun bashEval(cmd: String): BashResult {

    try {
        val proc = Runtime.getRuntime().exec("/bin/bash", arrayOf("-c", cmd))

        val errorGobbler = StreamGobbler(proc.errorStream)

        // any output?
        val outputGobbler = StreamGobbler(proc.inputStream)

        // kick them off
        errorGobbler.start()
        outputGobbler.start()

        // any error???
        val exitVal = proc.waitFor()
        return BashResult(exitVal, outputGobbler.sb.lines(), errorGobbler.sb.lines())
    } catch (t: Throwable) {
        throw RuntimeException(t)
    }
}


internal class StreamGobbler(var inStream: InputStream) : Thread() {
    var sb = StringBuilder()

    override fun run() {
        try {
            val isr = InputStreamReader(inStream)
            val br = BufferedReader(isr)
            for (line in br.lines()) {
                sb.append(line!! + "\n")
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }


    val output: String get() = sb.toString()
}

fun main(args: Array<String>) {
    println("test")
}
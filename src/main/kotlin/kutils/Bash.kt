package kutils

import java.io.*


data class BashResult(val exitCode: Int, val stdout: Iterable<String>, val stderr: Iterable<String>) {
    fun sout() = stdout.joinToString("\n").trim()

    fun serr() = stderr.joinToString("\n").trim()
}


fun bashEval(cmd: String): BashResult {


    fun convertStreamToString(inStream: java.io.InputStream): String {
        val s = java.util.Scanner(inStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    try {

        var pb = ProcessBuilder("/bin/bash", "-c", cmd) //.inheritIO();
        pb.directory(File("."));
        var p = pb.start();

        val errorGobbler = StreamGobbler(p.getErrorStream())

        // any output?
        val outputGobbler = StreamGobbler(p.getInputStream())

        // kick them off
        errorGobbler.start()
        outputGobbler.start()

        // any error???
        val exitVal = p.waitFor()
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
    println(bashEval("which STAR"))
    println(bashEval("echo test"))
    println(bashEval("echo errtest >&2"))
}
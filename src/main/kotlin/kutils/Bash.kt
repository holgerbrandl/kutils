package kutils

import java.io.*
import kotlin.system.exitProcess


data class BashResult(val exitCode: Int, val stdout: Iterable<String>, val stderr: Iterable<String>) {
    fun sout() = stdout.joinToString("\n")
        .trim()

    fun serr() = stderr.joinToString("\n")
        .trim()
}


fun evalBash(cmd: String, showOutput: Boolean = false, wd: File? = null): BashResult {

    try {

        // optionally prefix script with working directory change
        val cmdWithWD = (if(wd != null) "cd '${wd.absolutePath}'\n" else "") + cmd


        val pb = ProcessBuilder("/bin/bash", "-c", cmdWithWD)
        if(showOutput) {
            pb.inheritIO()
        }
        pb.directory(File("."))
        val p = pb.start()

        val outputGobbler = StreamGobbler(p.inputStream)
        val errorGobbler = StreamGobbler(p.errorStream)

        // kick them off
        errorGobbler.start()
        outputGobbler.start()

        // any error???
        val exitVal = p.waitFor()
        return BashResult(exitVal, outputGobbler.sb.lines(), errorGobbler.sb.lines())
    } catch(t: Throwable) {
        throw RuntimeException(t)
    }
}


internal class StreamGobbler(var inStream: InputStream) : Thread() {
    var sb = StringBuilder()

    override fun run() {
        try {
            val isr = InputStreamReader(inStream)
            val br = BufferedReader(isr)
            for(line in br.lines()) {
                sb.append(line + "\n")
            }
        } catch(ioe: IOException) {
            ioe.printStackTrace()
        }
    }


    val output: String
        get() = sb.toString()
}


object ShellUtils {

    fun isInPath(tool: String) = evalBash("which $tool").sout()
        .trim()
        .isNotBlank()

    fun requireInPath(tool: String) = require(isInPath(tool)) { "$tool is not in PATH" }
}

inline fun stopIfNot(value: Boolean, lazyMessage: () -> Any) {
    if(!value) {
        System.err.println("[ERROR] " + lazyMessage().toString())
        exitProcess(1)
    }
}

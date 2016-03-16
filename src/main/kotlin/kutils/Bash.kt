package kutils

import java.io.*


data class BashResult(val exitCode: Int, val stdout: Iterable<String>, val stderr: Iterable<String>) {
    fun sout() = stdout.joinToString("\n").trim()

    fun serr() = stderr.joinToString("\n").trim()
}


fun evalBash(cmd: String, showOutput: Boolean = false,
             redirectStdout: File? = null, redirectStderr: File? = null, wd: File? = null): BashResult {

    try {

        // optionally prefix script with working directory change
        val cmd = (if (wd != null) "cd '${wd.absolutePath}'\n" else "") + cmd


        var pb = ProcessBuilder("/bin/bash", "-c", cmd) //.inheritIO();
        pb.directory(File("."));
        var p = pb.start();

        val outputGobbler = StreamGobbler(p.getInputStream(), if (showOutput) System.out else null)
        val errorGobbler = StreamGobbler(p.getErrorStream(), if (showOutput) System.err else null)

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


internal class StreamGobbler(var inStream: InputStream, val printStream: PrintStream?) : Thread() {
    var sb = StringBuilder()

    override fun run() {
        try {
            val isr = InputStreamReader(inStream)
            val br = BufferedReader(isr)
            for (line in br.lines()) {
                sb.append(line!! + "\n")
                printStream?.println(line)
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }


    val output: String get() = sb.toString()
}


class ShellUtils {

    fun isInPath(tool: String) = evalBash("which $tool").sout().trim().isNotBlank()


    fun requireInPath(tool: String) = require(isInPath(tool)) { "$tool is not in PATH" }

    fun mailme(subject: String, body: String = "", logSubject: Boolean = true) = {
        if (logSubject) println("$subject")

        // use sendmail by default for email reporting but support custom commands as well via a variable
        val defCmd = """echo -e 'Subject:$subject\n\n $body' | sendmail $$(whoami)@mpi-cbg.de > /dev/null"""

        val mailCmd = (System.getenv("MAILME_TEMPLATE") ?: defCmd).
                replace("${'$'}${'$'}BODY${'$'}${'$'}", body).
                replace("${'$'}${'$'}SUBJECT${'$'}${'$'}", subject)

        evalBash(mailCmd)
    }
}


fun main(args: Array<String>) {
    //    println("test")
    //    println(bashEval("which STAR"))
    //    println(bashEval("echo test"))
    //    println(bashEval("echo errtest >&2"))

    //    require(true){"test"}

    println(evalBash("echo errtest ; sleep 3; echo $(ls) ; sleep 3 ; echo end", showOutput = true))
}
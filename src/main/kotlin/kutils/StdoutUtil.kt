package kutils

import java.io.ByteArrayOutputStream
import java.io.PrintStream


data class CapturedOutput<T>(val result: T, val stdout: String, val stderr: String)

/** Capture stdout and stdin and return them along with the result of expr*/
fun <T> captureOutput(expr: () -> T): CapturedOutput<T> {
    val origOut = System.out
    val origErr = System.err

    // https://stackoverflow.com/questions/216894/get-an-outputstream-into-a-string

    val baosOut = ByteArrayOutputStream()
    val baosErr = ByteArrayOutputStream()

    System.setOut(PrintStream(baosOut));
    System.setErr(PrintStream(baosErr));


    // run the expression
    // run the expression
    val result = try {
        expr()
    } catch(t: Throwable) {
        System.setOut(origOut)
        System.setErr(origErr)

        throw t
    }
    val stdout = String(baosOut.toByteArray()).trim()
    val stderr = String(baosErr.toByteArray()).trim()

    System.setOut(origOut)
    System.setErr(origErr)

    return  CapturedOutput (result, stdout, stderr)
}

fun <T : Any> suppressOutput(expr: () -> T) = captureOutput(expr).result